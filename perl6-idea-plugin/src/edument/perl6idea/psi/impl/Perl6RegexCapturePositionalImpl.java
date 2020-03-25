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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Perl6RegexCapturePositionalImpl extends ASTWrapperPsiElement implements Perl6RegexCapturePositional {
    public Perl6RegexCapturePositionalImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        // This is not very efficient, since we want to use alias in caller in Perl6QuoteRegexImpl,
        // but we sacrifice some performance for code consistency
        Perl6RegexDriver driver = PsiTreeUtil.getParentOfType(this, Perl6RegexDriver.class);
        if (driver == null) return "";
        Perl6Regex regex = PsiTreeUtil.findChildOfType(driver, Perl6Regex.class, false);
        if (regex == null) return "";

        int captureCounter = 0;
        for (PsiElement s : regex.getChildren()) {
            if (s.getFirstChild() instanceof Perl6RegexCapturePositional) {
                if (s.getFirstChild().equals(this))
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
