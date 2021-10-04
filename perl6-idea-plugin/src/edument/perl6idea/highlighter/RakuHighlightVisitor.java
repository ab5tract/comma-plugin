// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.highlighter;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RakuHighlightVisitor extends RakuElementVisitor implements HighlightVisitor {
    private final Map<String, List<Perl6PackageDecl>> myDuplicateClassesPool = new THashMap<>();
    private final Map<String, List<Perl6SignatureHolder>> myDuplicateRoutinesPool = new THashMap<>();
    private final Map<String, List<Perl6Variable>> myDuplicateVariablesPool = new THashMap<>();

    private HighlightInfoHolder myHolder;

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof Perl6File;
    }

    @Override
    public void visit(@NotNull PsiElement element) {
        element.accept(this);
    }

    @Override
    public boolean analyze(@NotNull PsiFile file,
                           boolean updateWholeFile,
                           @NotNull HighlightInfoHolder holder,
                           @NotNull Runnable highlight) {
        try {
            myHolder = holder;
            if (updateWholeFile) {
                ProgressIndicator progress = ProgressManager.getInstance().getProgressIndicator();
                if (progress == null) throw new IllegalStateException("Must be run under progress");
                highlight.run();
                ProgressManager.checkCanceled();
            }
            else {
                highlight.run();
            }
        }
        finally {
            myHolder = null;
            myDuplicateClassesPool.clear();
        }

        return true;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @NotNull HighlightVisitor clone() {
        return new RakuHighlightVisitor();
    }

    // Visitor methods

    @Override
    public void visitPackage(Perl6PackageDecl packageDecl) {
        super.visitPackage(packageDecl);
        if (packageDecl.getGlobalName() == null || packageDecl.getPackageKeywordNode() == null ||
            packageDecl.getNameIdentifier() == null)
            return;
        visitPackageDeclarations(myHolder, packageDecl);
        TextRange range = new TextRange(packageDecl.getPackageKeywordNode().getTextOffset(),
                                        packageDecl.getNameIdentifier().getTextRange().getEndOffset());
        myDuplicateClassesPool.compute(packageDecl.getGlobalName(), (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            else {
                // For roles we need to care about different signatures
                if (packageDecl.getPackageKind().equals("role")) {
                    Perl6Parameter[] newParams = packageDecl.getSignature();
                    for (Perl6PackageDecl decl : v) {
                        Perl6Parameter[] oldParams = decl.getSignature();
                        // Different numbers of args
                        if (newParams.length == oldParams.length)
                            myHolder.add(getDuplicateHighlightInfo(v.get(0), range, packageDecl.getGlobalName(), HighlightInfoType.ERROR));
                    }
                }
                else {
                    myHolder.add(getDuplicateHighlightInfo(v.get(0), range, packageDecl.getGlobalName(), HighlightInfoType.ERROR));
                }
            }
            v.add(packageDecl);
            return v;
        });
    }

    private void visitPackageDeclarations(HighlightInfoHolder holder,
                                          Perl6PackageDecl packageDecl) {
        Map<String, List<Perl6SignatureHolder>> methodsPool = new THashMap<>();
        List<Perl6PsiDeclaration> decls = packageDecl.getDeclarations();

        for (Perl6PsiDeclaration decl : decls) {
            if (decl instanceof Perl6RoutineDecl) {
                if (decl.getNameIdentifier() == null || ((Perl6RoutineDecl)decl).getDeclaratorNode() == null)
                    continue;
                TextRange textRange = new TextRange(((Perl6RoutineDecl)decl).getDeclaratorNode().getTextOffset(), decl.getNameIdentifier().getTextRange().getEndOffset());
                visitSignatureHolder(holder, (Perl6SignatureHolder)decl, ((Perl6RoutineDecl)decl).getRoutineName(), textRange, methodsPool);
            } else if (decl instanceof Perl6RegexDecl) {
                if (decl.getName() == null || decl.getNameIdentifier() == null)
                    return;
                TextRange textRange = new TextRange(decl.getTextOffset(), decl.getNameIdentifier().getTextRange().getEndOffset());
                visitSignatureHolder(holder, (Perl6SignatureHolder)decl, decl.getName(), textRange, methodsPool);
            }
        }
    }

    @Override
    public void visitRegex(Perl6RegexDecl regexDecl) {
        super.visitRegex(regexDecl);
        if (regexDecl.getName() == null || regexDecl.getNameIdentifier() == null)
            return;
        // First, exclude routines from packages, they have their own check in visitPackage
        if (PsiTreeUtil.getParentOfType(regexDecl, Perl6PackageDecl.class) != null)
            return;

        TextRange textRange = new TextRange(regexDecl.getTextOffset(), regexDecl.getNameIdentifier().getTextRange().getEndOffset());
        visitSignatureHolder(myHolder, regexDecl, regexDecl.getName(), textRange, myDuplicateRoutinesPool);
    }

    protected void visitSignatureHolder(HighlightInfoHolder infoHolder, Perl6SignatureHolder holder,
                                        String name, TextRange textRange,
                                        Map<String, List<Perl6SignatureHolder>> pool) {
        if (name == null || textRange == null)
            return;
        pool.compute(name, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            else {
                int firstDuplicateIndex = isElementInPool(holder, v);

                if (firstDuplicateIndex >= 0 && firstDuplicateIndex < v.size()) {
                    Perl6SignatureHolder oldHolder = v.get(firstDuplicateIndex);
                    if (oldHolder instanceof Perl6PsiElement)
                        infoHolder.add(getDuplicateHighlightInfo((Perl6PsiElement)oldHolder, textRange, name, HighlightInfoType.ERROR));
                }
            }
            v.add(holder);
            return v;
        });
    }

    private static int isElementInPool(Perl6SignatureHolder holder, List<Perl6SignatureHolder> v) {
        int firstDuplicateIndex = -1;
        // For multi routines we need to compare signatures
        Perl6Parameter[] paramsNew =
            holder.getSignatureNode() == null ? new Perl6Parameter[0] : holder.getSignatureNode().getParameters();
        Perl6PsiScope newScope = PsiTreeUtil.getParentOfType((PsiElement)holder, Perl6PsiScope.class);
        ROUTINES:
        for (int j = 0; j < v.size(); j++) {
            Perl6SignatureHolder decl = v.get(j);
            if (holder == decl)
                continue;

            Perl6PsiScope oldScope = PsiTreeUtil.getParentOfType((PsiElement)decl, Perl6PsiScope.class);
            if (newScope != null && oldScope != null && !PsiEquivalenceUtil.areElementsEquivalent(newScope, oldScope))
                continue;

            if (decl.getMultiness() == "only") {
                firstDuplicateIndex = j;
                break;
            }
            Perl6Parameter[] paramsKnown =
                decl.getSignatureNode() == null ? new Perl6Parameter[0] : decl.getSignatureNode().getParameters();
            // No params == both are duplicates
            if (paramsKnown.length == 0 && paramsNew.length == 0) {
                firstDuplicateIndex = j;
                break;
            }
            // Different signature length - not a duplicate
            if (paramsKnown.length != paramsNew.length) {
                continue;
            }
            // Compare by type as best as we can now
            for (int i = 0; i < paramsKnown.length; i++) {
                Perl6Parameter parameter1 = paramsKnown[i];
                Perl6Parameter parameter2 = paramsNew[i];
                if (!parameter1.equalsParameter(parameter2)) {
                    continue ROUTINES;
                }
            }
            firstDuplicateIndex = j;
            break;
        }
        return firstDuplicateIndex;
    }

    @Override
    public void visitRoutine(Perl6RoutineDecl routineDecl) {
        super.visitRoutine(routineDecl);
        if (routineDecl.getDeclaratorNode() == null || routineDecl.getName() == null || routineDecl.getNameIdentifier() == null)
            return;
        // First, exclude routines from packages, they have their own check in visitPackage
        if (PsiTreeUtil.getParentOfType(routineDecl, Perl6PackageDecl.class) != null)
            return;
        TextRange textRange = new TextRange(routineDecl.getDeclaratorNode().getTextOffset(), routineDecl.getNameIdentifier().getTextRange().getEndOffset());

        visitSignatureHolder(myHolder, routineDecl, routineDecl.getName(), textRange, myDuplicateRoutinesPool);
    }

    private static HighlightInfo getDuplicateHighlightInfo(Perl6PsiElement originalDecl,
                                                           TextRange range,
                                                           String name, HighlightInfoType infoType) {
        if (!originalDecl.isValid())
            return null;
        PsiFile containingFile = originalDecl.getContainingFile();
        String previousPos = containingFile.getName() +
                             ":" +
                             (StringUtil.offsetToLineNumber(containingFile.getText(), originalDecl.getTextOffset()) + 1);
        return HighlightInfo.newHighlightInfo(infoType)
            .range(range)
            .descriptionAndTooltip(String.format("Re-declaration of %s from %s", name, previousPos))
            .create();
    }

    @Override
    public void visitVariableDecl(Perl6VariableDecl variableDecl) {
        super.visitVariableDecl(variableDecl);
        for (Perl6Variable variable : variableDecl.getVariables()) {
            String varName = variable.getVariableName();
            // Don't visit anonymous ones
            if (varName.length() < 2)
                continue;
            myDuplicateVariablesPool.compute(varName, (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                else {
                    Perl6PsiScope newScope = PsiTreeUtil.getParentOfType(variableDecl, Perl6PsiScope.class);
                    for (Perl6Variable perl6Variable : v) {
                        Perl6PsiScope oldScope = PsiTreeUtil.getParentOfType(perl6Variable, Perl6PsiScope.class);
                        if (newScope != null && oldScope != null) {
                            if (PsiEquivalenceUtil.areElementsEquivalent(newScope, oldScope)) {
                                myHolder
                                    .add(getDuplicateHighlightInfo(v.get(0), variable.getTextRange(), varName, HighlightInfoType.WARNING));
                            }
                        }
                    }
                }
                v.add(variable);
                return v;
            });
        }
    }
}
