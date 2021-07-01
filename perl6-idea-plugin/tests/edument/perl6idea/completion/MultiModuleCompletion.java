package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.LightProjectDescriptor;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.Perl6MultiModuleProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class MultiModuleCompletion extends CommaFixtureTestCase {
    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return new Perl6MultiModuleProjectDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/multi-module";
    }

    public void testCrossModules() {
        myFixture.copyFileToProject("Module/Inner.pm6", "../lib/Module/Inner.pm6");
        myFixture.configureByText("10-test.t", "use Module::Inner; Foo.mm<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methodsFromAnotherModule = myFixture.getLookupElementStrings();
        assertContainsElements(methodsFromAnotherModule, ".mmmm", ".mmmmmmmm");
    }
}
