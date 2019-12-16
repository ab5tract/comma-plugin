package edument.perl6idea.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Perl6OutputLinkFilter implements Filter {
    private static final Pattern FILE_PATTERN = Pattern.compile("at (\\S+)(?: \\(\\S*\\))? line (\\d+)");
    private final Project myProject;

    public Perl6OutputLinkFilter(Project project) {
        myProject = project;
    }

    @Nullable
    @Override
    public Result applyFilter(@NotNull String line, int entireLength) {

        int startPoint = entireLength - line.length();
        Matcher matcher = FILE_PATTERN.matcher(line);

        if (!matcher.find())
            return null;

        String pathPart = matcher.group(1);
        Collection<VirtualFile> files =
            FilenameIndex.getVirtualFilesByName(myProject, Paths.get(pathPart).getFileName().toString(), GlobalSearchScope.allScope(myProject));
        if (files.isEmpty())
            return null;

        for (VirtualFile file : files) {
            if (file.getPath().endsWith(pathPart)) {
                OpenFileDescriptor fileDescriptor =
                    new OpenFileDescriptor(myProject, file,
                                           Integer.parseInt(matcher.group(2)) - 1, 0);
                ResultItem item = new ResultItem(startPoint + matcher.start(1),
                                                 startPoint + matcher.end(1),
                                                 new OpenFileHyperlinkInfo(fileDescriptor));
                return new Result(Collections.singletonList(item));
            }
        }
        return null;
    }
}
