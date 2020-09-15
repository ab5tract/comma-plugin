package edument.perl6idea.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertNonCapturingGroupIntoPosIntention extends ConvertNonCapturingGroupIntention {
    @NotNull
    @Override
    PsiElement obtainReplacer(Project project, Perl6RegexGroup group) {
        String regexContent = group.getText();
        return PsiTreeUtil.findChildOfType(
            Perl6ElementFactory
                .createStatementFromText(project, String.format("/(%s)/", regexContent.substring(1, regexContent.length() - 1))),
            Perl6RegexCapturePositional.class);
    }

    @Override
    protected void postProcess(Project project, Editor editor, PsiElement element) {
        // Here, we want to rename all positional capture variables from this regex
        // to consider newly added one
        // At zero step, check if this new positional is a top level,
        // only in this case we should update variables count, as recursive
        // positional captures are not flattened
        if (!(PsiTreeUtil.getParentOfType(element, Perl6RegexDriver.class, Perl6RegexCapturePositional.class, Perl6RegexVariable.class) instanceof Perl6RegexDriver))
            return;
        // First, find all positional variables this regex provides
        Perl6RegexDriver driver = PsiTreeUtil.getParentOfType(element, Perl6RegexDriver.class);
        assert driver != null;
        Collection<PsiNamedElement> vars = driver.collectRegexVariables();
        int rename = 0;
        for (PsiNamedElement var : vars) {
            if (var instanceof Perl6RegexCapturePositional) {
                if (var.getTextOffset() >= element.getTextOffset())
                    break;
                rename++;
            }
        }
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(element, Perl6Block.class, Perl6File.class);
        Collection<Perl6Variable> variablesToPatch = PsiTreeUtil.findChildrenOfType(scope, Perl6Variable.class);
        Pattern pattern = Pattern.compile("^\\$(\\d+)$");
        for (Perl6Variable maybePatch : variablesToPatch) {
            if (maybePatch.getTextOffset() < element.getTextOffset())
                continue;
            String name = maybePatch.getVariableName();
            if (name != null) {
                Matcher match = pattern.matcher(name);
                if (match.matches()) {
                    int digit = Integer.parseInt(match.group(1));
                    if (digit < rename)
                        continue;
                    maybePatch.setName("$" + (digit + 1));
                }
            }
        }
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert into positional capture";
    }
}