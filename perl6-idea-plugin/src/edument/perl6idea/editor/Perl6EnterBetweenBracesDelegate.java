package edument.perl6idea.editor;

import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesDelegate;

public class Perl6EnterBetweenBracesDelegate extends EnterBetweenBracesDelegate {
    @Override
    protected boolean isBracePair(char lBrace, char rBrace) {
        return (lBrace == '(' && rBrace == ')') || (lBrace == '{' && rBrace == '}') || lBrace == '[' && rBrace == ']';
    }
}
