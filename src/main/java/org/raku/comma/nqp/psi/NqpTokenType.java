package org.raku.comma.nqp.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.RakuLanguage;
import org.raku.comma.nqp.NqpLanguage;

public class NqpTokenType extends IElementType {

    public NqpTokenType(@NonNls @NotNull String debugName) {
        super(debugName, RakuLanguage.getInstance());
    }

    @Override
    public String toString() {
        return "NqpTokenType." + super.toString();
    }
}
