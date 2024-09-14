package org.raku.comma.descriptors.surrounder;

import org.raku.comma.psi.RakuTry;

public class RakuTryCatchDefaultSurrounder extends RakuGenericTrySurrounder<RakuTry> {
    public RakuTryCatchDefaultSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected String createBranch() {
        return "default {}";
    }

    @Override
    public String getTemplateDescription() {
        return "try { CATCH { default } }";
    }
}
