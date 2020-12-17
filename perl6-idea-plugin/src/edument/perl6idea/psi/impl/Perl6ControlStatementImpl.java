package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ControlStatement;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;

public class Perl6ControlStatementImpl extends ASTWrapperPsiElement implements Perl6ControlStatement {
    public Perl6ControlStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Perl6Type inferTopicType() {
        return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Exception);
    }
}
