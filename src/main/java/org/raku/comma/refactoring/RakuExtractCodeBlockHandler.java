package org.raku.comma.refactoring;

import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.external.ExternalRakuRoutineDecl;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.raku.comma.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.raku.comma.psi.RakuElementFactory.createNamedCodeBlock;

public class RakuExtractCodeBlockHandler implements RefactoringActionHandler, ContextAwareActionHandler {
    private static final String TITLE = "Code Block Extraction";
    private static final List<String> packageTypesWithMethods = new ArrayList<>(
            Arrays.asList("class", "role", "grammar", "monitor"));
    protected RakuCodeBlockType myCodeBlockType;
    private List<RakuStatementList> myScopes;
    private boolean selfIsPassed = false;
    protected boolean isExpr = false;
    public static final Pattern REGEX_DRIVEN_VARIABLES_PATTERN = Pattern.compile("^\\$\\d+$");

    public RakuExtractCodeBlockHandler(RakuCodeBlockType type) {
        myCodeBlockType = type;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        PsiElement[] elementsToExtract = getElementsToExtract(file, editor);
        invokeWithStatements(project, editor, file, elementsToExtract);
    }

    protected void invokeWithStatements(@NotNull Project project, Editor editor, PsiFile file, PsiElement[] elementsToExtract) {
        if (elementsToExtract.length == 0)
            return;

        // Gets a parent scope for new block according to callback-based API
        myScopes = getPossibleScopes(elementsToExtract);
        myScopes = handleZeroScopes(project, editor, elementsToExtract);
        if (myScopes.size() == 0) {
            reportError(project, editor, "Cannot extract selected statements as there are no possible scopes present");
            return;
        }
        if (myScopes.size() == 1) {
            invokeWithScope(project, editor, myScopes.get(0), elementsToExtract);
        }
        else {
            IntroduceTargetChooser.showChooser(editor, myScopes, new Pass<>() {
                @Override
                public void pass(RakuStatementList scope) {
                    invokeWithScope(project, editor, scope, elementsToExtract);
                }
            }, RakuBlockRenderer::renderBlock, "Select creation scope");
        }
    }

    private List<RakuStatementList> handleZeroScopes(@NotNull Project project,
                                                     Editor editor,
                                                     PsiElement[] elements) {
        if (myScopes.size() == 0) {
            if (myCodeBlockType == RakuCodeBlockType.ROUTINE) {
                reportError(project, editor, "Cannot extract selected statements as there are no possible scopes present");
                return new ArrayList<>();
            }
            else {
                // In case if user mis-typed, offer to create a sub instead
                DialogBuilder builder = new DialogBuilder();
                builder.setTitle("Extract Subroutine?");
                // FIXME A better presentation here is needed
                builder.setCenterPanel(new JLabel("It is impossible to extract a method using selected statements. Would you like to extract a subroutine instead?"));
                if (builder.showAndGet()) {
                    myCodeBlockType = RakuCodeBlockType.ROUTINE;
                    return getPossibleScopes(elements);
                }
            }
        }
        return myScopes;
    }

    protected void invokeWithScope(@NotNull Project project,
                                   Editor editor,
                                   RakuStatementList parentToCreateAt,
                                   PsiElement[] elements) {
        if (checkElementsSanity(project, editor, parentToCreateAt, elements)) return;
        if (checkMethodSanity(project, editor, parentToCreateAt)) return;

        /* Anchor represents an element, that will be a next to created one or null,
         * if no such anchor is possible, e.g. when a method is added after last one in a class */
        PsiElement anchor = getAnchor(parentToCreateAt, elements);

        NewCodeBlockData newCodeBlockData = getNewBlockData(project, parentToCreateAt, elements);
        // If user cancelled action or exception occurred
        if (newCodeBlockData == null) return;
        List<String> contents = Arrays.stream(elements).map(PsiElement::getText).collect(Collectors.toList());
        PsiElement newBlock = createNewBlock(project, newCodeBlockData, contents);
        insertNewCodeBlock(project, parentToCreateAt, newBlock, anchor);
        replaceStatementsWithCall(project, newCodeBlockData, parentToCreateAt, elements);
    }

    private static boolean checkElementsSanity(@NotNull Project project, Editor editor, PsiElement parentScope, PsiElement[] elements) {
        if (elements.length == 0 || parentScope == null) {
            reportError(project, editor, "Cannot extract code");
            return true;
        }
        return false;
    }

    private boolean checkMethodSanity(@NotNull Project project, Editor editor, PsiElement parentScope) {
        // Check if method outside of class creation
        if (myCodeBlockType == RakuCodeBlockType.METHOD || myCodeBlockType == RakuCodeBlockType.PRIVATEMETHOD) {
            PsiElement wrapper = PsiTreeUtil.getParentOfType(parentScope,
                                                             RakuFile.class, RakuRoutineDecl.class, RakuPackageDecl.class);
            if (wrapper instanceof RakuPackageDecl) {
                String scopeKind = ((RakuPackageDecl) wrapper).getPackageKind();
                if (!packageTypesWithMethods.contains(scopeKind)) {
                    reportError(project, editor, "Cannot extract a method into " + scopeKind);
                    return true;
                }
            } else {
                reportError(project, editor, "Cannot extract a method outside of a class, a monitor, a grammar or a role");
                return true;
            }

        }
        return false;
    }

    protected PsiElement[] getElementsToExtract(PsiFile file, Editor editor) {
        if (editor.getSelectionModel().hasSelection()) {
            return getElementsFromSelection(file, editor);
        } else {
            return getElementsFromCaret(file, editor);
        }
    }

    private PsiElement[] getElementsFromSelection(PsiFile file, Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        PsiElement startLeaf = file.findElementAt(selectionModel.getSelectionStart());
        PsiElement endLeaf = file.findElementAt(selectionModel.getSelectionEnd() - 1);
        PsiElement start = PsiTreeUtil.getNonStrictParentOfType(RakuPsiUtil.skipSpaces(startLeaf, true), RakuStatement.class, RakuHeredoc.class);
        PsiElement end = PsiTreeUtil.getNonStrictParentOfType(RakuPsiUtil.skipSpaces(endLeaf, false), RakuStatement.class, RakuHeredoc.class);

        if (start == null || end == null) {
            if (end != null) {
                return new PsiElement[]{end};
            }
            else if (start != null) {
                return new PsiElement[]{start};
            }
            else {
                return PsiElement.EMPTY_ARRAY;
            }
        }

        // If a single statement is selected, we want to offer
        // expressions too
        if (Objects.equals(start, end)) {
            // Leafs are never null if neither start and end is null
            assert startLeaf != null && endLeaf != null;
            PsiElement commonParent = PsiTreeUtil.findCommonParent(startLeaf, endLeaf);
            if (commonParent instanceof RakuStatement)
                return new PsiElement[]{commonParent};
            return commonParent == null ? PsiElement.EMPTY_ARRAY : getExpressionsFromSelection(file, editor, commonParent, start);
        }

        if (PsiTreeUtil.isAncestor(start, end, true)) {
            return new PsiElement[]{start};
        }
        else if (PsiTreeUtil.isAncestor(end, start, true)) {
            return new PsiElement[]{end};
        }

        List<PsiElement> elements = new ArrayList<>();
        while (start != end) {
            elements.add(start);
            start = start.getNextSibling();
        }
        elements.add(end);
        return elements.toArray(PsiElement.EMPTY_ARRAY);
    }

    protected PsiElement[] getExpressionsFromSelection(PsiFile file, Editor editor, @NotNull PsiElement commonParent, PsiElement fullStatementBackup) {
        if (commonParent instanceof RakuExtractable) {
            List<PsiElement> targets = getExpressionTargets(commonParent);
            IntroduceTargetChooser.showChooser(editor, targets, new Pass<>() {
                @Override
                public void pass(PsiElement element) {
                    isExpr = !(element instanceof RakuStatement);
                    invokeWithStatements(element.getProject(), editor, file, new PsiElement[]{element});
                }
            }, PsiElement::getText, "Select expression to extract");
            return PsiElement.EMPTY_ARRAY;
        } else {
            return new PsiElement[]{fullStatementBackup};
        }
    }

    @NotNull
    protected static List<PsiElement> getExpressionTargets(@NotNull PsiElement commonParent) {
        List<PsiElement> targets = new ArrayList<>();
        if (commonParent.getParent() instanceof RakuStatement) {
            targets.add(commonParent.getParent());
            return targets;
        }
        else {
            while (!(commonParent instanceof RakuStatementList)) {
                if (commonParent instanceof RakuExtractable && !(commonParent.getParent() instanceof RakuStatement))
                    targets.add(commonParent);
                commonParent = commonParent.getParent();
            }
        }
        return filterOutFalsePositiveExtractee(targets);
    }

    @NotNull
    private static List<PsiElement> filterOutFalsePositiveExtractee(List<PsiElement> targets) {
        targets = ContainerUtil.filter(targets, target -> {
            if (target instanceof RakuTypeName) {
                return !(target.getParent() instanceof RakuParameter) && !(target.getParent() instanceof RakuScopedDecl);
            } else if (target instanceof RakuVariable) {
                return !(target.getParent() instanceof RakuVariableDecl) && !(target.getParent() instanceof RakuParameterVariable);
            }
            return true;
        });
        return targets;
    }

    protected PsiElement[] getElementsFromCaret(PsiFile file, Editor editor) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return PsiElement.EMPTY_ARRAY;
        }
        List<PsiElement> targets = getExpressionTargets(element.getParent());
        if (targets.isEmpty())
            return PsiElement.EMPTY_ARRAY;
        IntroduceTargetChooser.showChooser(editor, targets, new Pass<>() {
            @Override
            public void pass(PsiElement element) {
                isExpr = !(element instanceof RakuStatement);
                invokeWithStatements(element.getProject(), editor, file, new PsiElement[]{element});
            }
        }, PsiElement::getText, "Select Expression to Extract");
        return PsiElement.EMPTY_ARRAY;
    }

    protected List<RakuStatementList> getPossibleScopes(PsiElement[] elements) {
        PsiElement commonParent = PsiTreeUtil.findCommonParent(elements);
        PsiElement list = PsiTreeUtil.getNonStrictParentOfType(commonParent, RakuPsiScope.class);
        if (list == null) {
            return new ArrayList<>();
        }

        List<RakuStatementList> scopes = new ArrayList<>();

        // Subroutine can be in any scope above, but methods are restricted
        if (myCodeBlockType != RakuCodeBlockType.ROUTINE) {
            while (true) {
                RakuPackageDecl methodScope = PsiTreeUtil.getParentOfType(list, RakuPackageDecl.class);
                if (methodScope == null) break;
                String packageKind = methodScope.getPackageKind();
                if (packageTypesWithMethods.contains(packageKind)) {
                    RakuStatementList newList = PsiTreeUtil.findChildOfType(methodScope, RakuStatementList.class);
                    if (newList != null) {
                        scopes.add(newList);
                        list = methodScope;
                    } else {
                        break;
                    }
                } else {
                    list = methodScope;
                }
            }
        } else {
            while (list != null) {
                RakuStatementList statementList = PsiTreeUtil.findChildOfType(list, RakuStatementList.class);
                if (statementList == null) break;
                scopes.add(statementList);
                list = PsiTreeUtil.getParentOfType(list, RakuPsiScope.class);
            }
        }
        return scopes;
    }

    protected PsiElement getAnchor(RakuStatementList parentToCreateAt, PsiElement[] elements) {
        PsiElement commonParent = PsiTreeUtil.findCommonParent(elements);
        if (parentToCreateAt == commonParent) return elements[0];

        return PsiTreeUtil.findFirstParent(commonParent, e -> e.getParent() == parentToCreateAt);
    }

    protected NewCodeBlockData getNewBlockData(Project project, RakuStatementList parentToCreateAt, PsiElement[] elements) {
        CompletableFuture<NewCodeBlockData> futureData = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            RakuExtractBlockDialog
              dialog = new RakuExtractBlockDialog(project, TITLE, myCodeBlockType, getCapturedVariables(parentToCreateAt, elements)) {
                @Override
                protected void doAction() {
                    NewCodeBlockData data = new NewCodeBlockData(
                            myCodeBlockType, getScope(),
                            getName(), getReturnType(),
                            getInputVariables()
                    );
                    data.containsExpression = isExpr;
                    data.wantsSemicolon = isExpr && elements.length == 1 && checkNeedsSemicolon(elements[0]);
                    futureData.complete(data);
                    closeOKAction();
                }

                @Override
                public void doCancelAction() {
                    futureData.complete(null);
                    close(1);
                }
            };
            dialog.show();
        });
        try {
            return futureData.get();
        }
        catch (InterruptedException|ExecutionException e) {
            return null;
        }
    }

    protected boolean checkNeedsSemicolon(PsiElement element) {
        RakuStatement statement = PsiTreeUtil.getParentOfType(element, RakuStatement.class);
        if (statement == null) return false; // won't happen, because we already checked isExpr in the caller
        return statement.getNextSibling() != null && element.getNextSibling() == null;
    }

    protected RakuVariableData[] getCapturedVariables(RakuStatementList parentToCreateAt, PsiElement[] elements) {
        List<RakuVariableData> capturedVariables = new ArrayList<>();

        // Check self usage
        if (checkSelfUsage(elements, parentToCreateAt)) {
            selfIsPassed = true;
            capturedVariables.add(new RakuVariableData("self", "$self", "", false, true));
        }

        // Check ordinary variables and attributes usage
        List<RakuVariable> usedVariables = collectVariablesInStatements(elements);
        List<RakuVariable> deduplicatedVars = new ArrayList<>();

        /*
        Here we need to de-duplicate collected variables
        As our most truthful check of declaration
        may result in null, we additionally check
        variable's name, so altogether it looks quite expensive,
        but considering that variable list rarely is too large and
        the refactoring call is not a hot operation, it is likely good enough
        */
        for (RakuVariable var : usedVariables) {
            PsiReference ref = var.getReference();
            assert ref != null;
            PsiElement decl = ref.resolve();
            String name = var.getVariableName();

            boolean isDuplicate = false;
            for (RakuVariable each : deduplicatedVars) {
                if (Objects.equals(each.getVariableName(), name)) {
                    PsiReference eachRef = each.getReference();
                    assert eachRef != null;
                    PsiElement eachDecl = eachRef.resolve();
                    if (Objects.equals(decl, eachDecl)) {
                        isDuplicate = true;
                    }
                }
            }
            if (!isDuplicate) {
                deduplicatedVars.add(var);
            }
        }

        for (RakuVariable usedVariable : deduplicatedVars) {
            char twigil = RakuVariable.getTwigil(usedVariable.getVariableName());
            if (REGEX_DRIVEN_VARIABLES_PATTERN.matcher(usedVariable.getVariableName()).matches()) {
                continue;
            }
            if (twigil != '!') {
                RakuVariableData variableCapture = checkIfLexicalVariableCaptured(parentToCreateAt, elements, usedVariable);
                if (variableCapture != null)
                    capturedVariables.add(variableCapture);
            }
            else {
                if (checkIfAttributeCaptured(parentToCreateAt, elements)) {
                    String type = usedVariable.getVariableName().startsWith("$") ? usedVariable.inferType().nominalType().getName() : "";
                    capturedVariables.add(new RakuVariableData(usedVariable.getVariableName(), usedVariable.getVariableName().replace("!", ""),
                                                                type, false, true));
                }
            }
        }

        // Check lexical subs usage
        for (RakuSubCallName call : collectCallsInStatements(elements)) {
            if (checkIfLexicalSubCaptured(parentToCreateAt, elements, call)) {
                capturedVariables.add(new RakuVariableData("&" + call.getCallName(), "", false, true));
            }
        }

        return capturedVariables.toArray(new RakuVariableData[0]);
    }

    private static boolean checkIfLexicalSubCaptured(RakuStatementList parentToCreateAt,
                                                     PsiElement[] elements,
                                                     RakuSubCallName call) {
        // Here, we iterate each call found
        PsiReference ref = call.getReference();
        assert ref != null;
        PsiElement decl = ref.resolve();
        // Declaration that the call invokes
        // if there is no declaration, it's not local
        if (decl == null || decl instanceof ExternalRakuRoutineDecl)
            return false;

        // If declared as one of the statements - skip this particular call name
        // as it will be still accessible after extraction anyway
        for (PsiElement statement : elements) {
            if (PsiTreeUtil.isAncestor(statement, decl, false))
                return false;
        }

        // Try to see if we have such routine accessible in new scope
        RakuSymbol routineSymbol = parentToCreateAt.resolveLexicalSymbol(RakuSymbolKind.Routine, call.getCallName());
        // If it is not or if it points to another routine with the same name, pass a lexical sub in
        return routineSymbol == null || routineSymbol.getPsi() == null || !call.getManager()
                                                                               .areElementsEquivalent(routineSymbol.getPsi(), decl);
    }

    private boolean checkIfAttributeCaptured(RakuStatementList parentToCreateAt, PsiElement[] elements) {
        // We are checking if attribute has to be passed to a newly created block or not
        // Passing is *not* necessary if either:
        // * new block == lexical sub of the method in the same class
        PsiElement commonParentOfOriginalElements = PsiTreeUtil.getNonStrictParentOfType(PsiTreeUtil.findCommonParent(elements), RakuPsiScope.class);
        PsiElement scopeToCreateAt = PsiTreeUtil.getParentOfType(parentToCreateAt, RakuPsiScope.class);

        if (myCodeBlockType == RakuCodeBlockType.ROUTINE) {
            while (true) {
                if (scopeToCreateAt instanceof RakuRoutineDecl) {
                    if (((RakuRoutineDecl) scopeToCreateAt).getRoutineKind().equals("method")) {
                        return false;
                    } else {
                        scopeToCreateAt = PsiTreeUtil.getNonStrictParentOfType(scopeToCreateAt, RakuPsiScope.class);
                    }
                } else if (scopeToCreateAt instanceof RakuPackageDecl) {
                    return true;
                } else {
                    if (scopeToCreateAt == null) break;
                    scopeToCreateAt = PsiTreeUtil.getNonStrictParentOfType(scopeToCreateAt, RakuPsiScope.class);
                }
            }
        } else { // * new block == method in the same class
            // Class that surrounds created method
            RakuPackageDecl outermostClassBody = PsiTreeUtil.getParentOfType(parentToCreateAt, RakuPackageDecl.class);
            return !Objects.equals(PsiTreeUtil.getNonStrictParentOfType(commonParentOfOriginalElements, RakuPackageDecl.class), outermostClassBody);
        }
        return true;
    }

    private boolean checkSelfUsage(PsiElement[] statements, RakuStatementList parentToCreateAt) {
        boolean selfPresence = Arrays.stream(statements)
            .flatMap(el -> getUsages(el, RakuSelf.class, RakuPackageDecl.class).stream())
            .map(el -> (RakuSelf)el).findAny().isPresent();
        if (!selfPresence) return false;
        // If self is present, we have to check if we are extracting stuff into new method of the same class
        // If we are extracting into a sub, just short-circuit as we always pass $self there
        if (myCodeBlockType == RakuCodeBlockType.ROUTINE) return true;
        // otherwise check seriously:
        RakuPackageDecl outerClassBody = PsiTreeUtil.getParentOfType(PsiTreeUtil.findCommonParent(statements), RakuPackageDecl.class);
        RakuPackageDecl outermostClassBody = PsiTreeUtil.getParentOfType(parentToCreateAt, RakuPackageDecl.class);
        return !Objects.equals(outerClassBody, outermostClassBody);
    }

    private static RakuVariableData checkIfLexicalVariableCaptured(RakuStatementList parentToCreateAt, PsiElement[] statements, RakuVariable usedVariable) {
        // First, check if the variable is defined locally in statements we are extracting
        PsiReference originalRef = usedVariable.getReference();
        assert originalRef != null;
        PsiElement usedVariableDeclaration = originalRef.resolve();
        if (usedVariableDeclaration != null) {
            for (PsiElement statement : statements) {
                if (PsiTreeUtil.isAncestor(statement, usedVariableDeclaration, true)) {
                    return null;
                }
            }
        }

        // We are checking whether a variable will be available from outer scope in scope where new block is created
        boolean isAvailableLexically = parentToCreateAt.resolveLexicalSymbol(RakuSymbolKind.Variable, usedVariable.getVariableName()) != null;
        String type = usedVariable.getVariableName().startsWith("$") ? usedVariable.inferType().nominalType().getName() : "";
        return new RakuVariableData(usedVariable.getVariableName(), type, isAvailableLexically, !isAvailableLexically);
    }

    private static List<RakuSubCallName> collectCallsInStatements(PsiElement[] elements) {
        return Arrays.stream(elements)
                .flatMap(el -> PsiTreeUtil.findChildrenOfType(el, RakuSubCallName.class).stream())
                .collect(Collectors.toList());
    }

    private static List<RakuVariable> collectVariablesInStatements(PsiElement[] elements) {
        return Arrays.stream(elements)
                     .flatMap(el -> PsiTreeUtil.findChildrenOfType(el, RakuVariable.class).stream())
                     .collect(Collectors.toList());
    }

    @NotNull
    private static Collection<PsiElement> getUsages(PsiElement statement, Class usageClazz, Class declClazz) {
        // It is an imitation of `getChildrenOfType` specialised for variables that avoids declarations
        // as we do have to differentiate between RakuVariable used in an expression and
        // inside of a variable declaration
        if (statement == null) return ContainerUtil.emptyList();

        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (usageClazz.isInstance(each)) {
                        return super.execute(each);
                    }
                    else if (declClazz.isInstance(each)) {
                        return false;
                    }
                    return true;
                }
            };
        PsiTreeUtil.processElements(statement, processor);
        return processor.getCollection();
    }

    protected PsiElement createNewBlock(Project project, NewCodeBlockData data, List<String> contents) {
        return postProcessVariables(project, data.variables, createNamedCodeBlock(project, data, contents));
    }

    private PsiElement postProcessVariables(Project project,
                                            RakuVariableData[] variables,
                                            RakuStatement block) {
        // Post-process references to self
        if (selfIsPassed)
            getUsages(block, RakuSelf.class, RakuPackageDecl.class)
                    .forEach(el -> el.replace(RakuElementFactory.createVariable(project, "$self")));
        // Post-process variables that were renamed
        for (RakuVariableData data : variables) {
            if (data.parameterName.equals(data.originalName))
                continue;
            renameVariableInBlock(project, block,
                                  (var) -> Objects.equals(var.getVariableName(), data.originalName),
                                  (var) -> data.parameterName);
        }
        return block;
    }

    private static void renameVariableInBlock(Project project,
                                              RakuStatement block,
                                              Function<RakuVariable, Boolean> check,
                                              Function<RakuVariable, String> newName) {
        collectVariablesInStatements(new PsiElement[]{block})
            .stream().filter((var) -> check.fun(var))
            .forEach(var -> var.replace(
              RakuElementFactory.createVariable(
                    project,
                    newName.fun(var))));
    }

    protected void insertNewCodeBlock(Project project, PsiElement parent,
                                      PsiElement newBlock, PsiElement anchor) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (myCodeBlockType == RakuCodeBlockType.ROUTINE) {
                parent.addBefore(newBlock, anchor);
            } else {
                parent.addAfter(newBlock, anchor);
            }
        });
    }

    protected void replaceStatementsWithCall(Project project, NewCodeBlockData data, PsiElement parentScope, PsiElement[] elements) {
        PsiElement call;
        if (data.type == RakuCodeBlockType.ROUTINE) {
            call = CompleteRakuElementFactory.createSubCall(parentScope.getProject(), data);
        } else {
            call = CompleteRakuElementFactory.createMethodCall(parentScope.getProject(), data);
        }

        PsiElement finalCall = call;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement closestParent = elements[0].getParent();
            // Insert a call
            PsiElement newCall = closestParent.addBefore(finalCall, elements[elements.length - 1].getNextSibling());
            // Delete present statements
            closestParent.deleteChildRange(elements[0], elements[elements.length - 1]);
            if (data.wantsSemicolon) {
                RakuStatement statement = PsiTreeUtil.getParentOfType(newCall, RakuStatement.class);
                if (statement != null)
                    RakuPsiUtil.terminateStatement(statement);
            }
        });
    }

    private static void reportError(Project project, Editor editor, String message) {
        CommonRefactoringUtil.showErrorHint(project, editor, message, TITLE, null);
    }

    @Override
    public boolean isAvailableForQuickList(@NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext) {
        return false;
    }
}
