package org.raku.comma.pm.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import net.miginfocom.swing.MigLayout;
import org.raku.comma.utils.zef.ZefCommandLine;
import org.raku.comma.utils.zef.ZefCommandLineOutputTextPane;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RakuPMWidget {
    private final static Logger LOG = Logger.getInstance(RakuPMWidget.class);
    public static final JTextPane outputPane = new JTextPane();
    private static ToolWindow myToolWindow;
    private static JComponent myComponent;

    public static int initAndRun(Project project, ZefCommandLine command, CompletableFuture<Integer> completion) {

        outputPane.setEditable(false);
        if (myComponent == null) {
            myComponent = new JPanel(new MigLayout());
            myComponent.add(new JBScrollPane(outputPane), "growx, growy, pushx, pushy");
        }
        ToolWindowManager twm = ToolWindowManager.getInstance(project);
        Supplier<String> supplier = () -> "Raku Package Management";
        if (myToolWindow == null) {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                myToolWindow = twm.registerToolWindow(new RegisterToolWindowTask(
                        "Raku PM Widget",
                        ToolWindowAnchor.BOTTOM,
                        myComponent,
                        false,
                        true,
                        false,
                        true,
                        null,
                        null,
                        supplier
                ));
            });
        }
        ApplicationManager.getApplication().invokeAndWait(() -> myToolWindow.activate(null, true));
        try {
            return ApplicationManager.getApplication().executeOnPooledThread(
                    () -> {
                        if (command.isSetup()) {
                            var status = executeProcess(command);
                            if (completion != null) {
                                completion.complete(status);
                            }
                            return status;
                        } else {
                            return -1;
                        }
                    }
            ).get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            LOG.warn(e);
        }
        return -1;
    }

    private static int executeProcess(ZefCommandLine command) {
        try {
            Process p = command.createProcess();
            var output = new ZefCommandLineOutputTextPane(outputPane);
            output.addFirst("> " + command.getCommandLineString());
            List<String> outputLines = new ArrayList<>();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) output.addText(line);
                output.addText(outputLines);
                outputLines.clear();
            } catch (IOException e) {
                LOG.warn(e);
            }

            var exitCode = p.waitFor();
            if (exitCode == 0) {
                output.addLast("DONE");
            } else {
                String line;
                try {
                    while ((line = errorReader.readLine()) != null) outputLines.add(line);
                    output.addText(outputLines);
                } catch (IOException e) {
                    LOG.warn(e);
                }
                output.addLast("ERROR (exit status: " + exitCode + ")");
            }

            return exitCode;
        } catch (ExecutionException | InterruptedException e) {
            LOG.warn(e);
        }
        return -1;
    }
}
