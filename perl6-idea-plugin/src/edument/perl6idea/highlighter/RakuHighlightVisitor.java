package edument.perl6idea.highlighter;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Pair;
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

@InternalIgnoreDependencyViolation
public class RakuHighlightVisitor extends RakuElementVisitor implements HighlightVisitor {
    private HighlightInfoHolder myHolder;
    private final Map<String, List<Perl6PsiElement>> ourScopedPackagesPool = new HashMap<>();
    private PsiFile myFile;

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
            myFile = file;
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
        Map<String, List<Perl6PsiElement>> duplicateClassesPool = new HashMap<>();
        Map<String, List<Perl6SignatureHolder>> duplicateRoutinesPool = new HashMap<>();
        Map<String, List<Perl6PsiElement>> duplicateVariablesPool = new HashMap<>();
        List<Perl6LexicalSymbolContributor> decls = element.getSymbolContributors();
        for (Perl6LexicalSymbolContributor contributor : decls) {
            if (contributor instanceof Perl6UseStatement) {
                visitUseStatement(duplicateClassesPool, duplicateRoutinesPool, contributor);
            }

            if (!(contributor instanceof Perl6PsiDeclaration decl))
                continue;

            if (decl instanceof Perl6RoutineDecl) {
                if (((Perl6RoutineDecl)decl).getRoutineName() == null || decl.getNameIdentifier() == null)
                    return;
                boolean wasReported = visitSignatureHolder(myHolder, (Perl6SignatureHolder)decl, ((Perl6RoutineDecl)decl).getRoutineName(),
                                                           duplicateRoutinesPool);
                if (!wasReported && ((Perl6RoutineDecl)decl).isSub() && Objects.equals(((Perl6RoutineDecl)decl).getMultiness(), "only")) {
                    // If a subroutine, expose `&foo` variable
                    TextRange textRange = new TextRange(((Perl6RoutineDecl)decl).getDeclaratorNode().getTextOffset(),
                                                        decl.getNameIdentifier().getTextRange().getEndOffset());
                    visitVariableDecl(
                        duplicateVariablesPool, new Perl6PsiElement[]{decl}, new String[]{"&" + ((Perl6RoutineDecl)decl).getRoutineName()},
                        new TextRange[]{textRange});
                }
            }
            else if (decl instanceof Perl6RegexDecl) {
                if (decl.getName() == null || decl.getNameIdentifier() == null)
                    return;
                visitSignatureHolder(myHolder, (Perl6SignatureHolder)decl, decl.getName(), duplicateRoutinesPool);
            }
            else if (decl instanceof Perl6VariableSource) {
                if (decl instanceof Perl6Parameter) {
                    if (PsiTreeUtil.getParentOfType(decl, Perl6VariableDecl.class, Perl6RoutineDecl.class) instanceof Perl6VariableDecl)
                        continue;
                    if (PsiTreeUtil.getParentOfType(decl, Perl6PointyBlock.class, Perl6RoutineDecl.class) instanceof Perl6PointyBlock)
                        continue;
                }
                visitVariableDecl(
                    duplicateVariablesPool, ((Perl6VariableSource)decl).getVariables(),
                    ((Perl6VariableSource)decl).getVariableNames(),
                    ContainerUtil.map2Array(((Perl6VariableSource)decl).getVariables(), TextRange.class, v -> v.getTextRange())
                );
            }
            else if (decl instanceof Perl6PackageDecl) {
                if (decl.getGlobalName() != null && !((Perl6PackageDecl)decl).isStubbed()) {
                    boolean marked = false;
                    if (decl.getScope().equals("our"))
                        marked = visitPackageDecl(decl, ourScopedPackagesPool, true);
                    visitPackageDecl(decl, duplicateClassesPool, !marked);
                }
            }
            else if (decl instanceof Perl6Subset) {
                if (decl.getGlobalName() != null) {
                    boolean marked = false;
                    if (decl.getScope().equals("our"))
                        marked = visitPackageDecl(decl, ourScopedPackagesPool, true);
                    visitPackageDecl(decl, duplicateClassesPool, !marked);
                }
            }
        }
    }

    private void visitUseStatement(Map<String, List<Perl6PsiElement>> packagesPool,
                                   Map<String, List<Perl6SignatureHolder>> routinesPool,
                                   Perl6LexicalSymbolContributor contributor) {
        Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(
            Perl6SymbolKind.TypeOrConstant, Perl6SymbolKind.Routine);
        contributor.contributeLexicalSymbols(collector);
        for (Perl6Symbol symbol : collector.getVariants()) {
            PsiElement psi = symbol.getPsi();
            if (psi instanceof Perl6PackageDecl && ((Perl6PackageDecl)psi).getGlobalName() != null) {
                visitPackageDecl((Perl6PackageDecl)psi, packagesPool, false);
            } else if (psi instanceof Perl6RoutineDecl) {
                visitSignatureHolder(myHolder, (Perl6SignatureHolder)psi, ((Perl6RoutineDecl)psi).getRoutineName(), routinesPool);
            }
        }
    }

    private boolean visitPackageDecl(Perl6PsiElement decl,
                                     Map<String, List<Perl6PsiElement>> pool,
                                     boolean shouldMark) {
        List<Perl6PsiElement> value = pool.compute(((Perl6PsiDeclaration)decl).getGlobalName(), (k, v) -> {
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

    private boolean markDuplicateValue(Perl6PsiElement decl, List<Perl6PsiElement> v) {
        AtomicBoolean marked = new AtomicBoolean(false);
        Optional<Perl6PsiElement> maxRedecl = v.stream()
            .filter(d -> d.getContainingFile().isEquivalentTo(decl.getContainingFile()))
            .max(Comparator.comparingInt(PsiElement::getTextOffset));
        if (maxRedecl.isEmpty())
            maxRedecl = v.stream().max(Comparator.comparingInt(PsiElement::getTextOffset));

        maxRedecl.ifPresent(redecl -> {
            if (redecl instanceof Perl6PackageDecl &&
                (((Perl6PackageDecl)redecl).getPackageKeywordNode() == null || ((Perl6PackageDecl)redecl).getNameIdentifier() == null) ||
                redecl instanceof Perl6Subset && ((Perl6Subset)redecl).getNameIdentifier() == null)
                return;
            TextRange range;
            //noinspection ConstantConditions
            range = new TextRange(redecl.getTextOffset(),
                                  ((Perl6PsiDeclaration)redecl).getNameIdentifier().getTextRange().getEndOffset());
            if (redecl instanceof Perl6PackageDecl && ((Perl6PackageDecl)redecl).getPackageKind().equals("role")) {
                Perl6Parameter[] newParams =
                    decl instanceof Perl6PackageDecl ? ((Perl6PackageDecl)decl).getSignature() : new Perl6Parameter[0];
                for (Perl6PsiElement role : v) {
                    if (role instanceof Perl6PackageDecl) {
                        Perl6Parameter[] oldParams = ((Perl6PackageDecl)role).getSignature();
                        // Different numbers of args
                        if (newParams.length == oldParams.length && role.getTextOffset() != decl.getTextOffset()) {
                            marked.set(true);
                            myHolder.add(getDuplicateHighlightInfo(
                                role, redecl, range,
                                ((Perl6PackageDecl)redecl).getGlobalName(), HighlightInfoType.ERROR));
                            break;
                        }
                    }
                }
            }
            else {
                Optional<Perl6PsiElement> minRedecl =
                    v.stream()
                        .filter(d -> !d.getContainingFile().isEquivalentTo(decl.getContainingFile()))
                        .min(Comparator.comparingInt(PsiElement::getTextOffset));
                if (minRedecl.isEmpty())
                    minRedecl = v.stream().min(Comparator.comparingInt(PsiElement::getTextOffset));
                minRedecl.ifPresent(packageDecl -> {
                    marked.set(true);
                    myHolder.add(
                        getDuplicateHighlightInfo(packageDecl, redecl, range, ((Perl6PsiDeclaration)packageDecl).getGlobalName(),
                                                  HighlightInfoType.ERROR));
                });
            }
        });
        return marked.get();
    }

    protected boolean visitSignatureHolder(HighlightInfoHolder infoHolder, Perl6SignatureHolder holder,
                                           String name, Map<String, List<Perl6SignatureHolder>> pool) {
        if (name == null)
            return false;
        return pool.compute(name, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            else {
                Pair<Perl6SignatureHolder, Perl6SignatureHolder> oldAndNewHolders = isElementInPool(holder, v);
                if (oldAndNewHolders != null) {
                    TextRange textRange = null;
                    if (oldAndNewHolders.second instanceof Perl6RoutineDecl decl) {
                        if (decl.getNameIdentifier() != null) {
                            PsiElement declaratorNode = decl.getDeclaratorNode();
                            if (declaratorNode != null)
                                textRange = new TextRange(declaratorNode.getTextOffset(),
                                                          decl.getNameIdentifier().getTextRange().getEndOffset());
                            else if (decl.getParent() instanceof Perl6MultiDecl) {
                                textRange = new TextRange(decl.getTextOffset(),
                                                          decl.getNameIdentifier().getTextRange().getEndOffset());
                            }
                        }
                    }
                    else if (oldAndNewHolders.second instanceof Perl6RegexDecl decl) {
                        if (decl.getNameIdentifier() != null)
                            textRange = new TextRange(decl.getTextOffset(),
                                                      decl.getNameIdentifier().getTextRange().getEndOffset());
                    }
                    if (textRange != null)
                        infoHolder
                            .add(getDuplicateHighlightInfo((Perl6PsiElement)oldAndNewHolders.first,
                                                           (PsiElement)holder, textRange, name, HighlightInfoType.ERROR));
                }
            }
            v.add(holder);
            return v;
        }).size() > 1;
    }

    private static Pair<Perl6SignatureHolder, Perl6SignatureHolder> isElementInPool(Perl6SignatureHolder holder, List<Perl6SignatureHolder> v) {
        String scope = holder instanceof Perl6PsiDeclaration ? ((Perl6PsiDeclaration)holder).getScope() : "";

        // For multi routines we need to compare signatures
        Perl6Parameter[] paramsNew =
            holder.getSignatureNode() == null ? new Perl6Parameter[0] : holder.getSignatureNode().getParameters();
        Perl6PsiScope newScope = PsiTreeUtil.getParentOfType((PsiElement)holder, Perl6PsiScope.class);
        List<Perl6SignatureHolder> matchingDecls = new ArrayList<>();
        ROUTINES:
        for (Perl6SignatureHolder checkedDecl : v) {
            if (holder == checkedDecl)
                continue;
            String checkedDeclScope = checkedDecl instanceof Perl6PsiDeclaration ? ((Perl6PsiDeclaration)checkedDecl).getScope() : "";
            if (!Objects.equals(checkedDeclScope, scope))
                continue;

            Perl6PsiScope oldScope = PsiTreeUtil.getParentOfType((PsiElement)checkedDecl, Perl6PsiScope.class);
            if (newScope != null && oldScope != null &&
                !(newScope instanceof Perl6File) && !(oldScope instanceof Perl6File) &&
                !PsiEquivalenceUtil.areElementsEquivalent(newScope, oldScope))
                continue;

            if (!Objects.equals(holder.getMultiness(), checkedDecl.getMultiness()))
                continue;
            if (Objects.equals(checkedDecl.getMultiness(), "only")) {
                matchingDecls.add(checkedDecl);
                continue;
            }
            Perl6Parameter[] paramsKnown =
                checkedDecl.getSignatureNode() == null ? new Perl6Parameter[0] : checkedDecl.getSignatureNode().getParameters();
            // No params == both are duplicates
            if (paramsKnown.length == 0 && paramsNew.length == 0) {
                matchingDecls.add(checkedDecl);
                continue;
            }
            // Different signature length - not a duplicate
            if (paramsKnown.length != paramsNew.length) {
                continue;
            }
            // Compare by type as precise as we can for now
            for (int j = 0; j < paramsKnown.length; j++) {
                Perl6Parameter parameter1 = paramsKnown[j];
                Perl6Parameter parameter2 = paramsNew[j];
                if (!parameter1.equalsParameter(parameter2)) {
                    continue ROUTINES;
                }
            }
            matchingDecls.add(checkedDecl);
            break;
        }

        if (!matchingDecls.isEmpty()) {
            matchingDecls.add(holder);
            Optional<PsiElement> maybeMin = matchingDecls.stream().filter(d -> d instanceof PsiElement)
                .map(d -> (PsiElement)d).min(Comparator.comparingInt(PsiElement::getTextOffset));
            Optional<PsiElement> maybeMax = matchingDecls.stream().filter(d -> d instanceof PsiElement)
                .map(d -> (PsiElement)d).max(Comparator.comparingInt(PsiElement::getTextOffset));
            if (maybeMin.isPresent() && maybeMax.isPresent())
                return Pair.create((Perl6SignatureHolder) maybeMin.get(), (Perl6SignatureHolder)maybeMax.get());
        }
        return null;
    }

    public void visitVariableDecl(Map<String, List<Perl6PsiElement>> pool,
                                  Perl6PsiElement[] variables,
                                  String[] variableNames,
                                  TextRange[] ranges) {
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
                        .add(getDuplicateHighlightInfo(originalDecl, variables[finalI], finalRange, varName,
                                                       varName.contains("!") ? HighlightInfoType.ERROR : HighlightInfoType.WARNING));
                }
                v.add(variables[finalI]);
                return v;
            });
        }
    }

    private HighlightInfo getDuplicateHighlightInfo(Perl6PsiElement originalDecl,
                                                           PsiElement currentDecl,
                                                           TextRange range,
                                                           String name, HighlightInfoType infoType) {
        if (!originalDecl.isValid())
            return null;
        if (!currentDecl.getContainingFile().equals(myFile))
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
