package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ForStatement;
import edument.perl6idea.psi.Perl6PointyBlock;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.type.Perl6ParametricType;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

public class Perl6ForStatementImpl extends ASTWrapperPsiElement implements Perl6ForStatement {
    public Perl6ForStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isTopicalizing() {
        return PsiTreeUtil.getChildOfType(this, Perl6PointyBlock.class) == null;
    }

    @Override
    public Perl6PsiElement getSource() {
        PsiElement found = Perl6PsiUtil.skipSpaces(getFirstChild().getNextSibling(), true);
        return found instanceof Perl6PsiElement ? (Perl6PsiElement)found : null;
    }

    @Override
    public Perl6Type inferTopicType() {
        Perl6PsiElement source = getSource();
        if (source == null)
            return Perl6Untyped.INSTANCE;

        Perl6Type sourceType = source.inferType();
        if (sourceType instanceof Perl6ParametricType) {
            Perl6Type baseType = ((Perl6ParametricType)sourceType).getGenericType();
            if (isArrayType(baseType)) {
                Perl6Type[] args = ((Perl6ParametricType)sourceType).getArguments();
                if (args.length == 1)
                    return args[0];
            }
        }
        return Perl6Untyped.INSTANCE;
    }

    private boolean isArrayType(Perl6Type type) {
        Perl6Type array = Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Array);
        if (type.equals(array))
            return true;
        Perl6Type list = Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.List);
        if (type.equals(list))
            return true;
        Perl6Type positional = Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Positional);
        if (type.equals(positional))
            return true;
        return false;
    }
}
