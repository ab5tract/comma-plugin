package edument.perl6idea.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.psi.Perl6PsiScope;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import edument.perl6idea.psi.Perl6UseStatement;

public class Perl6UseUtils {
    public static boolean usesModule(PsiElement element, String expected) {
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(element, Perl6PsiScope.class);
        int elementTextOffset = element.getTextOffset();
        while (scope != null) {
            Perl6StatementList list = PsiTreeUtil.findChildOfType(scope, Perl6StatementList.class);
            if (list == null) break;
            Perl6Statement[] stats = PsiTreeUtil.getChildrenOfType(list, Perl6Statement.class);
            if (stats == null) stats = new Perl6Statement[0];
            for (Perl6Statement statement : stats) {
                if (statement.getTextOffset() > elementTextOffset) break;
                for (PsiElement child : statement.getChildren()) {
                    if (!(child instanceof Perl6UseStatement)) continue;
                    String moduleName = ((Perl6UseStatement)child).getModuleName();
                    if (expected.equals(moduleName)) {
                        return true;
                    }
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }

        return false;
    }

    public static void addUse(Editor editor, PsiFile file, String useName, String moduleName) {
        editor.getDocument().insertString(0, "use " + useName + ";\n");
        Module module = ModuleUtilCore.findModuleForFile(file);
        assert module != null;
        Perl6MetaDataComponent metaData = module.getService(Perl6MetaDataComponent.class);
        if (!metaData.getDepends(true).contains(moduleName)) {
            metaData.addDepends(moduleName);
        }
    }
}
