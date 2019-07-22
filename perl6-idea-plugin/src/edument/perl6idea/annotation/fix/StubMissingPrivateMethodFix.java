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
import edument.perl6idea.refactoring.NewCodeBlockData;
import edument.perl6idea.refactoring.Perl6CodeBlockType;
import edument.perl6idea.refactoring.Perl6VariableData;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static edument.perl6idea.parsing.Perl6ElementTypes.UNTERMINATED_STATEMENT;
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
        Perl6StatementList list = PsiTreeUtil.findChildOfType(packageDecl, Perl6StatementList.class);
        if (packageDecl == null || list == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method without enclosing class",
                                                "Stubbing private method", null);
            return;
        }

        PsiElement anchor = null;
        PsiElement temp = myCall;
        while (temp != null && !(temp instanceof Perl6PackageDecl)) {
            temp = temp.getParent();
            if (temp instanceof Perl6RoutineDecl) {
                anchor = temp;
            }
        }
        anchor = anchor != null ? PsiTreeUtil.getParentOfType(anchor, Perl6Statement.class, false) : Perl6PsiUtil.skipSpaces(list.getLastChild(), false);
        if (anchor == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method: can't find suitable anchor",
                                                "Stubbing private method", null);
            return;
        }

        if (anchor.getLastChild().getNode().getElementType() == UNTERMINATED_STATEMENT) {
            Perl6PsiUtil.terminateStatement(anchor);
        }

        List<String> parameters = new ArrayList<>();
        populateParameters(parameters, myCall.getCallArguments());
        parameters = moveNamedsAfterPositionals(parameters);

        NewCodeBlockData data =
                new NewCodeBlockData(
                        Perl6CodeBlockType.PRIVATEMETHOD, "", myName, "",
                        parameters.stream().map(n -> new Perl6VariableData(n, "", false, true)).toArray(Perl6VariableData[]::new));
        Perl6Statement newMethod = Perl6ElementFactory.createNamedCodeBlock(project, data, new ArrayList<>());

        PsiElement newlyAddedMethod = list.addAfter(newMethod, anchor);

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformatNewlyAddedElement(list.getNode(), newlyAddedMethod.getNode());
        allowRename(newlyAddedMethod, editor);
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
                parameters.add(preprocessName(((Perl6Variable)arg).getVariableName()));
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

    private static String preprocessName(String name) {
        return Perl6Variable.getTwigil(name) == '!' ?
               Perl6Variable.getSigil(name) + name.substring(2) :
               name;
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

    private static List<String> moveNamedsAfterPositionals(List<String> parameters) {
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
