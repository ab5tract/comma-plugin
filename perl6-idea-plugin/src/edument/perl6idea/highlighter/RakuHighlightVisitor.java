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
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.*;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RakuHighlightVisitor extends RakuElementVisitor implements HighlightVisitor {
    private HighlightInfoHolder myHolder;
    private final Map<String, List<Perl6PackageDecl>> ourScopedPackagesPool = new THashMap<>();

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
            ourScopedPackagesPool.clear();
        }

        return true;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @NotNull HighlightVisitor clone() {
        return new RakuHighlightVisitor();
    }

    @Override
    public void visitScope(Perl6PsiScope element) {
        Map<String, List<Perl6PackageDecl>> duplicateClassesPool = new THashMap<>();
        Map<String, List<Perl6SignatureHolder>> duplicateRoutinesPool = new THashMap<>();
        Map<String, List<Perl6PsiElement>> duplicateVariablesPool = new THashMap<>();
        List<Perl6LexicalSymbolContributor> decls = element.getSymbolContributors();
        for (Perl6LexicalSymbolContributor contributor : decls) {
            if (contributor instanceof Perl6UseStatement) {
                visitUseStatement(duplicateClassesPool, contributor);
            }

            if (!(contributor instanceof Perl6PsiDeclaration))
                continue;
            Perl6PsiDeclaration decl = (Perl6PsiDeclaration)contributor;

            if (decl instanceof Perl6RoutineDecl) {
                if (((Perl6RoutineDecl)decl).getDeclaratorNode() == null ||
                    ((Perl6RoutineDecl)decl).getRoutineName() == null || decl.getNameIdentifier() == null)
                    return;
                TextRange textRange = new TextRange(((Perl6RoutineDecl)decl).getDeclaratorNode().getTextOffset(),
                                                    decl.getNameIdentifier().getTextRange().getEndOffset());
                boolean wasReported = visitSignatureHolder(myHolder, (Perl6SignatureHolder)decl, ((Perl6RoutineDecl)decl).getRoutineName(),
                                     textRange, duplicateRoutinesPool);
                if (!wasReported && ((Perl6RoutineDecl)decl).isSub() && ((Perl6RoutineDecl)decl).getMultiness() == "only") {
                    // If a subroutine, expose `&foo` variable
                    visitVariableDecl(
                        duplicateVariablesPool, new Perl6PsiElement[]{decl}, new String[]{"&" + ((Perl6RoutineDecl)decl).getRoutineName()},
                        new TextRange[]{textRange}, HighlightInfoType.ERROR);
                }
            }
            else if (decl instanceof Perl6RegexDecl) {
                if (decl.getName() == null || decl.getNameIdentifier() == null)
                    return;
                TextRange textRange = new TextRange(decl.getTextOffset(), decl.getNameIdentifier().getTextRange().getEndOffset());
                visitSignatureHolder(myHolder, (Perl6SignatureHolder)decl, decl.getName(),
                                     textRange, duplicateRoutinesPool);
            }
            else if (decl instanceof Perl6VariableSource) {
                visitVariableDecl(
                    duplicateVariablesPool, ((Perl6VariableSource)decl).getVariables(),
                    ((Perl6VariableSource)decl).getVariableNames(),
                    ContainerUtil.map2Array(((Perl6VariableSource)decl).getVariables(), TextRange.class, v -> v.getTextRange()),
                    HighlightInfoType.WARNING);
            }
            else if (decl instanceof Perl6PackageDecl) {
                if (decl.getGlobalName() != null && !((Perl6PackageDecl)decl).isStubbed()) {
                    boolean marked = false;
                    if (decl.getScope().equals("our"))
                        marked = visitPackageDecl((Perl6PackageDecl)decl, ourScopedPackagesPool, true);
                    visitPackageDecl((Perl6PackageDecl)decl, duplicateClassesPool, !marked);
                }
            }
        }
    }

    private void visitUseStatement(Map<String, List<Perl6PackageDecl>> duplicateClassesPool, Perl6LexicalSymbolContributor contributor) {
        Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(
            Perl6SymbolKind.TypeOrConstant, Perl6SymbolKind.Routine);
        contributor.contributeLexicalSymbols(collector);
        for (Perl6Symbol symbol : collector.getVariants()) {
            PsiElement psi = symbol.getPsi();
            if (psi instanceof Perl6PackageDecl && ((Perl6PackageDecl)psi).getGlobalName() != null) {
                visitPackageDecl((Perl6PackageDecl)psi, duplicateClassesPool, false);
            }
        }
    }

    private boolean visitPackageDecl(Perl6PackageDecl decl,
                                     Map<String, List<Perl6PackageDecl>> pool,
                                     boolean shouldMark) {
        List<Perl6PackageDecl> value = pool.compute(decl.getGlobalName(), (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(decl);
            return v;
        });
        if (shouldMark && value.size() > 1)
            return markDuplicateValue(decl, value);
        return false;
    }

    private boolean markDuplicateValue(Perl6PackageDecl decl, List<Perl6PackageDecl> v) {
        AtomicBoolean marked = new AtomicBoolean(false);
        Optional<Perl6PackageDecl> maxRedecl = v.stream()
            .filter(d -> d.getContainingFile().isEquivalentTo(decl.getContainingFile()))
            .max(Comparator.comparingInt(PsiElement::getTextOffset));
        if (maxRedecl.isEmpty())
            maxRedecl = v.stream().max(Comparator.comparingInt(PsiElement::getTextOffset));

        maxRedecl.ifPresent(redecl -> {
            if (redecl.getPackageKeywordNode() == null || redecl.getNameIdentifier() == null)
                return;
            TextRange range = new TextRange(redecl.getPackageKeywordNode().getTextOffset(),
                                            redecl.getNameIdentifier().getTextRange().getEndOffset());
            Optional<Perl6PackageDecl> minRedecl =
                v.stream()
                    .filter(d -> !d.getContainingFile().isEquivalentTo(decl.getContainingFile()))
                    .min(Comparator.comparingInt(PsiElement::getTextOffset));
            if (minRedecl.isEmpty())
                minRedecl = v.stream().min(Comparator.comparingInt(PsiElement::getTextOffset));
            minRedecl.ifPresent(packageDecl -> {
                if (packageDecl.getPackageKind().equals("role")) {
                    Perl6Parameter[] newParams = decl.getSignature();
                    for (Perl6PackageDecl role : v) {
                        Perl6Parameter[] oldParams = role.getSignature();
                        // Different numbers of args
                        if (newParams.length == oldParams.length && role.getTextOffset() != decl.getTextOffset()) {
                            marked.set(true);
                            myHolder.add(getDuplicateHighlightInfo(packageDecl, range, packageDecl.getGlobalName(),
                                                                   HighlightInfoType.ERROR));
                        }
                    }
                }
                else {
                    marked.set(true);
                    myHolder.add(
                        getDuplicateHighlightInfo(packageDecl, range, packageDecl.getGlobalName(), HighlightInfoType.ERROR));
                }
            });
        });
        return marked.get();
    }

    protected boolean visitSignatureHolder(HighlightInfoHolder infoHolder, Perl6SignatureHolder holder,
                                        String name, TextRange textRange,
                                        Map<String, List<Perl6SignatureHolder>> pool) {
        if (name == null || textRange == null)
            return false;
        return pool.compute(name, (k, v) -> {
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
        }).size() > 1;
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

    public void visitVariableDecl(Map<String, List<Perl6PsiElement>> pool,
                                  Perl6PsiElement[] variables,
                                  String[] variableNames,
                                  TextRange[] ranges, HighlightInfoType level) {
        for (int i = 0; i < variables.length; i++) {
            String varName = variableNames[i];
            // Don't visit anonymous ones or dynamic
            if (varName == null || varName.length() < 2 || varName.contains("*")) {
                continue;
            }
            int finalI = i;
            pool.compute(varName, (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                else {
                    TextRange finalRange = ranges[finalI];
                    Perl6PsiElement originalDecl = variables[finalI];
                    for (Perl6PsiElement decl : v) {
                        if (!PsiEquivalenceUtil.areElementsEquivalent(decl.getContainingFile(), variables[finalI].getContainingFile())) {
                            finalRange = decl.getTextRange();
                            originalDecl = decl;
                            break;
                        }
                        else {
                            if (finalRange.getEndOffset() < decl.getTextRange().getEndOffset()) {
                                finalRange = decl.getTextRange();
                            }
                            if (originalDecl.getTextOffset() > decl.getTextOffset())
                                originalDecl = decl;
                        }
                    }
                    myHolder
                        .add(getDuplicateHighlightInfo(originalDecl, finalRange, varName, level));
                }
                v.add(variables[finalI]);
                return v;
            });
        }
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
}
