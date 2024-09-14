package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.List;

public class TraitCompletionTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/codeInsight/localVariables";
    }

    public void testCompletionForRoutineParameter() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "sub foo($a is <caret>) {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "rw");
        assertEquals(5, vars.size());
    }

    public void testCompletionForRoutine() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "sub foo is e<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "export");
        assertEquals(11, vars.size());
    }

    public void testCompletionForMultipleTraits() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "sub foo is rw is e<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "export");
        assertEquals(11, vars.size());
    }

    public void testCompletionForPackage() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "class Foo is I<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, "Int");
    }

    public void testExportTraitAbsenceForMyScopedVariables() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my $foo is exp<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertFalse(vars.contains("export"));
    }

    public void testExportTraitPresenceForOurScopedVariables() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "our $foo is exp<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testAttributeTrait() throws InterruptedException {
        ensureModuleIsLoaded("Cro::WebApp::Form");
        myFixture.configureByText(RakuScriptFileType.INSTANCE,
                                  "use Cro::WebApp::Form; multi sub trait_mod:<is>(Attribute $attr, :$panpakapan) {}; multi sub trait_mod:<is>(Attribute $attr, :$pan) {}; class A { has $foo is pa<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertContainsElements(vars, "password", "package", "panpakapan", "pan");
    }

    public void testRoutineTraits() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE,
                                  "multi sub trait_mod:<is>(Routine $attr, :$panpakapan) {}; multi sub trait_mod:<is>(Routine $attr, :$pan) {}; multi sub trait_mod:<is>(Attribute $attr, :$pattern) {}; sub test(:$foo!) is pa<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertContainsElements(vars, "panpakapan", "pan");
        assertDoesntContain(vars, "pattern");
    }
}
