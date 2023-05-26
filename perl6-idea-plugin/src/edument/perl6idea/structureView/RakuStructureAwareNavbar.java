package edument.perl6idea.structureView;

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class RakuStructureAwareNavbar extends StructureAwareNavBarModelExtension {
    @Override
    public @Nullable String getPresentableText(Object object) {
        // Process Raku declarations
        if (object instanceof Perl6PackageDecl) {
            return ((Perl6PackageDecl)object).getPackageName();
        }
        else if (object instanceof Perl6RoutineDecl) {
            return ((Perl6RoutineDecl)object).getRoutineName();
        }
        else if (object instanceof Perl6VariableDecl) {
            return Strings.join(Arrays.asList(((Perl6VariableDecl)object).getVariableNames()), ", ");
        }
        else if (object instanceof Perl6SubCall) {
            return ((Perl6SubCall)object).getCallName();
        }
        // Common structure elements: files, directories etc.
        else if (object instanceof PsiFile) {
            return ((PsiFile)object).getName();
        }
        else if (object instanceof PsiDirectory)
            return null;
        else if (object instanceof Project) {
            return ((Project)object).getName();
        }
        return null;
    }

    @NotNull
    @Override
    protected Language getLanguage() {
        return Perl6Language.INSTANCE;
    }
}
