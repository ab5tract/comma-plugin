package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.impl.Perl6VariableImpl;

public class Perl6ElementFactory {
    public static Perl6LongName createPrivateMethodCall(Project project, String name) {
        String text = getPrivateMethodText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6LongName.class);
    }

    private static String getPrivateMethodText(String name) {
        return String.format("class Dummy { method %s {} }", name);
    }

    public static Perl6Variable createVariable(Project project, String name) {
        String text = getVariableText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6Variable.class);
    }

    private static String getVariableText(String name) {
        return name;
    }

    //public static Perl6ParameterVariable createParameter(Project project, String name) {
    //    String text = getParameterText(name);
    //    Perl6File dummyFile = createFile(project, text);
    //    return PsiTreeUtil.findChildOfType(dummyFile, Perl6ParameterVariable.class);
    //}

    private static Perl6File createFile(Project project, String text) {
        String filename = "dummy." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();
        return (Perl6File)PsiFileFactory.getInstance(project)
                                        .createFileFromText(filename, Perl6ScriptFileType.INSTANCE, text);
    }

    //private static String getParameterText(String name) {
    //    return "sub (" + name + ") {}";
    //}
}
