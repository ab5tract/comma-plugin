package org.raku.refactoring;

import com.intellij.refactoring.classMembers.MemberInfoBase;
import org.raku.psi.RakuPsiDeclaration;

public class RakuAttributeInfo extends MemberInfoBase<RakuPsiDeclaration> {
    private final RakuPsiDeclaration myDeclaration;

    public RakuAttributeInfo(RakuPsiDeclaration member) {
        super(member);
        myDeclaration = member;
    }

    @Override
    public String getDisplayName() {
        return myDeclaration.getName();
    }
}
