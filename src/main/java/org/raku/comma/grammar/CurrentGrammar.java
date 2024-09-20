package org.raku.comma.grammar;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.Consumer;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.utils.RakuCommandLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CurrentGrammar {
    public static final Logger LOG = Logger.getInstance(CurrentGrammar.class);

    private final Project project;
    private final String grammarName;
    private final Document grammarDocument;
    private final DocumentListener grammarDocumentChangeListener;
    private final Document inputDocument;
    private final Consumer<? super ParseResultsModel> resultsUpdate;
    private final Runnable processing;
    private final ScheduledExecutorService debounceExecutor;

    private boolean currentlyRunning;
    private boolean needsAnotherRun;
    private long debounceCounter;
    private Process currentProcess;
    private boolean cancelled;

    public CurrentGrammar(RakuPackageDecl decl, Document input, Consumer<? super ParseResultsModel> resultsUpdateCallback,
                          Runnable processingCallback, ScheduledExecutorService timedExecutor) {
        project = decl.getProject();
        grammarName = decl.getPackageName();
        grammarDocument = decl.getContainingFile().getViewProvider().getDocument();
        grammarDocumentChangeListener = new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                scheduleUpdate();
            }
        };
        if (grammarDocument != null)
            grammarDocument.addDocumentListener(grammarDocumentChangeListener);
        inputDocument = input;
        resultsUpdate = resultsUpdateCallback;
        processing = processingCallback;
        debounceExecutor = timedExecutor;
    }

    public String getGrammarName() {
        return grammarName;
    }

    public synchronized void scheduleUpdate() {
        long target = ++debounceCounter;
        debounceExecutor.schedule(() -> scheduleIfNotBouncy(target), 1000, TimeUnit.MILLISECONDS);
    }

    private synchronized void scheduleIfNotBouncy(long got) {
        if (got != debounceCounter)
            return;
        if (currentlyRunning) {
            needsAnotherRun = true;
        }
        else {
            currentlyRunning = true;
            startUpdate();
        }
    }

    private synchronized void runningDone() {
        currentlyRunning = false;
        if (needsAnotherRun) {
            needsAnotherRun = false;
            scheduleUpdate();
        }
    }

    private void startUpdate() {
        Application application = ApplicationManager.getApplication();
        application.invokeAndWait(() -> application.runWriteAction(processing));
        application.runReadAction(() -> {
            String currentInput = inputDocument.getText();
            String grammarFileContent = grammarDocument.getText();
            application.executeOnPooledThread(() -> {
                File inputAsFile = null;
                File tweakedGrammarAsFile;
                try {
                    // Set up input file and tweaked grammar file to run with.
                    inputAsFile = writeToTempFile(currentInput);
                    if (inputAsFile == null)
                        return;
                    String tweakedGramamrFileContent = tweakGrammarFileContent(grammarFileContent, inputAsFile);
                    if (tweakedGramamrFileContent == null)
                        return;
                    tweakedGrammarAsFile = writeToTempFile(tweakedGramamrFileContent);
                    if (tweakedGrammarAsFile == null)
                        return;

                    // Run and get output.
                    RakuCommandLine cmd = new RakuCommandLine(project);
                    cmd.setWorkDirectory(project.getBasePath());
                    cmd.addParameter("-I.");
                    cmd.addParameter(tweakedGrammarAsFile.getAbsolutePath());
                    cancelled = false;
                    List<String> lines = executeAndRead(cmd, tweakedGrammarAsFile);

                    // Find the lines that we need (we ignore those before a marker, in
                    // case the user has added prints or whatever).
                    StringBuilder jsonLines = new StringBuilder();
                    boolean on = false;
                    for (String line : lines) {
                        if (on)
                            jsonLines.append(line);
                        else if (line.equals("___PARSER__OUTPUT__BEGINS__"))
                            on = true;
                    }
                    String jsonOutput = String.join("\n", jsonLines).trim();
                    updateUsing(currentInput, jsonOutput.startsWith("{")
                            ? jsonOutput
                            : (cancelled ? "{ \"e\": \"Cancelled\" }" : "{ \"e\": \"Failed to compile grammar\" }"));
                }
                catch (ExecutionException e) {
                    LOG.warn(e);
                    updateUsing(currentInput, "{ \"e\": \"Failed to run Raku to process grammar\" }");
                }
                finally {
                    if (inputAsFile != null && inputAsFile.exists())
                        inputAsFile.delete();
                    runningDone();
                }
            });
        });
    }

    @NotNull
    private List<String> executeAndRead(RakuCommandLine cmd, @Nullable File scriptFile) {
        List<String> results = new LinkedList<>();
        try {
            Process p = cmd.createProcess();
            currentProcess = p;
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))
            ) {
                String line;
                while ((line = reader.readLine()) != null)
                    results.add(line);
                if (p.waitFor() != 0) {
                    if (scriptFile != null)
                        scriptFile.delete();
                    return new ArrayList<>();
                }
            }
            catch (IOException e) {
                LOG.warn(e);
            }
        }
        catch (InterruptedException | ExecutionException e) {
            LOG.warn(e);
        }
        finally {
            currentProcess = null;
        }
        if (scriptFile != null)
            scriptFile.delete();
        return results;
    }

    public void cancel() {
        cancelled = true;
        Process process = currentProcess;
        process.destroyForcibly();
    }

    private static File writeToTempFile(String input) {
        try {
            File tempFile = FileUtil.createTempFile("comma", ".tmp");
            FileUtil.writeToFile(tempFile, input);
            return tempFile;
        }
        catch (IOException e) {
            LOG.warn(e);
            return null;
        }
    }

    private String tweakGrammarFileContent(String content, File inputFile) {
        String supportCode = getSupportCode();
        if (supportCode == null)
            return null;
        return content.replaceFirst("((our|my)\\s+)?grammar\\s+" + grammarName, "\\$GLOBAL::__GLP = $0") + ";\n" +
               supportCode +
               "\ntry { " +
               "$GLOBAL::__GLP" +
               ".parse(slurp(Q[[[" +
               inputFile.getAbsolutePath() +
               "]]])); CATCH { default { $error = $_ } } }\n";
    }

    @Nullable
    private String getSupportCode() {
        InputStream supportCodeStream = this.getClass().getClassLoader().getResourceAsStream("grammarLivePreview/setup.p6");
        if (supportCodeStream == null)
            return null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(supportCodeStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    private void updateUsing(String input, String json) {
        ParseResultsModel model = new ParseResultsModel(input, json);
        Application application = ApplicationManager.getApplication();
        application.invokeAndWait(() -> ApplicationManager.getApplication().runWriteAction(() -> resultsUpdate.consume(model)));
    }

    public void dispose() {
        grammarDocument.removeDocumentListener(grammarDocumentChangeListener);
    }
}
