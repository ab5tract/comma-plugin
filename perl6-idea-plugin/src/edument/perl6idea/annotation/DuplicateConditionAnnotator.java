package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6ConditionalBranch;
import edument.perl6idea.psi.Perl6IfStatement;
import edument.perl6idea.psi.Perl6InfixApplication;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.utils.Perl6OperatorUtils;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DuplicateConditionAnnotator implements Annotator {
    private static final Set<String> COMMUTERS = ContainerUtil.set(
            "==", "!=", "eq", "ne", "===", "cmp", "eqv", "=~=", "=:=", "(==)",
            "+", "*", "&", "|");
    private static final Map<String, String> UNINORM = new HashMap<>() {{
        for (Pair<Character, String> op : ContainerUtil.zip(Perl6OperatorUtils.unicodeOperators, Perl6OperatorUtils.asciiOperators))
            put(op.first.toString(), op.second);
    }};
    private static final Map<String, String> REVERSES = new HashMap<>() {{
        put(">", "<");
        put(">=", "<=");
        put("gt", "lt");
        put("ge", "le");
        put("(<)", "(>)");
        put("(<=)", "(>=)");
        put("(<+)", "(>+)");
    }};

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Make sure it's a if/with/elsif/orwith structure at least two conditions.
        if (!(element instanceof Perl6IfStatement))
            return;
        Perl6ConditionalBranch[] branches = ((Perl6IfStatement)element).getBranches();
        if (branches.length < 2)
            return;

        // Compute a normalization of all the conditions group them on that.
        Map<String, List<PsiElement>> duplicated = new HashMap<>();
        for (Perl6ConditionalBranch branch : branches) {
            PsiElement condition = branch.condition;
            if (condition == null)
                continue;
            duplicated.computeIfAbsent(normalize(branch.term, condition), k -> new ArrayList<>()).add(condition);
        }

        // If there are any identical conditions, report them.
        if (duplicated.size() == branches.length) // Fail fast, all distinct
            return;
        for (Map.Entry<String, List<PsiElement>> maybeIdentical : duplicated.entrySet()) {
            List<PsiElement> identical = maybeIdentical.getValue();
            if (identical.size() > 1)
                for (int i = 1; i < identical.size(); i++)
                    holder.newAnnotation(HighlightSeverity.WARNING, "An identical condition appears in a previous branch")
                        .range(identical.get(i))
                        .create();
        }
    }

    private static String normalize(PsiElement term, PsiElement condition) {
        return normalize(term.getText()) + "\0" + normalize(condition);
    }

    private static String normalize(String prefix) {
        return switch (prefix) {
            case "if", "elsif" -> "if";
            case "with", "orwith" -> "with";
            case "without" -> "without";
            case "unless" -> "unless";
            default -> "";
        };
    }

    private static String normalize(PsiElement condition) {
        // We normalize some binary infix operators.
        if (condition instanceof Perl6InfixApplication) {
            String op = ((Perl6InfixApplication)condition).getOperator();
            PsiElement[] operands = ((Perl6InfixApplication)condition).getOperands();
            if (op != null && operands.length == 2 &&
                    operands[0] instanceof Perl6PsiElement && operands[1] instanceof Perl6PsiElement) {
                // Calculate the normalization fo the operands.
                String lhsNorm = normalize(operands[0]);
                String rhsNorm = normalize(operands[1]);

                // Normalize any Unicode/ASCII variants.
                op = UNINORM.getOrDefault(op, op);

                // If it commutes, sort the normalization of the arguments.
                if (COMMUTERS.contains(op)) {
                    return lhsNorm.compareTo(rhsNorm) < 0
                           ? lhsNorm + "\0" + op + "\0" + rhsNorm
                           : rhsNorm + "\0" + op + "\0" + lhsNorm;
                }

                // Some things are identical under swapping LHS and RHs.
                else if (REVERSES.containsKey(op)) {
                    return rhsNorm + "\0" + REVERSES.get(op) + "\0" + lhsNorm;
                }

                // Otherwise, just a normal op.
                else {
                    return rhsNorm + "\0" + op + "\0" + lhsNorm;
                }
            }
        }

        // Fallback semantics is to go through the children minus whitespace and
        // nul-separate them.
        StringJoiner parts = new StringJoiner("\0");
        PsiElement current = condition.getFirstChild();
        while ((current = Perl6PsiUtil.skipSpaces(current, true, true)) != null) {
            parts.add(current instanceof Perl6PsiElement
                      ? normalize(current)
                      : current.getText());
            current = current.getNextSibling();
        }
        return parts.toString();
    }
}
