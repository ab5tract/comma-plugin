package edument.perl6idea.coverage;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Perl6CoverageDataManagerImpl extends Perl6CoverageDataManager {
    private final Project project;
    private static final Pattern lineMatcher = Pattern.compile("^HIT  (.+)  (\\d+)");
    private Set<Perl6CoverageSuite> coverageSuites = new HashSet<>();
    private Perl6CoverageSuite currentSuite;

    public Perl6CoverageDataManagerImpl(@NotNull Project project) {
        this.project = project;
    }

    @Override
    void addSuiteFromSingleCoverageFile(File data, Perl6CoverageCommandLineState state) {
        Map<String, Set<Integer>> covered = parseCoverageFile(data);
        Perl6CoverageSuite suite = createSuite(state);
        suite.addCoverageData("main", covered);
        changeToSuite(suite);
    }

    public void changeToSuite(Perl6CoverageSuite suite) {
        currentSuite = suite;
        triggerPresentationUpdate();
    }

    private Perl6CoverageSuite createSuite(Perl6CoverageCommandLineState state) {
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
        return suite;
    }

    private Map<String,Set<Integer>> parseCoverageFile(File data) {
        try (BufferedReader br = new BufferedReader(new FileReader(data))) {
            Map<String, Set<Integer>> covered = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = lineMatcher.matcher(line);
                if (matcher.matches()) {
                    String filename = matcher.group(1);
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
        if (currentSuite == null)
            return;
        renewInformationInEditors();
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
            Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
            // Remote existing annotations for the file
            if (!fileData.isEmpty()) {
                // Annotate the file
            }
        }
    }
}
