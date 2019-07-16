package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
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
        builder.createPopup().showInBestPositionFor(editor);
    }

    private static void invokeImpl(Project project, Editor editor, PsiElement element, String type) {
        WriteCommandAction.runWriteCommandAction(project,
            () ->
                editor.getDocument().replaceString(
                    element.getTextOffset(), element.getTextOffset() + element.getTextLength(), type));
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return element.getNode().getElementType() == Perl6TokenTypes.PACKAGE_DECLARATOR;
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
