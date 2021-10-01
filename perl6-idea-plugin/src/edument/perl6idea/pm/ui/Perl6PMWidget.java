package edument.perl6idea.pm.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import edument.perl6idea.utils.Perl6CommandLine;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class Perl6PMWidget {
    private final static Logger LOG = Logger.getInstance(Perl6PMWidget.class);
    public static final JTextPane outputPane = new JTextPane();
    private static ToolWindow myToolWindow;
    private static JComponent myComponent;

    public static void initAndRun(Project project, Perl6CommandLine command) {
        outputPane.setEditable(false);
        if (myComponent == null) {
            myComponent = new JPanel(new MigLayout());
            myComponent.add(new JBScrollPane(outputPane), "growx, growy, pushx, pushy");
        }
        ToolWindowManager twm = ToolWindowManager.getInstance(project);
        Supplier<String> supplier = () -> "Raku PM";
        if (myToolWindow == null) {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                myToolWindow = twm.registerToolWindow(new RegisterToolWindowTask(
                    "Raku PM Widget", ToolWindowAnchor.BOTTOM,
                    myComponent, false,
                    true, false,
                    true, null,
                    null, supplier
                ));
            });
        }
        ApplicationManager.getApplication().invokeAndWait(() -> myToolWindow.activate(null, true));
        outputPane.setText(outputPane.getText() + "\n\n> " + command.getCommandLineString() + "\n");
        try {
            ApplicationManager.getApplication().executeOnPooledThread(() -> executeProcess(command)).get();
        }
        catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            LOG.warn(e);
        }
    }

    private static int executeProcess(Perl6CommandLine command) {
        try {
            Process p = command.createProcess();
            command.setRedirectErrorStream(true);
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String oldText = outputPane.getText();
                    outputPane.setText(oldText + "\n" + line);
                }
            }
            catch (IOException e) {
                LOG.warn(e);
            }
            outputPane.setText(outputPane.getText() + "\n\nDone.");
            return p.waitFor();
        }
        catch (ExecutionException | InterruptedException e) {
            LOG.warn(e);
        }
        return -1;
    }
}
