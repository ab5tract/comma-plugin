package edument.perl6idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.METHOD_CALL_OPERATOR;

public class Perl6MethodReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        ASTNode op = call.getNode().findChildByType(METHOD_CALL_OPERATOR);
        if (op != null && !op.getText().equals("!")) return null;
        ASTNode callName = call.getNode().findChildByType(LONG_NAME);
        Perl6PackageDeclImpl outerPackage = PsiTreeUtil.getParentOfType(call, Perl6PackageDeclImpl.class);
        if (outerPackage != null)
            for (Perl6PsiElement element : outerPackage.privateMethods()) {
                ASTNode nodeName = element.getNode().findChildByType(LONG_NAME);
                if (nodeName != null && callName != null && nodeName.getText().equals("!" + callName.getText()))
                    return element;
            }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
