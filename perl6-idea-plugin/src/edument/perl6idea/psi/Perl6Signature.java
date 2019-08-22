package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface Perl6Signature extends Perl6PsiElement {
    String summary(String type);
    Perl6Parameter[] getParameters();
    SignatureCompareResult acceptsArguments(PsiElement[] arguments, boolean isCompleteCall);

    enum MatchFailureReason {
        NOT_ENOUGH_ARGS,
        TOO_MANY_ARGS,
        SURPLUS_NAMED,
        TYPE_MISMATCH,
        CONSTRAINT_MISMATCH,
        MISSING_REQUIRED_NAMED
    }

    class SignatureCompareResult {
        private boolean isAccepted;
        private Map<Integer, Integer> argToParam = new HashMap<>();
        private Map<Integer, MatchFailureReason> failures = new HashMap<>();
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
