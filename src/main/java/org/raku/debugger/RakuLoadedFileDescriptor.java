package org.raku.debugger;

import com.intellij.util.text.CharSequenceBackedByChars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuLoadedFileDescriptor {
    private final String myPath;
    private final String myName;
    private String myModuleName;

    private final static char[] MODULE_NAME_AFTER_PATH_STARTS_WITH = {' ', '('};

    RakuLoadedFileDescriptor(String path, String name) {
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
        if (!(o instanceof RakuLoadedFileDescriptor that)) {
            return false;
        }

        return getPath().equals(that.getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }
}
