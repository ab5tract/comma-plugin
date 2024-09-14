package org.raku.comma.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertNonCapturingGroupIntoPosIntention extends ConvertNonCapturingGroupIntention {
    @NotNull
    @Override
    PsiElement obtainReplacer(Project project, RakuRegexGroup group) {
        String regexContent = group.getText();
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(
          RakuElementFactory
                .createStatementFromText(project, String.format("/(%s)/", regexContent.substring(1, regexContent.length() - 1))),
          RakuRegexCapturePositional.class));
    }

    @Override
    protected void postProcess(Project project, Editor editor, PsiElement element) {
        // Here, we want to rename all positional capture variables from this regex
        // to consider newly added one
        // At zero step, check if this new positional is a top level,
        // only in this case we should update variables count, as recursive
        // positional captures are not flattened
        if (!(PsiTreeUtil.getParentOfType(element, RakuRegexDriver.class, RakuRegexCapturePositional.class, RakuRegexVariable.class) instanceof RakuRegexDriver))
            return;
        // First, find all positional variables this regex provides
        RakuRegexDriver driver = PsiTreeUtil.getParentOfType(element, RakuRegexDriver.class);
        assert driver != null;
        Collection<PsiNamedElement> vars = driver.collectRegexVariables();
        int rename = 0;
        for (PsiNamedElement var : vars) {
            if (var instanceof RakuRegexCapturePositional) {
                if (var.getTextOffset() >= element.getTextOffset())
                    break;
                rename++;
            }
        }
        RakuPsiScope scope = PsiTreeUtil.getParentOfType(element, RakuBlock.class, RakuFile.class);
        Collection<RakuVariable> variablesToPatch = PsiTreeUtil.findChildrenOfType(scope, RakuVariable.class);
        Pattern pattern = Pattern.compile("^\\$(\\d+)$");
        for (RakuVariable maybePatch : variablesToPatch) {
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