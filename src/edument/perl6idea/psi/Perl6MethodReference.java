package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6MethodReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        if (!call.getCallOperator().equals("!")) return null;
        Perl6PackageDeclImpl outerPackage = PsiTreeUtil.getParentOfType(call, Perl6PackageDeclImpl.class);
        if (outerPackage == null) return null;
        for (Object element : outerPackage.privateMethods())
            if (element instanceof Perl6RoutineDeclStub && ((Perl6RoutineDeclStub)element).getRoutineName().equals(call.getCallName()) ||
                element instanceof Perl6RoutineDecl && ((Perl6RoutineDecl)element).getRoutineName().equals(call.getCallName()))
                return (PsiElement)element;
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<Object> results = new ArrayList<>();
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        if (!call.getCallOperator().equals("!")) return results.toArray();
        Perl6PackageDeclImpl outerPackage = PsiTreeUtil.getParentOfType(call, Perl6PackageDeclImpl.class);
        if (outerPackage != null)
            results.addAll(Arrays.asList(outerPackage.privateMethods()));
        return results.toArray();
    }
}
