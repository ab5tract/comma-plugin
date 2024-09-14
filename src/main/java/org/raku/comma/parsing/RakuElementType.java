package org.raku.comma.parsing;

import com.intellij.psi.tree.IElementType;
import org.raku.comma.RakuLanguage;
import org.jetbrains.annotations.NonNls;

public class RakuElementType extends IElementType {
    public RakuElementType(@NonNls String debugName) {
        super(debugName, RakuLanguage.getInstance());
    }

    public String toString() {
        return "Raku:" + super.toString();
    }
}
