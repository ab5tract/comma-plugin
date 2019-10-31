package edument.perl6idea.traits;

import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Trait;

import java.util.List;

public class TraitsTest extends CommaFixtureTestCase {
    public void testIsExportTraitData() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Fo<caret>o is export {}");
        Perl6PackageDecl usage = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), Perl6PackageDecl.class);
        assertNotNull(usage);
        List<Perl6Trait> traits = usage.getTraits();
        assertTrue(traits.size() != 0);
        assertEquals("is", traits.get(0).getTraitModifier());
        assertEquals("export", traits.get(0).getTraitName());
    }

    public void testDoesTraitData() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Fo<caret>o does Bar {}");
        Perl6PackageDecl usage = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), Perl6PackageDecl.class);
        assertNotNull(usage);
        List<Perl6Trait> traits = usage.getTraits();
        assertTrue(traits.size() != 0);
        assertEquals("does", traits.get(0).getTraitModifier());
        assertEquals("Bar", traits.get(0).getTraitName());
    }
}
