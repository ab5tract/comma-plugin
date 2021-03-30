package edument.perl6idea.psi;

import org.jetbrains.annotations.Nullable;

public interface Perl6IfStatement extends P6Extractable, P6Conditional, P6Control, P6Topicalizer {
    class IfPart {
        private final boolean isWith;
        private final Perl6PsiElement condition;
        private final Perl6PsiElement body;
        public IfPart(boolean isWith, Perl6PsiElement condition, Perl6PsiElement body) {
            this.isWith = isWith;
            this.condition = condition;
            this.body = body;
        }
        public boolean isWith() {
            return isWith;
        }
        @Nullable
        public Perl6PsiElement getCondition() {
            return condition;
        }
        @Nullable
        public Perl6PsiElement getBody() {
            return body;
        }
    }
    class IfStructure {
        private final IfPart[] conditionalParts;
        private final Perl6PsiElement elseBody;
        public IfStructure(IfPart[] parts, Perl6PsiElement body) {
            conditionalParts = parts;
            elseBody = body;
        }
        public IfPart[] getConditionalParts() {
            return conditionalParts;
        }
        @Nullable
        public Perl6PsiElement getElseBody() {
            return elseBody;
        }
    }

    String getLeadingStatementControl();
    IfStructure getStructure();
}
