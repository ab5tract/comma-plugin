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
    public Perl6Type inferLoopParameterType(int index) {
        // For now we only do array types, so this shall do.
        return inferTopicType();
    }

    @Override
    public Perl6Type inferTopicType() {
        Perl6PsiElement source = getSource();
        if (source == null)
            return Perl6Untyped.INSTANCE;

        Perl6Type sourceType = source.inferType().nominalType();
        if (sourceType instanceof Perl6ParametricType) {
            Perl6Type baseType = ((Perl6ParametricType)sourceType).getGenericType();
            if (isArrayType(baseType)) {
                Perl6Type[] args = ((Perl6ParametricType)sourceType).getArguments();
                if (args.length == 1)
                    return args[0];
            }
        }
        else if (isHashType(sourceType)) {
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Pair);
        }

        return Perl6Untyped.INSTANCE;
    }

    private boolean isArrayType(Perl6Type type) {
        Perl6SdkType sdkType = Perl6SdkType.getInstance();
        Perl6Type array = sdkType.getCoreSettingType(getProject(), Perl6SettingTypeId.Array);
        if (type.equals(array))
            return true;
        Perl6Type list = sdkType.getCoreSettingType(getProject(), Perl6SettingTypeId.List);
        if (type.equals(list))
            return true;
        Perl6Type positional = sdkType.getCoreSettingType(getProject(), Perl6SettingTypeId.Positional);
        if (type.equals(positional))
            return true;
        return false;
    }

    private boolean isHashType(Perl6Type type) {
        Perl6SdkType sdkType = Perl6SdkType.getInstance();
        Perl6Type array = sdkType.getCoreSettingType(getProject(), Perl6SettingTypeId.Hash);
        if (type.equals(array))
            return true;
        Perl6Type list = sdkType.getCoreSettingType(getProject(), Perl6SettingTypeId.Map);
        if (type.equals(list))
            return true;
        Perl6Type positional = sdkType.getCoreSettingType(getProject(), Perl6SettingTypeId.Associative);
        if (type.equals(positional))
            return true;
        return false;
    }
}
