package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.annotation.fix.AddUseModuleFix;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.utils.Perl6Constants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ChangePackageTypeIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        ColoredListCellRenderer<String> renderer = new ColoredListCellRenderer<String>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends String> list,
                                                 String value,
                                                 int index,
                                                 boolean selected,
                                                 boolean hasFocus) {
                setIcon(Perl6Constants.PACKAGE_TYPES.get(value));
                append(value);
            }
        };

        List<String> options = new ArrayList<>(
            ContainerUtil.filter(Perl6Constants.PACKAGE_TYPES.keySet(), s -> !s.equals(element.getText()))
        );

        IPopupChooserBuilder<String> builder = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(options)
            .setRenderer(renderer)
            .setItemChosenCallback(type -> invokeImpl(project, editor, element, type))
            .setNamerForFiltering(p -> p);
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            // For testing just take the next element in list
            options = new ArrayList<>(Perl6Constants.PACKAGE_TYPES.keySet());
            String newType = options.get(options.indexOf(element.getText()) % (options.size() - 1) + 1);
            if (element.getText().equals("role"))
                newType = "class";
            else if (element.getText().equals("class"))
                newType = "role";
            invokeImpl(project, editor, element, newType);
        } else {
            builder.createPopup().showInBestPositionFor(editor);
        }
    }

    private static void invokeImpl(Project project, Editor editor, PsiElement element, String type) {
        WriteCommandAction.runWriteCommandAction(
            project, () -> {
                boolean shouldAddMonitorUsage = false;

                PsiFile containingFile = element.getContainingFile();
                Perl6PsiElement perl6PsiElement = PsiTreeUtil.getParentOfType(element, Perl6PsiElement.class);
                if (perl6PsiElement != null) {
                    Perl6Symbol metamodelSymbol = perl6PsiElement.resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, "MetamodelX::MonitorHOW");
                    shouldAddMonitorUsage = type.equals("monitor") && (metamodelSymbol == null);
                }

                Perl6PackageDecl decl = PsiTreeUtil.getParentOfType(element, Perl6PackageDecl.class);
                String oldType = decl.getPackageKind();
                PsiElement declarator = decl.getPackageKeywordNode();
                declarator.replace(Perl6ElementFactory.createPackageDeclarator(project, type));

                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());

                if (shouldAddMonitorUsage) {
                    new AddUseModuleFix("OO::Monitors").invoke(project, editor, containingFile);
                    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                }

                updateChildren(decl, oldType, type);
            });
    }

    private static void updateChildren(Perl6PackageDecl decl, String oldType, String newType) {
        if (!(oldType.equals("class") && newType.equals("role")) &&
            !(oldType.equals("role") && newType.equals("class")))
            return;

        List<Perl6PackageDecl> children = decl.collectChildren();

        String packageName = decl.getPackageName();

        for (Perl6PackageDecl child : children) {
            for (Perl6Trait trait : child.getTraits()) {
                String modifier = trait.getTraitModifier();
                String name = trait.getTraitName();
                if (oldType.equals("class") && modifier.equals("is") && name.equals(packageName))
                    trait.changeTraitMod("does");
                else if (oldType.equals("role") && modifier.equals("does") && name.equals(packageName))
                    trait.changeTraitMod("is");
            }
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        IElementType elementType = element.getNode().getElementType();
        if (elementType == Perl6TokenTypes.PACKAGE_DECLARATOR)
            return true;
        return elementType == Perl6TokenTypes.PACKAGE_NAME;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Change package type";
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }
}
