package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6OldStyleScriptFileType extends LanguageFileType {
    public static final Perl6OldStyleScriptFileType INSTANCE = new Perl6OldStyleScriptFileType();

    private Perl6OldStyleScriptFileType() {
        super(Perl6Language.INSTANCE);
    }


    @NotNull
    @Override
    public String getName() {
        return "Perl 6 Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Perl 6 Script";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "pl6";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA;
    }
}
