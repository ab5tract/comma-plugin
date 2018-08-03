package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.module.Perl6ModuleBuilder;
import edument.perl6idea.utils.Perl6ModuleListFetcher;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MissingModuleFix implements IntentionAction {
    private String moduleName;

    public MissingModuleFix(Project project, String text) {
        moduleName = Perl6ModuleListFetcher.getModuleByProvideAsync(project, text);
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return String.format("Add module %s to META6.json", moduleName);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Add module to META6.json";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return moduleName != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        VirtualFile metaFile = project.getBaseDir().findChild("META6.json");
        if (metaFile == null) return;
        Path path = Paths.get(metaFile.getPath());
        String jsonString;
        JSONObject meta;
        try {
            jsonString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            meta = new JSONObject(jsonString);
        } catch (IOException|JSONException e) {
            return;
        }
        JSONArray depends;
        if (!meta.has("depends"))
            meta.put("depends", new JSONArray());
        depends = (JSONArray)meta.get("depends");
        depends.put(moduleName);
        meta.put("depends", depends);
        Perl6ModuleBuilder.writeMetaFile(path, meta);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
