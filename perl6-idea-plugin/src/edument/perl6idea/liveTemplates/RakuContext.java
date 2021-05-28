package edument.perl6idea.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;

public class RakuContext extends TemplateContextType {
  protected RakuContext() {
    super("RAKU", "Raku");
  }

  @Override
  public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
    return templateActionContext.getFile().getLanguage().is(Perl6Language.INSTANCE);
  }
}
