package org.raku.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.raku.psi.*;

import java.util.*;

import static org.raku.parsing.RakuTokenTypes.COLON_PAIR;
import static org.raku.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

/** Collection of things related to processing of signatures
 * and argument captures */
public class RakuSignatureUtils {

    public static List<String> populateParameters(PsiElement[] children) {
        List<String> parameters = new ArrayList<>();
        for (PsiElement arg : children) {
            if (arg instanceof PsiWhiteSpace ||
                arg.getNode().getElementType() == UNV_WHITE_SPACE ||
                arg instanceof RakuInfix) continue;
            if (arg instanceof RakuVariable)
                parameters.add(preprocessName(((RakuVariable)arg).getVariableName()));
            else if (arg instanceof RakuPostfixApplication && arg.getLastChild() instanceof RakuMethodCall)
                parameters.add("$" + ((RakuMethodCall)arg.getLastChild()).getCallName().substring(1));
            else if (arg instanceof RakuSubCall && arg.getFirstChild() instanceof RakuSubCallName)
                parameters.add("$" + ((RakuSubCallName)arg.getFirstChild()).getCallName());
            else if (arg instanceof RakuFatArrow)
                parameters.add(":$" + arg.getFirstChild().getText());
            else if (arg instanceof RakuArrayComposer || arg instanceof RakuParenthesizedExpr)
                parameters.add("@p");
            else if (arg instanceof RakuColonPair)
                parameters.add(processColonpair(arg));
            else
                parameters.add("$p");
        }
        return moveNamedsAfterPositionals(resolveConflicts(parameters));
    }

    private static String preprocessName(String name) {
        return RakuVariable.getTwigil(name) == '!' ?
               RakuVariable.getSigil(name) + name.substring(2) :
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
            } else if (child instanceof RakuParenthesizedExpr) {
                PsiElement name = child.getPrevSibling();
                if (name != null)
                    return ":$" + name.getText();
            }
        }
        return "$p";
    }

    private static List<String> resolveConflicts(List<String> parameters) {
        Set<String> set = new HashSet<>(parameters);
        // If there are no duplicates, do nothing
        if (set.size() == parameters.size()) return parameters;
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
        return parameters;
    }

    public static List<String> moveNamedsAfterPositionals(List<String> parameters) {
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
}
