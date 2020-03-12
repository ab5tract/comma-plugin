package edument.perl6idea.cro.template.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.CroTemplateFileType;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;

public class CroTemplateElementFactory {
    protected static <T extends PsiElement> T produceElement(Project project, @NotNull String text, Class<T> clazz) {
        String filename = "dummy." + CroTemplateFileType.INSTANCE.getDefaultExtension();
        CroTemplateFile dummyFile = (CroTemplateFile) PsiFileFactory.getInstance(project)
                .createFileFromText(filename, CroTemplateFileType.INSTANCE, text);
        return PsiTreeUtil.findChildOfType(dummyFile, clazz);
    }

    public static CroTemplateParameter createSubParameter(Project project, String name) {
        return produceElement(project, getSubParameterText(name), CroTemplateParameter.class);
    }

    private static String getSubParameterText(String name) {
        return String.format("<:sub a(%s)>", name);
    }

    public static CroTemplateVariableAccess createVariableAccess(Project project, String name) {
        return produceElement(project, String.format("<%s>", name), CroTemplateVariableAccess.class);
    }

    public static CroTemplateCall createSubCall(Project project, String name) {
        return produceElement(project, String.format("<&%s>", name), CroTemplateCall.class);
    }

    public static CroTemplateApply createMacroCall(Project project, String name) {
        return produceElement(project, String.format("<|%s>", name), CroTemplateApply.class);
    }
}
