package org.raku.comma.psi.impl;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.psi.symbols.RakuSymbolKind;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RakuSubCallNameImpl extends RakuASTWrapperPsiElement implements RakuSubCallName {
    public RakuSubCallNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        PsiElement parent = getParent();
        boolean maybeCoercion = parent instanceof RakuSubCall && ((RakuSubCall) parent).maybeCoercion();
        return new RakuSubCallReference(this, maybeCoercion);
    }

    @NotNull
    @Override
    public String getCallName() {
        return getText();
    }

    public boolean resolvesLexically() {
        return (0 < resolveLexicalSymbolAllowingMulti(RakuSymbolKind.Routine, getCallName()).size()) || resolvesAsLexicalOperator();
    }

    // When checking if a sub has been previously declared, we need to also check whether it is
    // lexically available as an operator in the current scope.
    private static final List<BiFunction<Project, String, RakuPsiElement>> createOperatorFunctions = List.of(
        RakuElementFactory::createInfixOperator,
        RakuElementFactory::createPrefixOperator,
        RakuElementFactory::createPostfixOperator
    );
    @Override
    public boolean resolvesAsLexicalOperator() {
        var project  = getProject();
        var callName = getCallName();

        // term operator needs some special handling because the parser doesn't distinguish RakuSubCall vs
        // a theoretical RakuTermCall, so using the RakuElementFactory approach doesn't work because it is
        // based on parsing the call text in current lexical scope.
        if (0 < (new RakuOpReference(this)).multiResolve(false).length) return true;

        // when using the RakuElementFactory methods, we are parsing against the current lexical scope,
        // meaning that we only need to know if the result of the attempted creation is not-null.
        return createOperatorFunctions.stream()
                                      .map(createOp -> createOp.apply(project, callName))
                                      .anyMatch(Objects::nonNull);
    }
}
