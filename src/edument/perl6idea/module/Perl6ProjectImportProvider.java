package edument.perl6idea.module;

import com.intellij.projectImport.ProjectImportProvider;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

public class Perl6ProjectImportProvider extends ProjectImportProvider {
    protected Perl6ProjectImportProvider() {
        super(new Perl6ProjectBuilder());
    }

    @Nullable
    @Language("HTML")
    public String getFileSample() {
        return "<b>Perl 6</b> project file (META6.json";
    }
}
