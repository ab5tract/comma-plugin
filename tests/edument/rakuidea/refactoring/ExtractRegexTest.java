package edument.rakuidea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.psi.RakuPsiScope;

public class ExtractRegexTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "complete/testData/regex-extract";
    }

    public void testBasic() {
        doTest("rule-case", RakuRegexPartType.RULE, false);
        doTest("basic", RakuRegexPartType.TOKEN, false);
        doTest("basic-rule", RakuRegexPartType.RULE, false);
        doTest("basic-regex-capture", RakuRegexPartType.REGEX, true);
    }

    public void testGrammar() {
        doTest("grammar", RakuRegexPartType.TOKEN, false);
        doTest("grammar-rule", RakuRegexPartType.RULE, true);
    }

    private void doTest(String filename, RakuRegexPartType type, boolean isCapture) {
        myFixture.configureByFile(filename + "-before.p6");
        RakuExtractRegexPartHandlerMock handler = new RakuExtractRegexPartHandlerMock(type, isCapture);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResultByFile(filename + ".p6", true);
    }

    private static class RakuExtractRegexPartHandlerMock extends RakuExtractRegexPartHandler {
        private final RakuRegexPartType myType;
        private final boolean myIsCapture;

        public RakuExtractRegexPartHandlerMock(RakuRegexPartType type, boolean isCapture) {
            myType = type;
            myIsCapture = isCapture;
        }

        @Override
        protected NewRegexPartData getNewRegexPartData(Project project, RakuPsiScope parentToCreateAt,
                                                       PsiElement[] atoms, boolean isLexical, RakuRegexPartType parentType) {
            RakuVariableData[] params = getCapturedVariables(parentToCreateAt, atoms);
            String base = "";
            if (params.length != 0)
                base += NewCodeBlockData.formSignature(params, false);
            return new NewRegexPartData(myType, "foo",
                                        base.isEmpty() ? "" : "(" + base + ")",
                                        myIsCapture, isLexical, myType);
        }
    }
}
