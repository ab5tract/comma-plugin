package edument.perl6idea.refactoring.inline;

import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.Perl6Language;

abstract public class Perl6InlineActionHandler extends InlineActionHandler {
    @Override
    public boolean isEnabledForLanguage(Language language) {
        return language instanceof Perl6Language;
    }
}
