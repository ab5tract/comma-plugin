package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Perl6RegexCapturePositionalImpl extends ASTWrapperPsiElement implements Perl6RegexCapturePositional {
    public Perl6RegexCapturePositionalImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        // This is not very efficient, since we want to use alias in caller in Perl6QuoteRegexImpl,
        // but we sacrifice some performance for code consistency
        Perl6RegexDriver regex = PsiTreeUtil.getParentOfType(this, Perl6QuoteRegex.class, Perl6Regex.class);
        if (regex != null) {
            Collection<Perl6RegexCapturePositional> captures = PsiTreeUtil.findChildrenOfType((PsiElement)regex, Perl6RegexCapturePositional.class);
            int captureCounter = 0;
            for (Perl6RegexCapturePositional capture : captures) {
                if (this.equals(capture))
                    return "$" + captureCounter;
                captureCounter++;
            }
        }
        return "";
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
