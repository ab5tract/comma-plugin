package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Perl6SignatureImpl extends ASTWrapperPsiElement implements Perl6Signature {
    public Perl6SignatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary(String type) {
        Perl6Parameter[] params = getParameters();
        List<String> sums = new ArrayList<>();
        for (Perl6Parameter param : params)
            sums.add(param.summary());
        String paramsSummary = String.join(", ", ArrayUtil.toStringArray(sums));
        if (type == null)
            return String.format("(%s)", paramsSummary);
        else
            return String.format("(%s%s--> %s)", paramsSummary, paramsSummary.isEmpty() ? "" : " ", type);
    }

    @Override
    @NotNull
    public Perl6Parameter[] getParameters() {
        return findChildrenByClass(Perl6Parameter.class);
    }

    @Override
    public SignatureCompareResult acceptsArguments(PsiElement[] argsArray, boolean isCompleteCall) {
        List<PsiElement> arguments = Arrays.asList(argsArray);
        List<Perl6Parameter> parameters = Arrays.asList(getParameters());

        if (parameters.size() == 0 && arguments.size() == 0)
            return new SignatureCompareResult(true);

        // Resulting data
        SignatureCompareResult result = new SignatureCompareResult(true);

        // We split all available arguments into positional and named ones
        List<PsiElement> positionalArgs = new ArrayList<>();
        Map<String, PsiElement> namedArgs = new HashMap<>();

        categorizeAttributes(arguments, positionalArgs, namedArgs);

        // We need to keep index of currently available positional
        int posArgIndex = 0;
        // And named attributes we encountered already
        Set<String> seen = new HashSet<>();

        // For every parameters from left to right
        for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
            Perl6Parameter parameter = parameters.get(parameterIndex);

            if (parameter.isPositional()) {
                // If it is a positional slurpy, eat rest of positionals and move on to see if we have a named slurpy ahead
                if (parameter.isSlurpy()) {
                    posArgIndex = eatPositionalSlurpy(arguments, result, positionalArgs, posArgIndex, parameterIndex);
                    continue;
                }

                // If we still have positional args...
                if (positionalArgs.size() > posArgIndex) {
                    PsiElement positionalArg = positionalArgs.get(posArgIndex++);
                    // TODO check positional argument for constraints
                    // Set index and move pointer on next
                    result.setParameterIndexOfArg(arguments.indexOf(positionalArg), parameterIndex);
                    result.incrementNextParameter();
                }
                else {
                    if (!parameter.isOptional() && isCompleteCall) {
                        failMatch(result, new Pair<>(posArgIndex, MatchFailureReason.NOT_ENOUGH_ARGS));
                        break;
                    }
                }
            }
            else if (parameter.isNamed()) {
                // If it is a named slurpy, eat rest of named ones and move on to see if we have a positional slurpy ahead
                if (parameter.isSlurpy()) {
                    eatNamedSlurpy(arguments, result, namedArgs, seen, parameterIndex);
                    continue;
                }

                String namedParameterName = parameter.getVariableName().substring(1); // cut off sigil
                PsiElement namedArgumentForParameter = namedArgs.get(namedParameterName);
                // If we found an argument for named parameter, process it, otherwise break
                if (namedArgumentForParameter != null) {
                    // TODO Check named argument for constraints
                    seen.add(namedParameterName);
                    result.setParameterIndexOfArg(arguments.indexOf(namedArgumentForParameter), parameterIndex);
                    result.incrementNextParameter();
                }
                else {
                    if (!parameter.isOptional() && isCompleteCall) {
                        failMatch(result, new Pair<>(posArgIndex, MatchFailureReason.MISSING_REQUIRED_NAMED));
                        break;
                    }
                }
            }
        }

        // If we iterated over all parameters, but there are still arguments left,
        // disallow matching and mark them as errors
        if (result.isAccepted()) {
            // Check surplus named arguments
            seen.forEach(key -> namedArgs.remove(key));
            if (!namedArgs.isEmpty())
                failMatch(result, namedArgs.keySet().stream().map(n -> new Pair<>(arguments.indexOf(namedArgs.get(n)), MatchFailureReason.SURPLUS_NAMED)).toArray(Pair[]::new));

            // Check surplus positional arguments
            List<PsiElement> positionalLeftovers = positionalArgs.subList(posArgIndex, positionalArgs.size());
            if (positionalLeftovers.size() != 0)
                failMatch(result, positionalLeftovers.stream().map(n -> new Pair<>(arguments.indexOf(n), MatchFailureReason.TOO_MANY_ARGS)).toArray(Pair[]::new));
        }

        return result;
    }

    private static int eatPositionalSlurpy(List<PsiElement> arguments,
                                           SignatureCompareResult result,
                                           List<PsiElement> positionalArgs, int posArgIndex, int parameterIndex) {
        for (PsiElement rest : positionalArgs.subList(posArgIndex, positionalArgs.size()))
            result.setParameterIndexOfArg(arguments.indexOf(rest), parameterIndex);
        result.setNextParameter(parameterIndex);
        return positionalArgs.size();
    }

    private static void eatNamedSlurpy(List<PsiElement> arguments,
                                       SignatureCompareResult result,
                                       Map<String, PsiElement> namedArgs,
                                       Set<String> seen, int parameterIndex) {
        for (String rest : namedArgs.keySet())
            if (!seen.contains(rest))
                result.setParameterIndexOfArg(arguments.indexOf(namedArgs.get(rest)), parameterIndex);
        seen.addAll(namedArgs.keySet());
        result.setNextParameter(parameterIndex);
    }

    private static void failMatch(SignatureCompareResult result, Pair<Integer, MatchFailureReason>... failures) {
        result.setAccepted(false);
        for (Pair<Integer, MatchFailureReason> failure : failures)
            result.setFailureForArg(failure.getFirst(), failure.getSecond());
    }

    private static void categorizeAttributes(List<PsiElement> arguments, List<PsiElement> positionalArgs, Map<String, PsiElement> namedArgs) {
        for (PsiElement arg : arguments)
            if (arg instanceof Perl6ColonPair)
                namedArgs.put(((Perl6ColonPair)arg).getKey(), arg);
            else if (arg instanceof Perl6FatArrow)
                namedArgs.put(((Perl6FatArrow)arg).getKey(), arg);
            else
                positionalArgs.add(arg);
    }
}
