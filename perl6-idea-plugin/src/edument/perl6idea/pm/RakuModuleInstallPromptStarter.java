package edument.perl6idea.pm;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
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
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@InternalIgnoreDependencyViolation
public class RakuModuleInstallPromptStarter implements StartupActivity.Background {
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
                Set<RakuDependencySpec> installedDists = currentPM.getInstalledDistributions(project)
                    .stream().map(s -> new RakuDependencySpec(s)).collect(Collectors.toSet());
                for (String depFromMeta : dependencies) {
                    boolean hasDep = installedDists.stream().anyMatch(idFromPM -> idFromPM.equals(new RakuDependencySpec(depFromMeta)));
                    if (!hasDep)
                        unavailableDeps.add(depFromMeta);
                }
            }
            catch (ExecutionException e) {
                LOG.info("Could not query installed modules: " + e.getMessage());
            }
        }

        if (unavailableDeps.isEmpty())
            return;

        // Report if there are modules to install
        FileEditor[] editors = FileEditorManager.getInstance(project).getSelectedEditors();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            for (FileEditor fileEditor : editors) {
                @NotNull DismissableNotificationPanel notification = getPanel(project, pmManager.getCurrentPM(), unavailableDeps);
                FileEditorManager.getInstance(project).addTopComponent(fileEditor, notification);
                notification.setDismissCallback(() -> {
                    FileEditorManager.getInstance(project).removeTopComponent(fileEditor, notification);
                });
            }
        });
    }

    @NotNull
    private static DismissableNotificationPanel getPanel(@NotNull Project project,
                                                    RakuPackageManager pm,
                                                    List<String> unavailableDeps) {
        DismissableNotificationPanel panel = new DismissableNotificationPanel();
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

    static class DismissableNotificationPanel extends EditorNotificationPanel {
        void setDismissCallback(Runnable callback) {
            createActionLabel("Dismiss", new ActionHandler() {
                @Override
                public void handlePanelActionClick(@NotNull EditorNotificationPanel panel, @NotNull HyperlinkEvent event) {
                    callback.run();
                }

                @Override
                public void handleQuickFixClick(@NotNull Editor editor, @NotNull PsiFile psiFile) {}
            }, true);
        }
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
        return joiner + (complete ? "" : "...");
    }
}
