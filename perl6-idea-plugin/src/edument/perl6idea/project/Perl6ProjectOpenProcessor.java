package edument.perl6idea.project;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessorBase;
import edument.perl6idea.module.Perl6ProjectBuilder;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Perl6ProjectOpenProcessor extends ProjectOpenProcessorBase<Perl6ProjectBuilder> {
    private static final Logger LOG = Logger.getInstance(Perl6ProjectOpenProcessor.class);

    protected Perl6ProjectOpenProcessor() {
        super(new Perl6ProjectBuilder());
    }

    @Nullable
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{ "META6.json", "META.list" };
    }

    @Override
    public boolean isStrongProjectInfoHolder() {
        return true;
    }

    @Override
    protected boolean doQuickImport(VirtualFile file, WizardContext wizardContext) {
        try {
            getBuilder().setFileToImport(file.getParent().getPath());
            JSONObject object = new JSONObject(new String(file.contentsToByteArray(), CharsetToolkit.UTF8_CHARSET));
            if (object.has("name")) {
                Object value = object.get("name");
                if (value instanceof String) {
                    wizardContext.setProjectName(StringUtil.sanitizeJavaIdentifier((String)value));
                    return true;
                }
            }
        }
        catch (IOException|JSONException e) {
            LOG.error(e);
            return false;
        }
        return false;
    }
}
