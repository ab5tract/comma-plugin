package edument.perl6idea.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Perl6OutputLinkFilter implements Filter {
    private static final Pattern FILE_PATTERN = Pattern.compile("\\s*?in (?:\\S+)(?: \\S+?)? +at (\\S+)(?: \\(\\S*\\))? line (\\d+)");
    private final Project myProject;
    private VirtualFile baseDir;
    private String baseDirPath;

    public Perl6OutputLinkFilter(Project project) {
        myProject = project;
        baseDir = project.getBaseDir();
        baseDirPath = baseDir.getCanonicalPath();
    }

    @Nullable
    @Override
    public Result applyFilter(String line, int entireLength) {
        if (baseDirPath == null) return null;
        int startPoint = entireLength - line.length();
        Matcher matcher = FILE_PATTERN.matcher(line);

        if (matcher.find()) {
            if (matcher.group(1).startsWith(baseDirPath)) {
                VirtualFile file = baseDir.getFileSystem().findFileByPath(matcher.group(1));
                if (file == null)
                    return null;
                OpenFileDescriptor fileDescriptor =
                    new OpenFileDescriptor(myProject, file,
                                           Integer.parseInt(matcher.group(2)), 0);
                return new Result(startPoint + matcher.start(1),
                                  startPoint + matcher.end(1),
                                  new OpenFileHyperlinkInfo(fileDescriptor));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
