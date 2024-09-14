package org.raku.comma.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.RakuSubCall;
import org.raku.comma.psi.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuStructureViewBuilder extends TreeBasedStructureViewBuilder {
    private final PsiFile psiFile;

    public RakuStructureViewBuilder(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    @NotNull
    @Override
    public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        return new StructureViewModelBase(psiFile, editor,
                    new RakuStructureViewElement((RakuPsiElement)psiFile))
                .withSorters(Sorter.ALPHA_SORTER)
                .withSuitableClasses(RakuFile.class, RakuPackageDecl.class,
                                     RakuRegexDeclImpl.class, RakuRoutineDeclImpl.class,
                                     RakuConstantImpl.class, RakuVariableDeclImpl.class,
                                     RakuSubsetImpl.class, RakuEnumImpl.class,
                                     RakuSubCall.class);
    }
}
