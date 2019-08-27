package edument.perl6idea.signatures;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.Perl6SubCall;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Perl6SignatureComparatorTest extends LightCodeInsightFixtureTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    private void doTest(String sig, String args, Consumer<Perl6Signature.SignatureCompareResult> asserts) {
        doTest(sig, args, true, asserts);
    }

    private void doTest(String sig, String args, boolean isCompleteCall, Consumer<Perl6Signature.SignatureCompareResult> asserts) {
        Perl6Signature signature = PsiTreeUtil.findChildOfType(Perl6ElementFactory.createStatementFromText(getProject(), String.format("sub (%s) {}", sig)), Perl6Signature.class);
        assertNotNull(signature);
        Perl6SubCall call = PsiTreeUtil.findChildOfType(Perl6ElementFactory.createStatementFromText(getProject(), String.format("a(%s);", args)), Perl6SubCall.class);
        Perl6Signature.SignatureCompareResult result = signature.acceptsArguments(call.getCallArguments(), isCompleteCall);
        asserts.accept(result);
    }

    private static void assertArgument(Perl6Signature.SignatureCompareResult res,
                                       int argumentIndex, int parameterIndex,
                                       Perl6Signature.MatchFailureReason reason) {
        assertEquals(parameterIndex, res.getParameterIndexOfArg(argumentIndex));
        assertEquals(reason, res.getArgumentFailureReason(argumentIndex));
    }

    public void testSingleArgSignature() {
        doTest("$a", "42", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(1, res.getNextParameterIndex());
            assertArgument(res,0,0, null);
        });
    }

    public void testNamedArgSignature() {
        doTest(":$abc", ":abc", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(1, res.getNextParameterIndex());
            assertArgument(res, 0, 0, null);
        });
    }

    public void testNamedArgSignatureNoMatch() {
        doTest(":$abc", "", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(0, res.getNextParameterIndex());
            assertArgument(res, 0, -1, null);
        });
    }

    public void testNamedArgSignatureSurplusArg() {
        doTest(":$abc", ":nono", (res) -> {
            assertFalse(res.isAccepted());
            assertEquals(0, res.getNextParameterIndex());
            assertArgument(res, 0, -1, Perl6Signature.MatchFailureReason.SURPLUS_NAMED);
        });
    }

    public void testRequiredNamed() {
        doTest(":$abc!", "", (res) -> {
            assertFalse(res.isAccepted());
            assertEquals(0, res.getNextParameterIndex());
            assertArgument(res, 0, -1, Perl6Signature.MatchFailureReason.MISSING_REQUIRED_NAMED);
        });
        doTest(":$abc!", ":nonono", (res) -> {
            assertFalse(res.isAccepted());
            assertEquals(0, res.getNextParameterIndex());
            assertArgument(res, 0, -1, Perl6Signature.MatchFailureReason.MISSING_REQUIRED_NAMED);
        });
    }

    public void testSurplus() {
        doTest("$a, $b?, *@a", "1, 2, 3, 4", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(2, res.getNextParameterIndex());
            assertArgument(res, 0, 0, null);
            assertArgument(res, 1, 1, null);
            for (int i = 2; i < 4; i++)
                assertArgument(res, i, 2, null);
        });
    }

    public void testNamedSurplus() {
        doTest(":$a!, :$b, *%rest", ":a, :aa, :bb, :cc", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(2, res.getNextParameterIndex());
            assertArgument(res, 0, 0, null);
            for (int i = 1; i < 4; i++)
                assertArgument(res, i, 2, null);
        });
    }

    public void testComplexSignature() {
        doTest("$a, @b, $d?, :$one, :$two!, *@rest, *%rest", "42, (1,2,3), :two, 'rest', 'rest', :rest, :rest2", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(6, res.getNextParameterIndex());
            assertArgument(res, 0, 0, null);
            assertArgument(res, 1, 1, null);
            assertArgument(res, 2, 4, null);
            assertArgument(res, 3, 2, null);
            assertArgument(res, 4, 5, null);
            assertArgument(res, 5, 6, null);
            assertArgument(res, 6, 6, null);
        });
        doTest("$a, @b, :$one, :$two!, *@rest, *%rest", "42, (1,2,3), :two, :one, :rest1, :rest2, :!rest3, 'rest', 'rest'", (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(5, res.getNextParameterIndex());
            assertArgument(res, 0, 0, null);
            assertArgument(res, 1, 1, null);
            assertArgument(res, 2, 3, null);
            assertArgument(res, 3,2, null);
            assertArgument(res, 4, 5, null);
            assertArgument(res, 5, 5, null);
            assertArgument(res, 6, 5, null);
            assertArgument(res, 7, 4, null);
            assertArgument(res, 8, 4, null);
        });
    }

    public void testIncompleteCalls() {
        doTest("$a, *@foo", "", false, (res) -> {
            assertTrue(res.isAccepted());
            assertEquals(0, res.getNextParameterIndex());
        });
        doTest("$a?", "", true, (res) -> assertTrue(res.isAccepted()));
        doTest("$a", "", false, (res) -> assertTrue(res.isAccepted()));
        doTest("$a, $b", "", false, (res) -> assertTrue(res.isAccepted()));
        doTest("$a, $b", "42", false, (res) -> assertTrue(res.isAccepted()));
        doTest("$a, :$foo", "42", false, (res) -> assertTrue(res.isAccepted()));
        doTest(":$a", ":a", false, (res) -> assertTrue(res.isAccepted()));
        doTest(":$a", ":b", false, (res) -> assertFalse(res.isAccepted()));
        doTest("@a", "(1,2,3)", false, (res) -> assertTrue(res.isAccepted()));
        doTest("$a, *%b", "1, 2, :b", false, (res) -> assertFalse(res.isAccepted()));
        doTest("$a, :$abc!", "1", false, (res) -> assertTrue(res.isAccepted()));
    }

    // TODO literal comparison
    // TODO constraints comparison
    // TODO type comparison
}
