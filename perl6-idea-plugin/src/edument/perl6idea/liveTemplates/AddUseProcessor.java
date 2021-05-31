package edument.perl6idea.liveTemplates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.TemplateOptionalProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;
import com.intellij.psi.util.PsiUtilCore;
import edument.perl6idea.utils.Perl6UseUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AddUseProcessor implements TemplateOptionalProcessor {
  public static final List<Pair<List<String>, List<String>>> USES = new ArrayList();

  static {
    USES.add(new Pair<>(
      Arrays.asList("cro-http-get", "cro-http-post", "cro-http-head", "cro-http-post", "cro-http-put", "cro-http-patch",
                    "cro-http-post-wwww-form-urlencoded", "cro-http-post-multipart", "cro-multipart-part"),
      Collections.singletonList("Cro::HTTP")
    ));
    USES.add(new Pair<>(
      Collections.singletonList("cro-webapp-form"),
      Collections.singletonList("Cro::WebApp")
    ));
    USES.add(new Pair<>(
      Collections.singletonList("red-model"),
      Collections.singletonList("Red")
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

    List<String> uses = getUses(template.getKey());
    if (uses == null)
      return;
    for (String use : uses) {
      if (!Perl6UseUtils.usesModule(element, use))
        Perl6UseUtils.addUse(editor, file, use);
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
  private static List<String> getUses(String key) {
    for (Pair<List<String>, List<String>> use : USES) {
      if (use.first.contains(key))
        return use.second;
    }
    return null;
  }
}
