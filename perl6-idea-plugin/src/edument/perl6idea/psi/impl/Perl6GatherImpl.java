package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Gather;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;

public class Perl6GatherImpl extends ASTWrapperPsiElement implements Perl6Gather {
    public Perl6GatherImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull Perl6Type inferType() {
        return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Seq);
    }
}
