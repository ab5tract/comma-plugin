package org.raku.liveTemplates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.TemplateOptionalProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.raku.utils.RakuUseUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AddUseProcessor implements TemplateOptionalProcessor {
    public static final List<UseData> USES = new ArrayList<>();

    private static final class UseData {
        public final List<String> templateNames;
        public final List<String> useNames;
        public final String moduleName;

        private UseData(List<String> templateNames, List<String> useNames, String name) {
            this.templateNames = templateNames;
            this.useNames = useNames;
            moduleName = name;
        }
    }

    static {
        USES.add(new UseData(
            Arrays.asList("cro-http-get", "cro-http-post", "cro-http-head", "cro-http-post", "cro-http-put", "cro-http-patch",
                          "cro-http-post-wwww-form-urlencoded", "cro-http-post-multipart", "cro-multipart-part"),
            Collections.singletonList("Cro::HTTP::Client"),
            "Cro::HTTP"
        ));
        USES.add(new UseData(
            Collections.singletonList("cro-webapp-form"),
            Collections.singletonList("Cro::WebApp::Form"),
            "Cro::WebApp"
        ));
        USES.add(new UseData(
            Collections.singletonList("red-model"),
            Collections.singletonList("Red"),
            "Red"
        ));
    }

    @Override
    public void processText(Project project,
                            Template template,
                            Document document,
                            RangeMarker templateRange,
                            Editor editor) {

        PsiFile file = PsiEditorUtil.getPsiFile(editor);
        PsiElement element = PsiUtilCore.getElementAtOffset(file, templateRange.getStartOffset());

        UseData use = getUses(template.getKey());
      if (use == null) {
        return;
      }
        for (String moduleToUse : use.useNames) {
          if (!RakuUseUtils.usesModule(element, moduleToUse)) {
            RakuUseUtils.addUse(editor, file, moduleToUse, use.moduleName);
          }
        }
    }

    @Override
    public @Nls String getOptionName() {
        return null;
    }

    @Override
    public boolean isEnabled(Template template) {
        return getUses(template.getKey()) != null;
    }

    @Nullable
    private static UseData getUses(String key) {
        for (UseData use : USES) {
          if (use.templateNames.contains(key)) {
            return use;
          }
        }
        return null;
    }
}
