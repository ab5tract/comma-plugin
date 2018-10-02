package edument.perl6idea.findUsages;

import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.Nullable;

public class Perl6UsageTypeProvider implements UsageTypeProvider {
    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element) {
        if (element == null) return null;

        if (element instanceof Perl6TypeName) {
            return handleTypeName(element);
        }
        else if (element instanceof Perl6IsTraitName) {
            return handleIsTraitName();
        }
        else if (element instanceof Perl6Variable) {
            return handleVariable();
        }
        else if (element instanceof Perl6MethodCall) {
            return handleMethodCall();
        }
        else if (element instanceof Perl6SubCallName) {
            return handleSubCallName();
        }
        else if (element instanceof Perl6ModuleName) {
            return handleModuleName(element);
        }

        return null;
    }

    private static UsageType handleModuleName(PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof Perl6UseStatement) {
            return new UsageType("Use module usage");
        }
        else if (parent instanceof Perl6NeedStatement) {
            return new UsageType("Need module usage");
        }
        return null;
    }

    private static UsageType handleIsTraitName() {
        return new UsageType("Inheritance usage");
    }

    private static UsageType handleSubCallName() {
        return new UsageType("Subroutine call usage");
    }

    private static UsageType handleMethodCall() {
        return new UsageType("Method call usage");
    }

    private static UsageType handleVariable() {
        return new UsageType("Variable usage");
    }

    private static UsageType handleTypeName(PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof Perl6ScopedDecl) {
            return new UsageType("Declaration type restriction usage");
        }
        else if (parent instanceof Perl6Parameter) {
            return new UsageType("Parameter type restriction usage");
        }
        else if (parent instanceof Perl6ReturnConstraint) {
            return new UsageType("Return type restriction usage");
        }
        else if (parent instanceof Perl6Trait) {
            if (((Perl6Trait)parent).getTraitModifier().equals("does")) {
                return new UsageType("Composition usage");
            }
            else {
                return new UsageType("Trait usage");
            }
        }
        return null;
    }
}
