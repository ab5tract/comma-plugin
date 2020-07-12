package edument.perl6idea.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Perl6ExtractRegexPartHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        PsiElement[] elementsToExtract = getPartsToExtract(file, editor);
        invokeWithParts(project, editor.getDocument(), elementsToExtract);
    }

    private void invokeWithParts(Project project, Document document, PsiElement[] atoms) {
        if (atoms.length == 0)
            return;

        PsiElement anchor = getAnchor(atoms);
        if (anchor == null) return;
        Perl6RegexDriver regex = PsiTreeUtil.getParentOfType(atoms[0], Perl6RegexDriver.class);
        Perl6RegexDecl regexDecl = PsiTreeUtil.getParentOfType(regex, Perl6RegexDecl.class);
        if (regex == null) return;
        Perl6PsiScope parentToCreateAt = PsiTreeUtil.getParentOfType(anchor, Perl6PsiScope.class);
        if (parentToCreateAt == null) return;
        Perl6PackageDecl parentPackage = PsiTreeUtil.getParentOfType(regex, Perl6PackageDecl.class);
        boolean isLexical = parentPackage == null || !parentPackage.getPackageKind().equals("grammar");
        Perl6RegexPartType parentType = isLexical || regexDecl == null ? Perl6RegexPartType.REGEX :
                                        convertRegexTypes(regexDecl.getRegexKind());
        NewRegexPartData data = getNewRegexPartData(project, parentToCreateAt, atoms, isLexical, parentType);
        if (data == null) return;
        PsiElement newRegex = createNewRegex(project, data, atoms);
        WriteCommandAction.runWriteCommandAction(project, "Extract Regex", null, () -> {
            PsiElement parent = anchor.getParent();
            parent.addBefore(newRegex, anchor);
            parent.addBefore(Perl6ElementFactory.createNewLine(project), anchor);
            replaceNodesWithCall(project, data, atoms);
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
            CodeStyleManager.getInstance(project).reformat(parent);
        });
    }

    private static Perl6RegexPartType convertRegexTypes(String kind) {
        switch (kind) {
            case "token": return Perl6RegexPartType.TOKEN;
            case "rule": return Perl6RegexPartType.RULE;
            default: return Perl6RegexPartType.REGEX;
        }
    }

    private static void replaceNodesWithCall(Project project, NewRegexPartData data, PsiElement[] atoms) {
        String name = data.isCapture ? data.name : data.isLexical ? "&" + data.name : "." + data.name;
        Perl6RegexAtom newCall = CompletePerl6ElementFactory.createRegexCall(project, name);
        PsiElement parent = PsiTreeUtil.findCommonParent(atoms);
        assert parent != null;
        atoms[atoms.length - 1].replace(newCall);
        if (atoms.length > 1)
            parent.deleteChildRange(atoms[0], atoms[atoms.length - 2]);
    }

    private static PsiElement createNewRegex(Project project, NewRegexPartData data, PsiElement[] atoms) {
        return CompletePerl6ElementFactory.createRegexPart(project, data, atoms);
    }

    protected NewRegexPartData getNewRegexPartData(Project project, Perl6PsiScope parentToCreateAt, PsiElement[] atoms, boolean isLexical,
                                                   Perl6RegexPartType parentType) {
        CompletableFuture<NewRegexPartData> futureData = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            Perl6ExtractRegexDialog dialog = new Perl6ExtractRegexDialog(project, getCapturedVariables(parentToCreateAt, atoms), atoms, isLexical, parentType) {
                @Override
                protected void doAction() {
                    futureData.complete(new NewRegexPartData(getType(), getNewRegexName(),
                                                             getSignatureParameterBlock(),
                                                             getCaptureStatus(), isLexical,
                                                             parentType));
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

    protected Perl6VariableData[] getCapturedVariables(Perl6PsiScope parentToCreateAt, PsiElement[] elements) {
        List<Perl6VariableData> capturedVariables = new ArrayList<>();

        // Check ordinary variables and attributes usage
        List<Perl6Variable> usedVariables = collectVariablesInStatements(elements);
        List<Perl6Variable> deduplicatedVars = new ArrayList<>();

        /*
        Here we need to de-duplicate collected variables
        As our most truthful check of declaration
        may result in null, we additionally check
        variable's name, so altogether it looks quite expensive,
        but considering that variable list rarely is too large and
        the refactoring call is not a hot operation, it is likely good enough
        */
        for (Perl6Variable var : usedVariables) {
            PsiReference ref = var.getReference();
            assert ref != null;
            PsiElement decl = ref.resolve();
            String name = var.getVariableName();

            boolean isDuplicate = false;
            for (Perl6Variable each : deduplicatedVars) {
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

        for (Perl6Variable usedVariable : deduplicatedVars) {
            char twigil = Perl6Variable.getTwigil(usedVariable.getVariableName());
            if (Perl6ExtractCodeBlockHandler.REGEX_DRIVEN_VARIABLES_PATTERN.matcher(usedVariable.getVariableName()).matches()) {
                continue;
            }
            if (twigil != '!' && twigil != '*') {
                Perl6VariableData variableCapture = checkIfLexicalVariableCaptured(parentToCreateAt, elements, usedVariable);
                if (variableCapture != null)
                    capturedVariables.add(variableCapture);
            }
        }
        return capturedVariables.toArray(new Perl6VariableData[0]);
    }

    private static Perl6VariableData checkIfLexicalVariableCaptured(Perl6PsiScope parentToCreateAt, PsiElement[] statements, Perl6Variable usedVariable) {
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
        boolean isAvailableLexically = parentToCreateAt.resolveLexicalSymbol(Perl6SymbolKind.Variable, usedVariable.getVariableName()) != null;
        String type = usedVariable.getVariableName().startsWith("$") ? usedVariable.inferType() : "";
        return new Perl6VariableData(usedVariable.getVariableName(), type, isAvailableLexically, !isAvailableLexically);
    }

    private static List<Perl6Variable> collectVariablesInStatements(PsiElement[] elements) {
        return Arrays.stream(elements)
                     .flatMap(el -> PsiTreeUtil.findChildrenOfType(el, Perl6Variable.class).stream())
                     .collect(Collectors.toList());
    }

    @Nullable
    private static PsiElement getAnchor(PsiElement[] atoms) {
        return PsiTreeUtil.getParentOfType(PsiTreeUtil.findCommonParent(atoms), Perl6Statement.class, false);
    }

    private static PsiElement[] getPartsToExtract(PsiFile file, Editor editor) {
        if (editor.getSelectionModel().hasSelection())
            return getElementsFromSelection(file, editor);
        return PsiElement.EMPTY_ARRAY;
    }

    private static PsiElement[] getElementsFromSelection(PsiFile file, Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        PsiElement startLeaf = file.findElementAt(selectionModel.getSelectionStart());
        PsiElement endLeaf = file.findElementAt(selectionModel.getSelectionEnd() - 1);
        PsiElement start = PsiTreeUtil.getNonStrictParentOfType(Perl6PsiUtil.skipSpaces(startLeaf, true), Perl6RegexAtom.class);
        PsiElement end = PsiTreeUtil.getNonStrictParentOfType(Perl6PsiUtil.skipSpaces(endLeaf, false), Perl6RegexAtom.class);

        if (start == null || end == null) {
            return PsiElement.EMPTY_ARRAY;
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


    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {}
}
