package edument.perl6idea.findUsages;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import edument.perl6idea.Perl6LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FindUsageTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/findUsage";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    // When declarator has different PsiElement comparing to usage,
    // it is not counted in this test, yet it is correctly resolved

    public void testVariableDefinition() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("VariableDefinition.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testVariable() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Variable.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testOuterVariable1() {
        myFixture.configureByFiles("IdeaFoo/User.pm6", "IdeaFoo/Base.pm6");
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(2, usages.size());
    }

    public void testOuterVariable2() {
        myFixture.configureByFiles("IdeaFoo/Base.pm6", "IdeaFoo/User.pm6");
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(2, usages.size());
    }

    public void testVariablesInBlock() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("VariableBlock.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testVariableFromParameter1() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("VariableFromParameter.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testVariableFromParameter2() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("VariableFromParameterOnDeclaration.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testPrivateAttributeOfClass() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("AttributeOfClass.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testPrivateAttributeOfClass1() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("AttributeOfClass1.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testPrivateAttributeFromRole() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("AttributeFromRole.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testPrivateAttributeFromRole1() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("AttributeFromRole1.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testPrivateAttributeFromOuterRole1() {
        myFixture.configureByFiles("IdeaFoo2/Base.pm6", "IdeaFoo2/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(24);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        // FIXME
        // assertEquals(4, usages.size());
    }

    public void testPrivateAttributeFromOuterRole2() {
        myFixture.configureByFiles("IdeaFoo2/Base.pm6", "IdeaFoo2/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(44);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(5, usages.size());
    }

    public void testComposedAndInherited() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("RoleClassAttribute.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testPrivateMethod() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("PrivateMethod.p6");
        assertEquals(4, usageInfos.size());
    }

    public void testPrivateMethodFromRole() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("PrivateMethodFromRole.p6");
        assertEquals(4, usageInfos.size());
    }

    public void testPrivateMethodFromRoleOverloaded() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("PrivateMethodFromRoleOverloaded.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testSub() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Sub.p6");
        assertEquals(4, usageInfos.size());
    }

    public void testOuterSub1() {
        myFixture.configureByFiles("IdeaFoo3/Base.pm6", "IdeaFoo3/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(6);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(4, usages.size());
    }

    public void testOuterSub2() {
        myFixture.configureByFiles("IdeaFoo3/Base.pm6", "IdeaFoo3/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(39);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(2, usages.size());
    }

    public void testTypeDefinition() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("TypeDefinition.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testType() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Type.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testRole() {
        myFixture.configureByFiles("IdeaFoo4/Base.pm6", "IdeaFoo4/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(7);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(9, usages.size());
    }

    public void testGrammar() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Grammar.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testMonitor() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Monitor.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testSubsetOuter1() {
        myFixture.configureByFiles("IdeaFoo5/Base.pm6", "IdeaFoo5/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(8);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(2, usages.size());
    }

    public void testSubsetOuter2() {
        myFixture.configureByFiles("IdeaFoo5/Base.pm6", "IdeaFoo5/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(50);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(0, usages.size());
    }

    public void testEnum() {
        myFixture.configureByFiles("IdeaFoo6/Base.pm6", "IdeaFoo6/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(7);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(6, usages.size());
    }

    public void testMultiPartNameOuter() {
        myFixture.configureByFiles("IdeaFoo7/Base.pm6", "IdeaFoo7/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(8);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(1, usages.size());
    }

    public void testMultiPartNameMiddle() {
        myFixture.configureByFiles("IdeaFoo7/Base.pm6", "IdeaFoo7/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(24);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(1, usages.size());
    }

    public void testMultiPartNameInner() {
        myFixture.configureByFiles("IdeaFoo7/Base.pm6", "IdeaFoo7/User.pm6");
        myFixture.getEditor().getCaretModel().moveToOffset(43);
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(2, usages.size());
    }
}
