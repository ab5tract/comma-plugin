package org.raku.comma.editor;

import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesDelegate;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;

@InternalIgnoreDependencyViolation
public class RakuEnterBetweenBracesDelegate extends EnterBetweenBracesDelegate {
    @Override
    protected boolean isBracePair(char lBrace, char rBrace) {
        return (lBrace == '(' && rBrace == ')') || (lBrace == '{' && rBrace == '}') || lBrace == '[' && rBrace == ']';
    }
}
