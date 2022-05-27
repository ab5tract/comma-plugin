package edument.perl6idea.findUsages;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class Perl6UsageTypeProvider implements UsageTypeProvider {
    public static final UsageType SUBROUTINE_CALL_USAGE = new UsageType(() -> "Subroutine call usage");
    public static final UsageType METHOD_CALL_USAGE = new UsageType(() -> "Method call usage");
    public static final UsageType VARIABLE_USAGE = new UsageType(() -> "Variable usage");
    public static final UsageType INHERITANCE_USAGE = new UsageType(() ->"Inheritance usage");
    public static final UsageType DECLARATION_TYPE_RESTRICTION_USAGE = new UsageType(() ->"Declaration type restriction usage");
    public static final UsageType PARAMETER_TYPE_RESTRICTION_USAGE = new UsageType(() ->"Parameter type restriction usage");
    public static final UsageType RETURN_TYPE_RESTRICTION_USAGE = new UsageType(() -> "Return type restriction usage");
    public static final UsageType COMPOSITION_USAGE = new UsageType(() -> "Composition usage");
    public static final UsageType TRAIT_USAGE = new UsageType(() -> "Trait usage");
    public static final UsageType USE_MODULE_USAGE = new UsageType(() -> "Use module usage");
    public static final UsageType NEED_MODULE_USAGE = new UsageType(() ->"Need module usage");

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
            return USE_MODULE_USAGE;
        }
        else if (parent instanceof Perl6NeedStatement) {
            return NEED_MODULE_USAGE;
        }
        return null;
    }

    private static UsageType handleIsTraitName() {
        return INHERITANCE_USAGE;
    }

    private static UsageType handleSubCallName() {
        return SUBROUTINE_CALL_USAGE;
    }

    private static UsageType handleMethodCall() {
        return METHOD_CALL_USAGE;
    }

    private static UsageType handleVariable() {
        return VARIABLE_USAGE;
    }

    private static UsageType handleTypeName(PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof Perl6ScopedDecl) {
            return DECLARATION_TYPE_RESTRICTION_USAGE;
        }
        else if (parent instanceof Perl6Parameter) {
            return PARAMETER_TYPE_RESTRICTION_USAGE;
        }
        else if (parent instanceof Perl6ReturnConstraint) {
            return RETURN_TYPE_RESTRICTION_USAGE;
        }
        else if (parent instanceof Perl6Trait) {
            if (((Perl6Trait)parent).getTraitModifier().equals("does")) {
                return COMPOSITION_USAGE;
            }
            else {
                return TRAIT_USAGE;
            }
        }
        return null;
    }
}
