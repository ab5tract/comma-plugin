package edument.perl6idea.grammar;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.utils.Perl6CommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CurrentGrammar {
    public static final Logger LOG = Logger.getInstance(CurrentGrammar.class);

    private final Project project;
    private final String grammarName;
    private final Document grammarDocument;
    private final DocumentListener grammarDocumentChangeListener;
    private final Document inputDocument;
    private boolean currentlyRunning;
    private boolean needsAnotherRun;

    public CurrentGrammar(Perl6PackageDecl decl, Document input) {
        project = decl.getProject();
        grammarName = decl.getPackageName();
        grammarDocument = decl.getContainingFile().getViewProvider().getDocument();
        grammarDocumentChangeListener = new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                scheduleUpdate();
            }
        };
        grammarDocument.addDocumentListener(grammarDocumentChangeListener);
        inputDocument = input;
    }

    public synchronized void scheduleUpdate() {
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
        application.runReadAction(() -> {
            String currentInput = inputDocument.getText();
            String grammarFileContent = grammarDocument.getText();
            application.executeOnPooledThread(() -> {
                try {
                    // Set up input file and tweaked grammar file to run with.
                    File inputAsFile = writeToTempFile(currentInput);
                    if (inputAsFile == null)
                        return;
                    String tweakedGramamrFileContent = tweakGrammarFileContent(grammarFileContent, inputAsFile);
                    if (tweakedGramamrFileContent == null)
                        return;
                    File tweakedGrammarAsFile = writeToTempFile(tweakedGramamrFileContent);
                    if (tweakedGrammarAsFile == null)
                        return;

                    // Run and get output.
                    Perl6CommandLine cmd = new Perl6CommandLine(project);
                    cmd.setWorkDirectory(project.getBasePath());
                    cmd.addParameter("-Ilib");
                    cmd.addParameter(tweakedGrammarAsFile.getAbsolutePath());
                    String jsonOutput = String.join("\n", cmd.executeAndRead());
                    System.err.println(jsonOutput);
                }
                catch (ExecutionException e) {
                    LOG.error(e);
                    // TODO report to user
                }
                finally {
                    runningDone();
                }
            });
        });
    }

    private File writeToTempFile(String input) {
        try {
            File tempFile = FileUtil.createTempFile("comma", ".tmp");
            FileUtil.writeToFile(tempFile, input);
            tempFile.deleteOnExit();
            return tempFile;
        }
        catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    private String tweakGrammarFileContent(String content, File inputFile) {
        String supportCode = getSupportCode();
        if (supportCode == null)
            return null;
        StringBuilder builder = new StringBuilder(content);
        builder.append(";\n");
        builder.append(supportCode.replace("__GRAMMAR_LIVE_PREVIEW_GRAMMAR_NAME__", grammarName));
        builder.append("\n");
        builder.append(grammarName);
        builder.append(".parse(slurp(Q[[[");
        builder.append(inputFile.getAbsolutePath());
        builder.append("]]]));\n");
        return builder.toString();
    }

    private String getSupportCode() {
        InputStream supportCodeStream = this.getClass().getClassLoader()
                .getResourceAsStream("grammarLivePreview/setup.p6");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(supportCodeStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    public void dispose() {
        grammarDocument.removeDocumentListener(grammarDocumentChangeListener);
    }
}
