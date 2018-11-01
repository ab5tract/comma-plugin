package edument.perl6idea.traits;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Trait;

import java.util.List;

public class TraitsTest extends LightCodeInsightFixtureTestCase {
    public void testIsExportTraitData() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Fo<caret>o is export {}");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertNotNull(usage);
        assertTrue(usage instanceof Perl6PackageDecl);
        List<Perl6Trait> traits = ((Perl6PackageDecl)usage).getTraits();
        assertTrue(traits.size() != 0);
        assertEquals("is", traits.get(0).getTraitModifier());
        assertEquals("export", traits.get(0).getTraitName());
    }

    public void testDoesTraitData() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Fo<caret>o does Bar {}");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertNotNull(usage);
        assertTrue(usage instanceof Perl6PackageDecl);
        List<Perl6Trait> traits = ((Perl6PackageDecl)usage).getTraits();
        assertTrue(traits.size() != 0);
        assertEquals("does", traits.get(0).getTraitModifier());
        assertEquals("Bar", traits.get(0).getTraitName());
    }
}
