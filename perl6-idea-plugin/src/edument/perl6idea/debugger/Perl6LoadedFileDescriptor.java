package edument.perl6idea.debugger;

import com.intellij.util.text.CharSequenceBackedByChars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6LoadedFileDescriptor {
    private final String myPath;
    private final String myName;
    private String myModuleName;

    private final static char[] MODULE_NAME_AFTER_PATH_STARTS_WITH = {' ', '('};

    Perl6LoadedFileDescriptor(String path, String name) {
        myName = name;
        if (path.contains(new CharSequenceBackedByChars(MODULE_NAME_AFTER_PATH_STARTS_WITH))) {
            int startOfParens = path.lastIndexOf(" (");
            myModuleName = path.substring(startOfParens + 2, path.length() - 1);
            myPath = path.substring(0, startOfParens);
        } else {
            myPath = path;
        }
    }

    @NotNull
    public String getPath() {
        return myPath;
    }

    @Nullable
    public String getName() {
        return myName;
    }

    public String getModuleName() {
        return myModuleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Perl6LoadedFileDescriptor)) {
            return false;
        }

        Perl6LoadedFileDescriptor that = (Perl6LoadedFileDescriptor) o;

        return getPath().equals(that.getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }
}
