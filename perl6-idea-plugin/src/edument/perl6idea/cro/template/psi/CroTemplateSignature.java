package edument.perl6idea.cro.template.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import edument.perl6idea.psi.Perl6Signature;

import java.util.*;

import static edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes.NAMED_PARAMETER_SYNTAX;

public interface CroTemplateSignature extends PsiElement {
    void offerAllParametersTo(CroTemplateSymbolCollector collector);

    default Perl6Signature.SignatureCompareResult acceptsArguments(PsiElement[] argsArray, boolean isComplete) {
        List<PsiElement> arguments = Arrays.asList(argsArray);
        List<CroTemplateParameter> parameters = PsiTreeUtil.getChildrenOfTypeAsList(this, CroTemplateParameter.class);

        if (parameters.size() == 0 && arguments.size() == 0)
            return new Perl6Signature.SignatureCompareResult(true);

        // Resulting data
        Perl6Signature.SignatureCompareResult result = new Perl6Signature.SignatureCompareResult(true);

        // We split all available arguments into positional and named ones
        List<PsiElement> positionalArgs = new ArrayList<>();
        Map<String, PsiElement> namedArgs = new HashMap<>();

        for (int i = 0, size = arguments.size(); i < size; i++) {
            PsiElement arg = arguments.get(i);
            if (arg instanceof CroTemplateNamedArgument) {
                if (i + 1 >= size)
                    break;
                PsiElement named = arguments.get(i + 1);
                namedArgs.put(named.getText(), named);
                i++;
            } else {
                positionalArgs.add(arg);
            }
        }

        // We need to keep index of currently available positional
        int posArgIndex = 0;
        // And named attributes we encountered already
        Set<String> seen = new HashSet<>();

        // For every parameters from left to right
        for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
            CroTemplateParameter parameter = parameters.get(parameterIndex);

            if (parameter.getFirstChild().getNode().getElementType() != NAMED_PARAMETER_SYNTAX) {
                // If we still have positional args...
                if (positionalArgs.size() > posArgIndex) {
                    PsiElement positionalArg = positionalArgs.get(posArgIndex++);
                    result.setParameterIndexOfArg(arguments.indexOf(positionalArg), parameterIndex);
                    result.incrementNextParameter();
                }
            }
            else {
                String namedParameterName = parameter.getText().substring(1); // cut off sigil
                PsiElement namedArgumentForParameter = namedArgs.get(namedParameterName);
                // If we found an argument for named parameter, process it, otherwise break
                if (namedArgumentForParameter != null) {
                    seen.add(namedParameterName);
                    result.setParameterIndexOfArg(arguments.indexOf(namedArgumentForParameter), parameterIndex);
                    result.incrementNextParameter();
                }
            }
        }

        // If we iterated over all parameters, but there are still arguments left,
        // disallow matching and mark them as errors
        if (result.isAccepted()) {
            // Check surplus named arguments
            seen.forEach(key -> namedArgs.remove(key));
            if (!namedArgs.isEmpty())
                failMatch(result, namedArgs.keySet().stream().map(n -> new Pair<>(arguments.indexOf(namedArgs.get(n)), Perl6Signature.MatchFailureReason.SURPLUS_NAMED)).toArray(Pair[]::new));

            // Check surplus positional arguments
            List<PsiElement> positionalLeftovers = positionalArgs.subList(posArgIndex, positionalArgs.size());
            if (positionalLeftovers.size() != 0)
                failMatch(result, positionalLeftovers.stream().map(n -> new Pair<>(arguments.indexOf(n), Perl6Signature.MatchFailureReason.TOO_MANY_ARGS)).toArray(Pair[]::new));
        }

        return result;
    }

    default void failMatch(Perl6Signature.SignatureCompareResult result, Pair<Integer, Perl6Signature.MatchFailureReason>... failures) {
        result.setAccepted(false);
        for (Pair<Integer, Perl6Signature.MatchFailureReason> failure : failures)
            result.setFailureForArg(failure.getFirst(), failure.getSecond());
    }
}
