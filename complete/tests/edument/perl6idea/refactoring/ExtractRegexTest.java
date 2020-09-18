package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.psi.Perl6PsiScope;

public class ExtractRegexTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "complete/testData/regex-extract";
    }

    public void testBasic() {
        doTest("rule-case", Perl6RegexPartType.RULE, false);
        doTest("basic", Perl6RegexPartType.TOKEN, false);
        doTest("basic-rule", Perl6RegexPartType.RULE, false);
        doTest("basic-regex-capture", Perl6RegexPartType.REGEX, true);
    }

    public void testGrammar() {
        doTest("grammar", Perl6RegexPartType.TOKEN, false);
        doTest("grammar-rule", Perl6RegexPartType.RULE, true);
    }

    private void doTest(String filename, Perl6RegexPartType type, boolean isCapture) {
        myFixture.configureByFile(filename + "-before.p6");
        Perl6ExtractRegexPartHandlerMock handler = new Perl6ExtractRegexPartHandlerMock(type, isCapture);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResultByFile(filename + ".p6", true);
    }

    private static class Perl6ExtractRegexPartHandlerMock extends Perl6ExtractRegexPartHandler {
        private final Perl6RegexPartType myType;
        private final boolean myIsCapture;

        public Perl6ExtractRegexPartHandlerMock(Perl6RegexPartType type, boolean isCapture) {
            myType = type;
            myIsCapture = isCapture;
        }

        @Override
        protected NewRegexPartData getNewRegexPartData(Project project, Perl6PsiScope parentToCreateAt,
                                                       PsiElement[] atoms, boolean isLexical, Perl6RegexPartType parentType) {
            Perl6VariableData[] params = getCapturedVariables(parentToCreateAt, atoms);
            String base = "";
            if (params.length != 0)
                base += NewCodeBlockData.formSignature(params, false);
            return new NewRegexPartData(myType, "foo",
                                        base.isEmpty() ? "" : "(" + base + ")",
                                        myIsCapture, isLexical, myType);
        }
    }
}
