package edument.rakuidea.cro.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.cro.template.CroTemplateFileType;

import java.util.Arrays;
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

    public void testCompletionOfSubFromOtherFile() {
        myFixture.configureByText("stuff.crotmp", "<:sub s3()>abc</:> <:sub s4($x, $y)>def</:>");
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
                "<:use 'stuff.crotmp'> <&<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(2, vars);
        assertContainsElements(vars, Arrays.asList("s3", "s4"));
    }

    public void testCompletionOfMacroFromOtherFile() {
        myFixture.configureByText("stuff.crotmp", "<:macro m3()>abc</:> <:macro m4($x, $y)>def</:>");
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
                "<:use 'stuff.crotmp'> <|<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertSize(2, vars);
        assertContainsElements(vars, Arrays.asList("m3", "m4"));
    }
}
