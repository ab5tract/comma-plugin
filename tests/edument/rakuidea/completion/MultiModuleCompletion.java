package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.LightProjectDescriptor;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.RakuMultiModuleProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiModuleCompletion extends CommaFixtureTestCase {
    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return new RakuMultiModuleProjectDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/multi-module";
    }

    public void testCrossModules() {
        myFixture.copyFileToProject("Module/Inner.pm6", "../lib/Module/Inner.pm6");
        myFixture.configureByText("10-test.t", "use Module::Inner; Foo.mm<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methodsFromAnotherModule = myFixture.getLookupElementStrings();
        assertContainsElements(methodsFromAnotherModule, ".mmmm", ".mmmmmmmm");
    }
}
