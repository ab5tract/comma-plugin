package edument.perl6idea.providers;

import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import edument.perl6idea.psi.Perl6Heredoc;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.LanguageInjectionSupport;
import org.jetbrains.annotations.NotNull;

public class Perl6LanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        InjectedLanguage injected = host.getUserData(LanguageInjectionSupport.TEMPORARY_INJECTED_LANGUAGE);
        String text = null;
        TextRange range = null;

        if (host instanceof Perl6Heredoc && ((Perl6Heredoc)host).getIndentation() == 0) {
            text = ((Perl6Heredoc)host).getStringText();
            range = new TextRange(0, text.length());
        }

        Language language = injected != null ? injected.getLanguage() : null;
        if (language != null && text != null) {
            injectionPlacesRegistrar.addPlace(language, range, null, null);
        }
    }
}
