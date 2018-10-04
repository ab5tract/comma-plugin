package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.module.Perl6ModuleBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AddMonitorModuleFix implements IntentionAction {
    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Use OO::Monitors module";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Perl 6";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        editor.getDocument().insertString(0, "use OO::Monitors;\n");
        Path pathToMeta = Perl6ModuleBuilder.getMETAFilePath(project);
        if (pathToMeta == null) return;
        String jsonString;
        try {
            jsonString = new String(Files.readAllBytes(pathToMeta), CharsetToolkit.UTF8_CHARSET);
        } catch (IOException |JSONException e) {
            // TODO Warn a user with a popup?
            return;
        }
        JSONObject object = new JSONObject(jsonString);
        JSONArray depends = object.has("depends") ?
                            object.getJSONArray("depends") :
                            new JSONArray();
        for (Object item : depends) {
            // Check if it is already present
            if (item.toString().equals("OO::Monitors"))
                return;
        }
        depends.put("OO::Monitors");
        object.put("depends", depends);
        Perl6ModuleBuilder.writeMetaFile(pathToMeta, object);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
