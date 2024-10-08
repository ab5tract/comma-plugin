package org.raku.comma.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.type.RakuType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface RakuSignature extends RakuPsiElement {
    String summary(RakuType type);
    RakuParameter[] getParameters();

    default SignatureCompareResult acceptsArguments(PsiElement[] argsArray, boolean isCompleteCall, boolean isMethodCall) {
        List<PsiElement> arguments = Arrays.asList(argsArray);
        List<RakuParameter> parameters = Arrays.asList(getParameters());

        if (parameters.isEmpty() && arguments.isEmpty()) return new SignatureCompareResult(true);

        // Resulting data
        SignatureCompareResult result = new SignatureCompareResult(true);

        // We split all available arguments into positional and named ones
        List<PsiElement> positionalArgs = new ArrayList<>();
        Map<String, PsiElement> namedArgs = new HashMap<>();

        categorizeArguments(arguments, positionalArgs, namedArgs);

        // We need to keep index of currently available positional
        int posArgIndex = 0;
        // And named attributes we encountered already
        Set<String> seen = new HashSet<>();

        // For every parameters from left to right
        for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
            RakuParameter parameter = parameters.get(parameterIndex);

            // Signature (|) or (|c) accepts everything
            if (parameter.getText().startsWith("|"))
                return result;

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
                    }
                }
            }
            else if (parameter.isNamed()) {
                // If it is a named slurpy, eat rest of named ones and move on to see if we have a positional slurpy ahead
                if (parameter.isSlurpy()) {
                    eatNamedSlurpy(arguments, result, namedArgs, seen, parameterIndex);
                    continue;
                }

                String namedParameterName = prepareParamName(parameter.getVariableName());
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
                        MatchFailureReason failReason = MatchFailureReason.MISSING_REQUIRED_NAMED;
                        failReason.name = parameter.getVariableName();
                        failMatch(result, new Pair<>(posArgIndex, failReason));
                    }
                }
            }
        }

        // If we iterated over all parameters, but there are still arguments left,
        // disallow matching and mark them as errors
        // Check surplus named arguments, but methods allow them
        if (! isMethodCall) {
            seen.forEach(namedArgs::remove);
            if (! namedArgs.isEmpty()) {
                failMatch(result, namedArgs.keySet().stream().map(n -> new Pair<>(arguments.indexOf(namedArgs.get(n)), MatchFailureReason.SURPLUS_NAMED)).toArray(Pair[]::new));
            }
        }

        // Check surplus positional arguments
        List<PsiElement> positionalLeftovers = positionalArgs.subList(posArgIndex, positionalArgs.size());
        if (! positionalLeftovers.isEmpty()) {
            failMatch(result, positionalLeftovers.stream().map(n -> new Pair<>(arguments.indexOf(n), MatchFailureReason.TOO_MANY_ARGS)).toArray(Pair[]::new));
        }
        return result;
    }

    default String prepareParamName(String variableName) {
        int start = 0, end = 0;
        if (variableName.length() <= 1) return variableName;
        for (int i = 0; i < variableName.length(); i++) {
            if (variableName.charAt(i) == ':' ||
                variableName.charAt(i) == '$' ||
                variableName.charAt(i) == '@' ||
                variableName.charAt(i) == '%' ||
                variableName.charAt(i) == '&')
            {
                start = i + 1;
            }
            else if (variableName.charAt(i) == '?' || variableName.charAt(i) == '!') {
                end = i;
                break;
            }
            else {
                end = i;
            }
        }
        return variableName.substring(start, end + 1);
    }

    default int eatPositionalSlurpy(List<PsiElement> arguments,
                                           SignatureCompareResult result,
                                           List<PsiElement> positionalArgs, int posArgIndex, int parameterIndex)
    {
        for (PsiElement rest : positionalArgs.subList(posArgIndex, positionalArgs.size())) {
            result.setParameterIndexOfArg(arguments.indexOf(rest), parameterIndex);
        }
        result.setNextParameter(parameterIndex);
        return positionalArgs.size();
    }

    default void eatNamedSlurpy(List<PsiElement> arguments,
                                       SignatureCompareResult result,
                                       Map<String, PsiElement> namedArgs,
                                       Set<String> seen, int parameterIndex) {
        for (String rest : namedArgs.keySet()) {
            if (!seen.contains(rest)) {
                result.setParameterIndexOfArg(arguments.indexOf(namedArgs.get(rest)), parameterIndex);
            }
        }
        seen.addAll(namedArgs.keySet());
        result.setNextParameter(parameterIndex);
    }

    default void failMatch(SignatureCompareResult result, Pair<Integer, MatchFailureReason>... failures) {
        result.setAccepted(false);
        for (Pair<Integer, MatchFailureReason> failure : failures) {
            result.setFailureForArg(failure.getFirst(), failure.getSecond());
        }
    }

    default void categorizeArguments(List<PsiElement> arguments, List<PsiElement> positionalArgs, Map<String, PsiElement> namedArgs) {
        for (PsiElement arg : arguments)
            if (arg instanceof RakuColonPair)
                namedArgs.put(((RakuColonPair)arg).getKey(), arg);
            else if (arg instanceof RakuFatArrow)
                namedArgs.put(((RakuFatArrow)arg).getKey(), arg);
            else
                positionalArgs.add(arg);
    }

    enum MatchFailureReason {
        NOT_ENOUGH_ARGS,
        TOO_MANY_ARGS,
        SURPLUS_NAMED,
        TYPE_MISMATCH,
        CONSTRAINT_MISMATCH,
        MISSING_REQUIRED_NAMED;

        // Additional data
        @Nullable
        public String name;
    }

    class SignatureCompareResult {
        private boolean isAccepted;
        private final Map<Integer, Integer> argToParam = new HashMap<>();
        private final Map<Integer, MatchFailureReason> failures = new HashMap<>();
        private int nextParameterIndex;

        public SignatureCompareResult(boolean isAccepted) {
            this.isAccepted = isAccepted;
        }

        public void setAccepted(boolean accepted) {
            isAccepted = accepted;
        }

        public boolean isAccepted() {
            return isAccepted;
        }

        public void setParameterIndexOfArg(int argIndex, int paramIndex) {
            argToParam.put(argIndex, paramIndex);
        }

        public int getParameterIndexOfArg(int argumentIndex) {
            return argToParam.getOrDefault(argumentIndex, -1);
        }

        public void setFailureForArg(int argIndex, MatchFailureReason reason) {
            failures.put(argIndex, reason);
        }

        @Nullable
        public MatchFailureReason getArgumentFailureReason(int argumentIndex) {
            return failures.getOrDefault(argumentIndex, null);
        }

        public void incrementNextParameter() {
            nextParameterIndex++;
        }

        public void setNextParameter(int index) {
            nextParameterIndex = index;
        }

        public int getNextParameterIndex() {
            return nextParameterIndex;
        }
    }
}
