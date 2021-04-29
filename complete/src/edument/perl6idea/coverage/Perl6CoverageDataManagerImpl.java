package edument.perl6idea.coverage;

import com.intellij.execution.configurations.CommandLineState;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.stubs.*;
import com.intellij.util.Alarm;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.stub.Perl6FileStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class Perl6CoverageDataManagerImpl extends Perl6CoverageDataManager {
    private final Project project;
    private static final Pattern indexMatcher = Pattern.compile("^([^\\t]+)\\t(.+)");
    private static final Pattern lineMatcher = Pattern.compile("^HIT {2}(.+?)(?: \\(.+\\))? {2}(\\d+)");
    private Set<Perl6CoverageSuite> coverageSuites = new HashSet<>();
    private Perl6CoverageSuite currentSuite;
    private ConcurrentMap<Editor, Perl6CoverageSourceAnnotator> editorAnnotators =
        new ConcurrentHashMap<>();
    private ConcurrentMap<String, CoverageStatistics> fileCoverageStatsCache
            = new ConcurrentHashMap<>();

    public Perl6CoverageDataManagerImpl(@NotNull Project project) {
        this.project = project;
        EditorFactory.getInstance().addEditorFactoryListener(new CoverageEditorFactoryListener(), project);
    }

    @Override
    void addSuiteFromSingleCoverageFile(File data, Perl6CoverageCommandLineState state) {
        Map<String, Set<Integer>> covered = parseCoverageFile(data);
        Perl6CoverageSuite suite = createSuite(state);
        suite.addCoverageData("main", covered);
        changeToSuite(suite);
    }

    @Override
    public void addSuiteFromIndexFile(File index, Perl6CoverageTestRunningState state) {
        Perl6CoverageSuite suite = createSuite(state);
        try (BufferedReader br = new BufferedReader(new FileReader(index))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = indexMatcher.matcher(line);
                if (matcher.matches())
                    suite.addCoverageData(matcher.group(1),
                            parseCoverageFile(new File(matcher.group(2))));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        changeToSuite(suite);
    }

    @Override
    public void changeToSuite(Perl6CoverageSuite suite) {
        currentSuite = suite;
        fileCoverageStatsCache.clear();
        triggerPresentationUpdate();
    }

    @Override
    public void hideCoverageData() {
        currentSuite = null;
        fileCoverageStatsCache.clear();
        triggerPresentationUpdate();
    }

    @Override
    public boolean hasCurrentCoverageSuite() {
        return currentSuite != null;
    }

    private Perl6CoverageSuite createSuite(CommandLineState state) {
        // Form suite info.
        String name = project.getName() + " - " + state.getEnvironment().getRunProfile().getName();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Create a directory for the suite.
        @NonNls final String coverageRootPath = PathManager.getSystemPath() + File.separator + "coverage";
        final String path = coverageRootPath + File.separator + FileUtil.sanitizeFileName(name) +
            File.separator + FileUtil.sanitizeFileName(timestamp);
        new File(path).mkdirs();

        // Create the suite record.
        Perl6CoverageSuite suite = new Perl6CoverageSuite(name, timestamp, path);
        coverageSuites.add(suite);
        return suite;
    }

    private Map<String,Set<Integer>> parseCoverageFile(File data) {
        try (BufferedReader br = new BufferedReader(new FileReader(data))) {
            Map<String, Set<Integer>> covered = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = lineMatcher.matcher(line);
                if (matcher.matches()) {
                    String filename = matcher.group(1).replace(File.separator, "/");
                    if (isProjectFile(filename)) {
                        Integer lineNumber = Integer.parseInt(matcher.group(2));
                        if (!covered.containsKey(filename))
                            covered.put(filename, new HashSet<>());
                        covered.get(filename).add(lineNumber);
                    }
                }
            }
            return covered;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isProjectFile(String filename) {
        return filename.startsWith(project.getBasePath());
    }

    @Override
    public void triggerPresentationUpdate() {
        renewInformationInEditors();
        ProjectView.getInstance(project).refresh();
    }

    private void renewInformationInEditors() {
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        final VirtualFile[] openFiles = fileEditorManager.getOpenFiles();
        for (VirtualFile openFile : openFiles) {
            final FileEditor[] allEditors = fileEditorManager.getAllEditors(openFile);
            applyInformationToEditor(allEditors, openFile);
        }
    }

    private void applyInformationToEditor(FileEditor[] editors, final VirtualFile file) {
        PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>)
            () -> PsiManager.getInstance(project).findFile(file));
        if (psiFile != null && psiFile.isPhysical()) {
            String path = psiFile.getVirtualFile().getPath();
            for (FileEditor editor : editors) {
                if (editor instanceof TextEditor) {
                    // Clear any existing annotations.
                    final Editor textEditor = ((TextEditor)editor).getEditor();
                    Perl6CoverageSourceAnnotator ann = editorAnnotators.remove(textEditor);
                    if (ann != null)
                        Disposer.dispose(ann);

                    // Now add annotations.
                    if (currentSuite == null)
                        continue;
                    Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
                    if (fileData == null)
                        continue;
                    ann = new Perl6CoverageSourceAnnotator(psiFile, textEditor, fileData);
                    editorAnnotators.put(textEditor, ann);
                    ann.showAnnotations();
                }
            }
        }
    }

    private class CoverageEditorFactoryListener implements EditorFactoryListener {
        private final Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, project);
        private final Map<Editor, Runnable> myCurrentEditors = new HashMap<>();

        @Override
        public void editorCreated(@NotNull EditorFactoryEvent event) {
            final Editor editor = event.getEditor();
            if (editor.getProject() != project) return;
            if (currentSuite == null) return;
            final PsiFile psiFile = ReadAction.compute(() -> {
                if (project.isDisposed()) return null;
                final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
                final Document document = editor.getDocument();
                return documentManager.getPsiFile(document);
            });

            if (psiFile != null && psiFile.isPhysical()) {
                String path = psiFile.getVirtualFile().getPath();
                Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
                if (fileData == null)
                    return;
                Perl6CoverageSourceAnnotator ann = editorAnnotators.get(editor);
                if (ann == null) {
                    ann = new Perl6CoverageSourceAnnotator(psiFile, editor, fileData);
                }
                editorAnnotators.put(editor, ann);
                ann.showAnnotations();

                final Perl6CoverageSourceAnnotator finalAnn = ann;
                final Runnable request = () -> {
                    if (project.isDisposed()) return;
                    finalAnn.showAnnotations();
                };

                myCurrentEditors.put(editor, request);
                myAlarm.addRequest(request, 100);
            }
        }

        @Override
        public void editorReleased(@NotNull EditorFactoryEvent event) {
            final Editor editor = event.getEditor();
            if (editor.getProject() != project) return;
            try {
                Perl6CoverageSourceAnnotator ann = editorAnnotators.remove(editor);
                if (ann != null) {
                    Disposer.dispose(ann);
                }
            }
            finally {
                final Runnable request = myCurrentEditors.remove(editor);
                if (request != null) {
                    myAlarm.cancelRequest(request);
                }
            }
        }
    }

    @Override
    public CoverageStatistics coverageForFile(VirtualFile file) {
        String path = file.getPath();
        CoverageStatistics result = fileCoverageStatsCache.get(path);
        if (result == null) {
            result = computeCoverageForFile(file);
            if (result != null)
                fileCoverageStatsCache.put(path, result);
        }
        return result;
    }

    private CoverageStatistics computeCoverageForFile(VirtualFile file) {
        // Get coverage data if available.
        if (currentSuite == null)
            return null;
        String path = file.getPath();
        Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
        if (fileData == null)
            return null;

        // Look up file stub and line mapping for the file.
        ObjectStubTree stubTree = StubTreeLoader.getInstance().readFromVFile(project, file);
        if (stubTree == null)
            return null;
        Stub stubRoot = stubTree.getRoot();
        if (!(stubRoot instanceof Perl6FileStub))
            return null;
        Map<Integer, List<Integer>> statementLineMap = ((Perl6FileStub)stubRoot).getStatementLineMap();

        // Make a flat map of all line starters we might cover.
        Set<Integer> coverable = new HashSet<>();
        for (Map.Entry<Integer, List<Integer>> lineMapping : statementLineMap.entrySet())
            if (lineMapping.getValue() != null)
                coverable.add(lineMapping.getKey());
        int coverableLines = coverable.size();
        if (coverableLines == 0)
            return null;

        // Delete the covered ones from the set, leaving the uncovered.
        for (Set<Integer> covered : fileData.values())
            for (int oneBasedLine : covered)
                coverable.remove(oneBasedLine - 1);
        int uncoveredLines = coverable.size();

        // Return statistics object.
        return new CoverageStatistics(coverableLines - uncoveredLines, coverableLines);
    }

    @Override
    public CoverageStatistics coverageForDirectory(VirtualFile dir) {
        /* Gather all modules files in the directory. */
        List<VirtualFile> allSourceFiles = new ArrayList<>();
        VfsUtilCore.visitChildrenRecursively(dir, new VirtualFileVisitor<Object>() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                if (!file.isDirectory() && file.getFileType() instanceof Perl6ModuleFileType) {
                    allSourceFiles.add(file);
                }
                return true;
            }
        });

        /* Gather all the statistics. */
        List<CoverageStatistics> allStatistics = allSourceFiles.stream()
                .map(f -> coverageForFile(f))
                .filter(s -> s != null)
                 .collect(Collectors.toList());
        if (allStatistics.isEmpty())
            return null;
        int totalCovered = 0;
        int totalCoverable = 0;
        for (CoverageStatistics stats : allStatistics) {
            totalCovered += stats.getCoveredLines();
            totalCoverable += stats.getCoverableLines();
        }
        return new CoverageStatistics(totalCovered, totalCoverable);
    }
}
