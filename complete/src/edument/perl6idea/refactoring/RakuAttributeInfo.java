package edument.perl6idea.refactoring;

import com.intellij.refactoring.classMembers.MemberInfoBase;
import edument.perl6idea.psi.Perl6PsiDeclaration;

public class RakuAttributeInfo extends MemberInfoBase<Perl6PsiDeclaration> {
    private final Perl6PsiDeclaration myDeclaration;

    public RakuAttributeInfo(Perl6PsiDeclaration member) {
        super(member);
        myDeclaration = member;
    }

    @Override
    public String getDisplayName() {
        return myDeclaration.getName();
    }
}
