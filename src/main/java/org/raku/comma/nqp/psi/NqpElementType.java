package org.raku.comma.nqp.psi;

import com.intellij.psi.tree.IElementType;
import org.raku.comma.nqp.NqpLanguage;

public class NqpElementType extends IElementType {
    public NqpElementType(String debugName) {
        super(debugName, NqpLanguage.INSTANCE);
    }
}
