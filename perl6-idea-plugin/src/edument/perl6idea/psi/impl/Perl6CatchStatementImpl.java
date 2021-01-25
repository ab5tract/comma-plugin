package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6CatchStatement;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;

public class Perl6CatchStatementImpl extends ASTWrapperPsiElement implements Perl6CatchStatement {
    public Perl6CatchStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Perl6Type inferTopicType() {
        return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Exception);
    }
}
