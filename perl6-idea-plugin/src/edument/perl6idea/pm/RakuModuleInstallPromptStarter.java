package edument.perl6idea.pm;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorNotificationPanel;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;

public class RakuModuleInstallPromptStarter implements StartupActivity {
    private static final Logger LOG = Logger.getInstance(RakuModuleInstallPromptStarter.class);

    @Override
    public void runActivity(@NotNull Project project) {
        RakuPackageManagerManager pmManager = project.getService(RakuPackageManagerManager.class);
        if (pmManager == null || pmManager.getCurrentPM() == null)
            return;
        RakuPackageManager currentPM = pmManager.getCurrentPM();
        Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();

        List<String> unavailableDeps = new ArrayList<>();

        for (Module module : modules) {
            Perl6MetaDataComponent metadata = module.getService(Perl6MetaDataComponent.class);
            Set<String> dependencies = metadata.getAllDependencies();
            try {
                // FIXME this is a very simplistic compare to avoid parsing Raku dep spec with :auth, :var etc,
                // this should be fixed into something smarter and more general eventually
                Set<String> installedDists = currentPM.getInstalledDistributions(project);
                for (String dep : dependencies) {
                    boolean hasDep = installedDists.stream().anyMatch(id -> id.equals(dep) || id.startsWith(dep + ":"));
                    if (!hasDep)
                        unavailableDeps.add(dep);
                }
            }
            catch (ExecutionException e) {
                LOG.info("Could not query installed modules: " + e.getMessage());
            }
        }

        if (unavailableDeps.isEmpty())
            return;

        // Report if there are modules to install
        @NotNull EditorNotificationPanel notification = getPanel(project, pmManager.getCurrentPM(), unavailableDeps);
        FileEditor[] editors = FileEditorManager.getInstance(project).getSelectedEditors();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            for (FileEditor fileEditor : editors) {
                FileEditorManager.getInstance(project).addTopComponent(fileEditor, notification);
            }
        });
    }

    @NotNull
    private static EditorNotificationPanel getPanel(@NotNull Project project,
                                                    RakuPackageManager pm,
                                                    List<String> unavailableDeps) {
        EditorNotificationPanel panel = new EditorNotificationPanel();
        panel.setText("Some Raku dependencies for this project are not installed (" + getListText(unavailableDeps) + ").");
        String installButtonText = "Install with " + pm.getKind().getName();
        AtomicBoolean startedProcessing = new AtomicBoolean();
        panel.createActionLabel(installButtonText, new EditorNotificationPanel.ActionHandler() {
            @Override
            public void handlePanelActionClick(@NotNull EditorNotificationPanel panel, @NotNull HyperlinkEvent event) {
                if (!startedProcessing.compareAndSet(false, true)) {
                    return;
                }
                panel.setText(panel.getText() + " Installing...");
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    for (String dep : unavailableDeps) {
                        try {
                            pm.install(project, dep);
                        }
                        catch (ExecutionException e) {
                            LOG.warn("Could not install a distribution '" + dep + "': " + e.getMessage());
                        }
                    }
                    panel.getParent().remove(panel);
                });
            }

            @Override
            public void handleQuickFixClick(@NotNull Editor editor, @NotNull PsiFile psiFile) {}
        }, true);
        return panel;
    }

    private static String getListText(List<String> deps) {
        StringJoiner joiner = new StringJoiner(", ");
        boolean complete = true;
        for (String dep : deps) {
            joiner.add(dep);
            if (joiner.length() > 80) {
                complete = false;
                break;
            }
        }
        return joiner.toString() + (complete ? "" : "...");
    }
}
