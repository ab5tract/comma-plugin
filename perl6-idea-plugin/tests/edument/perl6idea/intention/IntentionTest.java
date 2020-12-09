package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IntentionTest extends CommaFixtureTestCase {
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

    public void testRangeSimplificationRegress1() {
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

    public void testPrivateMethodStubbingFromUnfinishedVariable() {
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

    public void testPrivateMethodHasPrivateVariableArgumentUpdated() {
        executeIntention("Create");
    }

    public void testColonPairSimplification() {
        executeIntention("Convert");
    }

    public void testFatArrowSimplification() {
        executeIntention("Convert");
    }

    public void testWhileOneSimplification() {
        executeIntention("Use");
    }

    public void testWhileTrueSimplification() {
        executeIntention("Use");
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

    public void testWithConstructionFix() {
        executeIntention("Use");
    }

    public void testPackageTypeChangeIntention() {
        executeIntention("Change");
    }

    public void testPackageTypeChangeIntoMonitorIntention() throws InterruptedException {
        ensureModuleIsLoaded("OO::Monitors");
        executeIntention("Change");
    }

    public void testPackageTypeChangeIntoMonitorPresent() throws InterruptedException {
        ensureModuleIsLoaded("OO::Monitors");
        executeIntention("Change");
    }

    public void testPackageTypeChangeOnTypeNameIntention() {
        executeIntention("Change");
    }

    public void testPackageTypeChangesInheritanceIntention() {
        executeIntention("Change");
    }

    public void testPackageTypeChangesInheritanceComposition() {
        executeIntention("Change");
    }

    public void testAttributeRequiredOnlyHas() {
        checkIntentionAbsence("Make required");
    }

    public void testAttributeRequiredNoDoubling() {
        checkIntentionAbsence("Make required");
    }

    public void testAttributeRequiredNoTraits() {
        executeIntention("Make required");
    }

    public void testAttributeRequiredTraits() {
        executeIntention("Make required");
    }

    public void testAttributeRequiredOnName() {
        executeIntention("Make required");
    }

    public void testAttributeRequiredWithDefault() {
        checkIntentionAbsence("Make required");
    }

    public void testWithoutConstructionFix() {
        executeIntention("Use");
    }

    public void testWithConstructionMultiFix() {
        executeIntention("Use");
    }

    public void testGrepFirstFixWhateverSingle() {
        executeIntention("Replace");
    }

    public void testGrepFirstFixWhateverMany() {
        executeIntention("Replace");
    }

    public void testGrepFirstFixBlockMany() {
        executeIntention("Replace");
    }

    public void testMakeMethodPublicIntention() {
        executeIntention("Make");
    }

    public void testMakeMethodPublicOnNameIntention() {
        executeIntention("Make");
    }

    public void testMakeMethodPublicIntentionIsForPrivateOnly() {
        checkIntentionAbsence("Make method public");
    }

    public void testMakeMethodPrivateIntention() {
        executeIntention("Make method private");
    }

    public void testMakeMethodSubmethod() {
        executeIntention("Make submethod");
    }

    public void testArrayInitializationRemoval() {
        executeIntention("Remove redundant");
    }

    public void testAwaitAllOfUnwrapArray() {
        executeIntention("Unwrap Promise.allof");
    }

    public void testAwaitAllOfUnwrapInfix() {
        executeIntention("Unwrap Promise.allof");
    }

    public void testAwaitAllOfUnwrapPrefix() {
        executeIntention("Unwrap Promise.allof");
    }

    public void testPerl6ExecutableStrFix() {
        executeIntention("Use $*EXECUTABLE");
    }

    public void testUnparenSimple() {
        executeIntention("Remove parentheses");
    }

    public void testUnparenInfix() {
        executeIntention("Remove parentheses");
    }

    public void testUnparenInitializer() {
        executeIntention("Remove parentheses");
    }

    public void testEmptyUnparenNotAllowed() {
        checkIntentionAbsence("Remove parentheses");
    }

    public void testUnparenOnColonpairNotAllowed() {
        checkIntentionAbsence("Remove parentheses");
    }

    public void testBindingDestructuringFix() {
        executeIntention("Use binding");
    }

    public void testQuotesConversion1() {
        executeIntention("Convert to double");
    }

    public void testQuotesConversion2() {
        executeIntention("Convert to single");
    }

    public void testNoQuotesConversionForStr() {
        checkIntentionAbsence("Convert to double");
        checkIntentionAbsence("Convert to single");
    }

    public void testConditionalBlockConversion1() {
        executeIntention("Convert to block");
    }

    public void testConditionalBlockConversion2() {
        executeIntention("Convert to block");
    }

    public void testNonCapturingGroupIntoPos() { executeIntention("Convert into positional"); }

    public void testNonCapturingGroupIntoPosNonFlatting() {
        executeIntention("Convert into positional");
    }

    public void testNonCapturingGroupIntoNamed() { executeIntention("Convert into named"); }

    public void testPositionalCapturingIntoNamed() {
        executeIntention("Convert");
    }

    public void testTernaryStatementConversion() {
        executeIntention("Convert");
    }

    public void testTernaryExprConversion() {
        executeIntention("Convert");
    }

    public void testNoSplittingWithoutInitializer() {
        checkIntentionAbsence("Split into");
    }

    public void testSplittingDeclaration() {
        executeIntention("Split into");
    }

    public void testASCIIToUni() {
        executeIntention("Convert to Uni");
    }

    public void testUniToASCII() {
        executeIntention("Convert to ASCII");
    }

    public void testASCIICannotBeConvertedIntoASCII() {
        checkIntentionAbsence("Convert to ASCII");
    }

    public void testNoUnicodeEditingInStrings() {
        checkIntentionAbsence("Convert to Uni");
    }

    public void testASCIITermToUni() {
        executeIntention("Convert term to Unicode");
    }

    public void testUniTermToASCII() {
        executeIntention("Convert term to ASCII");
    }

    public void testUseDirectAttributeAccess() {
        executeIntention("Replace with direct access");
    }

    public void testFatarrowToColonpair() {
        executeIntention("Convert to");
    }

    public void testColonpairToFatarrow() {
        executeIntention("Convert to");
    }

    public void testColonpairToFatarrowParen() {
        executeIntention("Convert to");
    }

    public void testColonpairForms1() { executeIntention("Convert to "); }
    public void testColonpairForms2() { executeIntention("Convert to "); }
    public void testColonpairForms3() { executeIntention("Convert to "); }

    public void testSubStubbing() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "he<caret>he(42, :two, :$bar);");
        List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions("Create");
        assertSize(1, availableIntentions);
        myFixture.launchAction(availableIntentions.get(0));
        myFixture.checkResult("sub hehe($p, :$two, :$bar) {}\nhehe(42, :two, :$bar);");
    }

    public void testSubroutineDeletion() {
        executeIntention("Safe delete");
    }
    public void testMethodDeletion() {
        executeIntention("Safe delete");
    }
    public void testSubDeletionRemovesDocs() {
        executeIntention("Safe delete");
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
