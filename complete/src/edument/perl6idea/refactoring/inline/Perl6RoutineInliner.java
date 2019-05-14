package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.containers.MultiMap;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.CompletePerl6ElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Perl6RoutineInliner implements InlineHandler.Inliner {
    @Nullable
    @Override
    public MultiMap<PsiElement, String> getConflicts(@NotNull PsiReference reference, @NotNull PsiElement referenced) {
        // No conflict detection for now
        return null;
    }

    @Override
    public void inlineUsage(@NotNull UsageInfo usage, @NotNull PsiElement referenced) {
        Perl6SubCall usageElement = PsiTreeUtil.getNonStrictParentOfType(usage.getElement(), Perl6SubCall.class);
        Project project = usageElement.getProject();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Perl6RoutineDecl decl = (Perl6RoutineDecl) referenced;

        PsiElement[] blockNodes = getBlockCopy(decl.getContent());

        unwrapLastReturnStatement(blockNodes);

        PsiElement replacement = createReplacement(usageElement.getProject(),
                usageElement, blockNodes, decl);

        usageElement.replace(replacement.copy());

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformat(replacement);
    }

    @NotNull
    private Map<String, Integer> enumeratePositionalParameters(Perl6RoutineDecl decl) {
        Map<String, Integer> positionalRoutineParams = new HashMap<>();

        Perl6Signature signature = decl.getSignatureNode();
        if (signature != null) {
            Perl6Parameter[] parameters = signature.getParameters();
            for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                Perl6Parameter parameter = parameters[i];
                if (parameter.isPositional())
                    positionalRoutineParams.put(parameter.getVariableName(), i);
            }
        }
        return positionalRoutineParams;
    }

    private void unwrapLastReturnStatement(PsiElement[] blockNodes) {
        PsiElement blockNode = blockNodes[blockNodes.length - 1];
        PsiElement returnStatement = blockNode.getFirstChild();
        if (returnStatement instanceof Perl6SubCall && ((Perl6SubCall) returnStatement).getSubCallName().equals("return")) {
            Perl6PsiElement name = (Perl6PsiElement) returnStatement.getFirstChild();
            PsiElement initializer = name.skipWhitespacesForward().copy();
            returnStatement.replace(initializer);
        }
    }

    private PsiElement[] getBlockCopy(PsiElement[] blockNodes) {
        for (int i = 0, blockNodesLength = blockNodes.length; i < blockNodesLength; i++) {
            blockNodes[i] = blockNodes[i].copy();
        }
        return blockNodes;
    }

    private PsiElement createReplacement(Project project, Perl6SubCall usageElement, PsiElement[] blockCopy, Perl6RoutineDecl decl) {
        PsiElement inserter;

        if (blockCopy.length == 1) {
            Perl6Statement singleStatement = (Perl6Statement) blockCopy[0];
            inserter = singleStatement.getFirstChild().copy();
        } else {
            inserter = CompletePerl6ElementFactory.createDoBlock(project, blockCopy);
        }
        updateVariables(usageElement, inserter, decl);
        return inserter;
    }

    private void updateVariables(Perl6SubCall usageElement, PsiElement inserter, Perl6RoutineDecl decl) {
        // We just use simple recursion here,
        // because variables can be pulled into inner subs and packages
        // as a closure, so we have to replace all of them except for
        // variables that are defined in this block and so are local
        Collection<Perl6Variable> variables = PsiTreeUtil.findChildrenOfType(inserter, Perl6Variable.class);

        Map<String, Integer> positionalRoutineParams = enumeratePositionalParameters(decl);

        List<PsiElement> callPositionalArgs = new ArrayList<>();
        Map<String, PsiElement> callNamedArgs = new HashMap<>();

        for (PsiElement arg : usageElement.getSubCallArguments()) {
            if (arg instanceof Perl6FatArrow)
                callNamedArgs.put(((Perl6FatArrow) arg).getKey(), ((Perl6FatArrow) arg).getValue());
            else
                callPositionalArgs.add(arg);
        }

        Perl6Signature signatureNode = decl.getSignatureNode();
        Perl6Parameter[] declarationParameters = signatureNode == null ? new Perl6Parameter[0] : signatureNode.getParameters();

        for (Perl6Variable variable : variables) {
            if (shouldKeepVariable(inserter, variable)) continue;

            // Named arguments don't have sigils, so we have to make a comparison
            // with sigilless variable name
            String variableName = variable.getVariableName();
            int sigilsCutIndex = Perl6Variable.getTwigil(variableName) == ' ' ? 1 : 2;

            if (callNamedArgs.containsKey(variableName.substring(sigilsCutIndex))) {
                variable.replace(callNamedArgs.get(variableName.substring(sigilsCutIndex)).copy());
            } else {
                // If it is not a named variable, let's try
                int callIndex = positionalRoutineParams.get(variableName);
                if (callIndex >= callPositionalArgs.size()) {
                    for (Perl6Parameter param : declarationParameters) {
                        if (Objects.equals(param.getVariableName(), variableName) && param.getInitializer() != null)
                            variable.replace(param.getInitializer());
                    }
                    positionalRoutineParams.get(variableName);
                } else {
                    PsiElement argument = callPositionalArgs.get(callIndex);
                    variable.replace(argument.copy());
                }
            }
        }
    }

    private boolean shouldKeepVariable(PsiElement block, Perl6Variable variable) {
        PsiReference ref = variable.getReference();
        assert ref != null;
        PsiElement declaration = ref.resolve();
        // If variable is not resolve-able for us, e.g. a dynamic,
        // or is declared but within the block itself, we keep it as it
        return declaration != null && PsiTreeUtil.isAncestor(block, declaration, true);
    }
}
