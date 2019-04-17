package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IntentionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/intention";
    }

    public void testZeroToN() {
        executeIntention("Use simpler range syntax");
    }

    public void testZeroToExclusiveN() {
        executeIntention("Use simpler range syntax");
    }

    public void testZeroToVar() {
        executeIntention("Use simpler range syntax");
    }

    public void testZeroToExclusiveVar() {
        executeIntention("Use simpler range syntax");
    }

    public void testZeroToExclusiveVarInParentheses() {
        executeIntention("Use simpler range syntax");
    }

    public void testEVALOfVariable() {
        executeIntention("Add MONKEY");
    }

    public void testEVALOfInterpolation() {
        executeIntention("Add MONKEY");
    }

    public void testRoleMethodsStubbing() {
        executeIntention("Stub");
    }

    public void testRecursiveRoleMethodsStubbing() {
        executeIntention("Stub");
    }

    public void testMyVariableExport() {
        executeIntention("Change scope");
    }

    public void testRoleDoesClassConvertedToInheritance() {
        executeIntention("Replace \"does\"");
    }

    public void testUnitPrependingForNamedClass() {
        executeIntention("Add missing");
    }

    public void testUnitRemovalForDefinedGrammar() {
        executeIntention("Remove 'unit'");
    }

    public void testPrivateMethodStubbing() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingWithoutEnclosingRoutine() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingInNestedRoutines() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingSignatureGeneration() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingSignatureGenerationForSingleArg() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingSignatureGenerationColonpair() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingSignatureGenerationForSingleArgColonpair() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingSignatureGenerationForNamedParameters() {
        executeIntention("Create");
    }

    public void testOrderOfNamedVariablesInCallIsFixed() {
        executeIntention("Create");
    }

    public void testConflictResolutionAfterMoveSolved() {
        executeIntention("Create");
    }

    public void testPrivateMethodStubbingReformatsOnlyAddedBlock() {
        executeIntention("Create");
    }

    public void testColonPairSimplification() {
        executeIntention("Convert");
    }

    public void testFatArrowSimplification() {
        executeIntention("Convert");
    }

    public void testConstConstantKeywordFix() {
        executeIntention("Use");
    }

    public void testConstConstantVarFix() {
        executeIntention("Use");
    }

    public void testConstSubFix() {
        checkIntentionAbsence("Use");
    }

    private void checkIntentionAbsence(String hint) {
        assertNull(prepareIntention(hint));
    }

    private void executeIntention(String hint) {
        IntentionAction intention = prepareIntention(hint);
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile(getTestName(false) + ".p6", true);
    }

    @Nullable
    private IntentionAction prepareIntention(String hint) {
        myFixture.configureByFile(getTestName(false) + "Before.p6");
        List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions(hint);
        assertTrue(availableIntentions.size() == 1 || availableIntentions.size() == 0);
        if (availableIntentions.size() == 0)
            return null;
        else
            return myFixture.findSingleIntention(hint);
    }
}
