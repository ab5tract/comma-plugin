package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static edument.perl6idea.parsing.Perl6ElementTypes.TYPE_NAME;

public class Perl6TraitImpl extends StubBasedPsiElementBase<Perl6TraitStub> implements Perl6Trait {
    public Perl6TraitImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6TraitImpl(Perl6TraitStub stub, Perl6TraitStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getTraitModifier() {
        Perl6TraitStub stub = getStub();
        if (stub != null)
            return stub.getTraitModifier();

        PsiElement trait = findChildByType(Perl6TokenTypes.TRAIT);
        return trait == null ? "" : trait.getText();
    }

    @Override
    public String getTraitName() {
        Perl6TraitStub stub = getStub();
        if (stub != null)
            return stub.getTraitName();

        PsiElement name = findChildByType(TYPE_NAME);
        return name == null ? "" : name.getText();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:TRAIT)";
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        if (!getTraitModifier().equals("does")) return;

        Perl6TypeName typeName = null;
        Perl6TraitStub traitStub = getStub();
        if (traitStub != null) {
            List<StubElement> children = traitStub.getChildrenStubs();
            for (StubElement child : children) {
                if (child instanceof Perl6TypeNameStub) {
                    typeName = ((Perl6TypeNameStub)child).getPsi();
                    break;
                }
            }
        } else {
            typeName = findChildByType(TYPE_NAME);
        }

        if (typeName == null) return;
        PsiReference ref = typeName.getReference();
        if (ref == null) return;
        PsiElement resolve = ref.resolve();
        if (resolve != null) {
            // Try to resolve a local role
            Perl6PackageDeclStub stub = ((Perl6PackageDecl)resolve).getStub();
            if (stub != null) {
                contributeLocalRoleStub(collector, stub);
            } else {
                contributeLocalRole(collector, resolve);
            }
        } else {
            // Try to resolve external thing.
            contributeExternalRole(collector, typeName);
        }
    }

    private static void contributeLocalRole(Perl6SymbolCollector collector, PsiElement resolve) {
        Perl6StatementList list = PsiTreeUtil.getChildOfType(resolve, Perl6StatementList.class);
        if (list == null) return;
        Perl6Statement[] states = PsiTreeUtil.getChildrenOfType(list, Perl6Statement.class);
        if (states == null) return;
        for (Perl6Statement s : states) {
            if (s instanceof Perl6RoutineDecl) {
                Perl6RoutineDecl routine = (Perl6RoutineDecl)s;
                if (routine.isPrivateMethod() &&
                    routine.getRoutineKind().equals("method"))
                    collector.offerSymbol(new Perl6ExplicitSymbol(
                            Perl6SymbolKind.Routine,
                            routine
                    ));
            }
        }
    }

    private static void contributeLocalRoleStub(Perl6SymbolCollector collector, Perl6PackageDeclStub stub) {
        List<StubElement> children = stub.getChildrenStubs();
        for (StubElement child : children) {
            Perl6RoutineDeclStub routine = (Perl6RoutineDeclStub)child;
            if (routine.isPrivate() &&
                routine.getRoutineKind().equals("method"))
                collector.offerSymbol(new Perl6ExplicitSymbol(
                        Perl6SymbolKind.Routine,
                        routine.getPsi()
                ));
        }
    }

    private void contributeExternalRole(Perl6SymbolCollector collector, Perl6TypeName typeName) {
        Perl6VariantsSymbolCollector extCollector =
                new Perl6VariantsSymbolCollector(Perl6SymbolKind.ExternalPackage);
        applySymbolCollector(extCollector);
        for (Perl6Symbol pack : extCollector.getVariants()) {
                Perl6ExternalPackage externalPackage = (Perl6ExternalPackage)pack;
                if (externalPackage.getPackageKind() == Perl6PackageKind.ROLE &&
                    pack.getName().equals(typeName.getTypeName())) {
                    for (String sym : externalPackage.privateMethods()) {
                        if (sym.startsWith("!")) {
                        collector.offerSymbol(
                                new Perl6ExternalSymbol(Perl6SymbolKind.Routine, sym));
                        }
                    }
                }
            }
    }
}
