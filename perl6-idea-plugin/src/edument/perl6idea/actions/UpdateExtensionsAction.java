package edument.perl6idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateExtensionsAction extends AnAction {
    public static final Pattern LEGACY_EXTENSION_PATTERN = Pattern.compile(".+?\\.(p6|pl6|pm6|pm|pod6|pod)");
    private static final Map<String, String> nonLegacyExts = new HashMap<>();

    static {
        nonLegacyExts.put("p6", "raku");
        nonLegacyExts.put("pl6", "raku");
        nonLegacyExts.put("pm6", "rakumod");
        nonLegacyExts.put("pm", "rakumod");
        nonLegacyExts.put("pod6", "rakudoc");
        nonLegacyExts.put("pod", "rakudoc");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();

        Map<String, List<File>> filesToUpdate = collectFilesWithLegacyNames(modules);
        if (filesToUpdate.isEmpty())
            return;

        DialogWrapper check = new ChooseExtensionsToUpdate(project, filesToUpdate.keySet(), filesToUpdate);
        check.show();
    }

    @NotNull
    private static Map<String, List<File>> collectFilesWithLegacyNames(Module @NotNull [] modules) {
        Map<String, List<File>> filesToUpdate = new HashMap<>();

        for (Module module : modules) {
            for (VirtualFile root : ModuleRootManager.getInstance(module).getSourceRoots()) {
                if (root.isDirectory()) {
                    @NotNull List<File> files = FileUtil.findFilesByMask(LEGACY_EXTENSION_PATTERN, root.toNioPath().toFile());
                    for (File file : files) {
                        Matcher matcher = LEGACY_EXTENSION_PATTERN.matcher(file.getName());
                        if (matcher.matches()) {
                            filesToUpdate.compute(matcher.group(1), (f, list) -> {
                                if (list == null)
                                    list = new ArrayList<>();
                                list.add(file);
                                return list;
                            });
                        }
                    }
                }
            }
        }
        return filesToUpdate;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(e.getProject() != null);
    }

    private static class ChooseExtensionsToUpdate extends DialogWrapper {
        private final List<StringItem> exts;
        private final Map<String, List<File>> filesToUpdate;
        private final Project myProject;
        private JBTable myTable;
        private ListTableModel<StringItem> myModel;


        private ChooseExtensionsToUpdate(Project project,
                                         Set<String> exts,
                                         Map<String, List<File>> filesToUpdate) {
            super(project, true);
            this.myProject = project;
            this.exts = ContainerUtil.map(exts, e -> new StringItem(e));
            this.filesToUpdate = filesToUpdate;
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel(new MigLayout());
            myModel = new ListTableModel<>(
                new ColumnInfo[]{
                    new ColumnInfo<StringItem, Boolean>("#") {
                        @Override
                        public Boolean valueOf(StringItem item) {
                            return item.isSelected;
                        }

                        @Override
                        public boolean isCellEditable(StringItem item) {
                            return true;
                        }

                        @Override
                        public Class<?> getColumnClass() {
                            return Boolean.class;
                        }

                        @Override
                        public void setValue(StringItem item, Boolean value) {
                            item.isSelected = value;
                        }
                    },
                    new ColumnInfo<StringItem, String>("Name") {
                        @Override
                        public @Nullable String valueOf(StringItem item) {
                            return String.format(".%s -> .%s", item.ext, nonLegacyExts.get(item.ext));
                        }
                    }
                }, this.exts
            );
            myTable = new JBTable(myModel);
            panel.setMinimumSize(new Dimension(400, 300));
            panel.add(new JBScrollPane(myTable), "growx, growy, pushx, pushy");
            return panel;
        }

        @Override
        protected void doOKAction() {
            List<Pair<Path, Path>> failedToProcess = new ArrayList<>();
            WriteCommandAction.runWriteCommandAction(myProject, "Renaming...", null, () -> {
                for (int i = 0; i < myTable.getRowCount(); i++) {
                    if (myTable.getValueAt(i, 0) instanceof Boolean && ((Boolean)myTable.getValueAt(i, 0)).booleanValue()) {
                        String ext = myModel.getItem(i).ext;
                        List<File> files = filesToUpdate.get(ext);
                        for (File file : files) {
                            Path target = file.toPath();
                            Path newTarget =
                                target.resolveSibling(target.getFileName().toString().replace("." + ext, "." + nonLegacyExts.get(ext)));
                            try {
                                Files.move(target, newTarget);
                            }
                            catch (IOException e) {
                                failedToProcess.add(Pair.create(target, newTarget));
                            }
                        }
                    }
                }
            });
            LocalFileSystem.getInstance().refresh(false);
            close(0);
            if (!failedToProcess.isEmpty())
                new FailureDialog(failedToProcess).show();
        }

        private class FailureDialog extends DialogWrapper {
            private final List<Pair<Path, Path>> filesToShow;

            private FailureDialog(List<Pair<Path, Path>> failedToProcess) {
                super(myProject, true);
                this.filesToShow = failedToProcess;
                init();
            }

            @Override
            protected @Nullable JComponent createCenterPanel() {
                StringJoiner joiner = new StringJoiner("<br>");
                joiner.add("Could not rename those files:<br>");
                for (Pair<Path, Path> pair : filesToShow)
                    joiner.add(pair.first + " -> " + pair.second);
                return new JBLabel("<html>" + joiner.toString() + "</html>");
            }
        }
    }

    private static class StringItem {
        public boolean isSelected;
        public String ext;

        private StringItem(String ext) {
            this.ext = ext;
        }
    }
}
