package edument.perl6idea.cro.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.cro.template.CroTemplateFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CroTemplateCompletionTest extends CommaFixtureTestCase {
    public void testCompletionOfSubArguments() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
            "<:sub foo($v1, $v2, $v3)> <$<caret> </:>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(3, vars);
        assertContainsElements(vars, Arrays.asList("$v1", "$v2", "$v3"));
    }

    public void testCompletionOfMacroArguments() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
          "<:macro foo($v1, $v2)> <$<caret> </:>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(2, vars);
        assertContainsElements(vars, Arrays.asList("$v1", "$v2"));
    }

    public void testCompletionOfIterationVariable() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
          "<@foo : $item> <@bar : $another> <$<caret> </@> </@>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(2, vars);
        assertContainsElements(vars, Arrays.asList("$item", "$another"));
    }

    public void testCompletionOfSub() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
          "<:sub s1()>abc</:> <:sub s2($x, $y)>def</:> <&<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(2, vars);
        assertContainsElements(vars, Arrays.asList("s1", "s2"));
    }

    public void testCompletionOfMacro() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
          "<:macro m1()>abc</:> <:macro m2($x, $y)>def</:> <|<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(2, vars);
        assertContainsElements(vars, Arrays.asList("m1", "m2"));
    }
}
