package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Version;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;

public class Perl6VersionImpl extends ASTWrapperPsiElement implements Perl6Version {
    public Perl6VersionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull Perl6Type inferType() {
        return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Version);
    }
}
