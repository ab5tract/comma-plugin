package edument.perl6idea.cro.template.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import edument.perl6idea.cro.template.psi.CroTemplateFile;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;
import edument.perl6idea.cro.template.psi.CroTemplateSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CroTemplateStructureViewBuilder extends TreeBasedStructureViewBuilder {
    private final PsiFile psiFile;

    public CroTemplateStructureViewBuilder(PsiFile file) {
        this.psiFile = file;
    }

    @NotNull
    @Override
    public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        return new StructureViewModelBase(psiFile, editor, new CroTemplateStructureViewElement(psiFile))
                .withSorters(Sorter.ALPHA_SORTER)
                .withSuitableClasses(CroTemplateFile.class, CroTemplateMacro.class, CroTemplateSub.class);
    }
}
