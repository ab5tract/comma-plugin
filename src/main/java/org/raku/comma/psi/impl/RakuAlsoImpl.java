package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.symbols.MOPSymbolsAllowed;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.jetbrains.annotations.NotNull;

public class RakuAlsoImpl extends ASTWrapperPsiElement implements RakuAlso {
    public RakuAlsoImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void contributeMOPSymbols(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        RakuTrait trait = getTrait();
        if (trait == null) return;

        String mod = trait.getTraitModifier();
        if (mod.equals("is") || mod.equals("does")) {
            PsiElement typeName = getTypeName();
            if (typeName == null) return;
            PsiReference ref = typeName.getReference();
            if (ref == null) return;
            PsiElement resolve = ref.resolve();
            if (resolve == null) return;
            if (!(resolve instanceof RakuPackageDecl)) return;
            ((RakuPackageDecl)resolve).contributeMOPSymbols(collector,
                    mod.equals("does") ? symbolsAllowed.does() : symbolsAllowed.is());
        }
    }

    @Override
    public RakuTrait getTrait() {
        return PsiTreeUtil.findChildOfType(this, RakuTrait.class);
    }

    private PsiElement getTypeName() {
        RakuTrait trait = getTrait();
        if (trait == null) return null;
        return trait.getTraitModifier().equals("does") ?
               PsiTreeUtil.findChildOfType(this, RakuTypeName.class) :
               PsiTreeUtil.findChildOfType(this, RakuIsTraitName.class);
    }
}
