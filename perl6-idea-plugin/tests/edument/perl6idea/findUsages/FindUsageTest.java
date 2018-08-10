package edument.perl6idea.findUsages;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;

public class FindUsageTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/findUsage";
    }

    public void testFindUsagesForVariableDefinition() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("VariableDefinition.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testFindUsagesForVariable() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Variable.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testFindUsagesForVariablesInBlock() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("VariableBlock.p6");
        assertEquals(3, usageInfos.size());
    }

    public void testFindUsagesForTypeDefinition() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("TypeDefinition.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testFindUsagesForType() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Type.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testFindUsagesForMethod() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Method.p6");
        assertEquals(2, usageInfos.size());
    }

    public void testFindUsagesForSub() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("Sub.p6");
        assertEquals(4, usageInfos.size());
    }
}
