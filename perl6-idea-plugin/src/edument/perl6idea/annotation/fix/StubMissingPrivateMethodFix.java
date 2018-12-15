package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.Perl6CodeBlockType;
import edument.perl6idea.refactoring.Perl6ExtractCodeBlockHandler;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class StubMissingPrivateMethodFix implements IntentionAction {
    private final String myName;
    private final Perl6MethodCall myCall;

    public StubMissingPrivateMethodFix(String name, Perl6MethodCall call) {
        myName = name;
        myCall = call;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return String.format("Create private method '%s'", myName);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Perl 6";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6PackageDecl packageDecl = PsiTreeUtil.getParentOfType(myCall, Perl6PackageDecl.class);
        Perl6StatementList list = packageDecl == null ?
                                  null :
                                  PsiTreeUtil.findChildOfType(packageDecl, Perl6StatementList.class);
        if (packageDecl == null || list == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method without enclosing class",
                                                "Stubbing private method", null);
            return;
        }

        Perl6RoutineDecl decl = null;
        PsiElement answer = myCall.getParent();
        do {
            if (answer == null || answer instanceof Perl6PackageDecl) break;
            if (answer instanceof Perl6RoutineDecl)
                decl = (Perl6RoutineDecl)answer;
            answer = answer.getParent();
        } while (true);

        PsiElement anchor = decl != null ? PsiTreeUtil.getParentOfType(decl, Perl6Statement.class) : list.getLastChild();

        List<String> parameters = new ArrayList<>();
        // There are more than 1 parameter, so infix
        PsiElement infixArgumentList = PsiTreeUtil.getChildOfType(myCall, Perl6InfixApplication.class);
        if (infixArgumentList != null)
            populateParameters(parameters, infixArgumentList.getChildren());
        // There might be only one parameter
        PsiElement possibleArg = PsiTreeUtil.getChildOfType(myCall, Perl6LongName.class);

        boolean openParenIsPassed = false;
        /* We iterate over children of call
        * Skip all elements until it's open parentheses
        * When it's done, set flag to true and continue
        * Between open paren and actual arg might be white space, skip those
        * It's useless to search if there are no sibling left, or it's closing paren,
        * or there are no name yet (possibleArg is null right from the start)
        */
        while (parameters.size() == 0) {
            if (possibleArg == null || possibleArg.getNode().getElementType() == PARENTHESES_CLOSE)
                break;
            IElementType elementType = possibleArg.getNode().getElementType();
            if (elementType == PARENTHESES_OPEN || elementType == INVOCANT_MARKER) {
                openParenIsPassed = true;
            }
            else if (openParenIsPassed &&
                     !(possibleArg instanceof PsiWhiteSpace) &&
                     elementType != UNV_WHITE_SPACE) {
                populateParameters(parameters, new PsiElement[]{possibleArg});
                break;
            }
            possibleArg = possibleArg.getNextSibling();
        }

        parameters = moveNamedsAfterPositionals(parameters);
        Perl6ExtractCodeBlockHandler.NewCodeBlockData data = new Perl6ExtractCodeBlockHandler.NewCodeBlockData();
        data.name = myName;
        data.type = Perl6CodeBlockType.PRIVATEMETHOD;
        PsiElement newMethod = Perl6ElementFactory.createNamedCodeBlock(project, data, new ArrayList<>());
        anchor = anchor == null ? null : anchor.getNextSibling();
        if (anchor == null) {
            list.getNode().addChild(new PsiWhiteSpaceImpl("\n"));
            list.getNode().addChild(newMethod.getNode());
        } else {
            addPossibleNewline(list, anchor);
            list.getNode().addChild(newMethod.getNode(), anchor.getNode());
            list.getNode().addChild(new PsiWhiteSpaceImpl("\n"), anchor.getNode());
        }
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformat(file);
        allowRename(newMethod, editor);
    }

    private static void allowRename(PsiElement newMethod, Editor editor) {
        Collection<Perl6ParameterVariable> children = PsiTreeUtil.findChildrenOfType(newMethod, Perl6ParameterVariable.class);
        TemplateBuilder builder = new TemplateBuilderImpl(newMethod);
        for (Perl6ParameterVariable var : children) {
            builder.replaceElement(var, var.getName());
        }
        builder.run(editor, true);
    }

    private static void populateParameters(List<String> parameters, PsiElement[] children) {
        for (PsiElement arg : children) {
            if (arg instanceof PsiWhiteSpace ||
                arg.getNode().getElementType() == UNV_WHITE_SPACE ||
                arg instanceof Perl6Infix) continue;
            if (arg instanceof Perl6Variable)
                parameters.add(((Perl6Variable)arg).getVariableName());
            else if (arg instanceof Perl6PostfixApplication && arg.getLastChild() instanceof Perl6MethodCall)
                parameters.add("$" + ((Perl6MethodCall)arg.getLastChild()).getCallName().substring(1));
            else if (arg instanceof Perl6SubCall && arg.getFirstChild() instanceof Perl6SubCallName)
                parameters.add("$" + ((Perl6SubCallName)arg.getFirstChild()).getCallName());
            else if (arg instanceof Perl6FatArrow)
                parameters.add(":$" + arg.getFirstChild().getText());
            else if (arg instanceof Perl6ArrayComposer || arg instanceof Perl6ParenthesizedExpr)
                parameters.add("@p");
            else if (arg instanceof Perl6ColonPair)
                parameters.add(processColonpair(arg));
            else
                parameters.add("$p");
        }
        resolveConflicts(parameters);
    }

    private static String processColonpair(PsiElement arg) {
        String colonpair = arg.getText();
        if (colonpair.startsWith(":$") ||
            colonpair.startsWith(":@") ||
            colonpair.startsWith(":%") ||
            colonpair.startsWith(":&")) {
            if (colonpair.length() >= 3 && colonpair.charAt(2) != '<') {
                return colonpair;
            } else {
                return ":$" + colonpair.substring(3, colonpair.length() - 1);
            }
        } else {
            PsiElement child = arg.getLastChild();
            if (child != null && child.getNode().getElementType() == COLON_PAIR) {
                return ":$" + child.getText();
            } else if (child instanceof Perl6ParenthesizedExpr) {
                PsiElement name = child.getPrevSibling();
                if (name != null)
                    return ":$" + name.getText();
            }
        }
        return "$p";
    }

    private static void resolveConflicts(List<String> parameters) {
        Set<String> set = new HashSet<>(parameters);
        // If there are no duplicates, do nothing
        if (set.size() == parameters.size()) return;
        // Else rename it
        Map<String, Integer> firstOccurrences = new HashMap<>();
        Map<String, Integer> counter = new HashMap<>();
        String param;
        for (int i = 0; i < parameters.size(); i++) {
            param = parameters.get(i);
            if (counter.containsKey(param)) {
                // We already saw this one more than twice
                int value = counter.get(param);
                int nextIndex = value + 1;
                parameters.set(i, param + nextIndex);
                counter.put(param, nextIndex);
            } else if (firstOccurrences.containsKey(param)) {
                // We seen it once, but not twice
                int firstOccurrenceIndex = firstOccurrences.get(param);
                parameters.set(firstOccurrenceIndex, param + 1);
                parameters.set(i, param + 2);
                counter.put(param, 2);
            } else {
                // We have not seen it yet
                firstOccurrences.put(param, i);
            }
        }
    }

    private List<String> moveNamedsAfterPositionals(List<String> parameters) {
        List<String> result = new ArrayList<>();
        List<String> namedParams = new ArrayList<>();
        for (String param : parameters) {
            if (param.startsWith(":")) {
                namedParams.add(param);
            } else {
                result.add(param);
            }
        }
        result.addAll(namedParams);
        return result;
    }

    private static void addPossibleNewline(Perl6StatementList list, PsiElement anchor) {
        list.getNode().addChild(new PsiWhiteSpaceImpl("\n"), anchor.getNode());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
