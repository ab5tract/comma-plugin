package edument.rakuidea.traits;

import com.intellij.psi.util.PsiTreeUtil;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;
import edument.rakuidea.psi.RakuPackageDecl;
import edument.rakuidea.psi.RakuTrait;

import java.util.List;

public class TraitsTest extends CommaFixtureTestCase {
    public void testIsExportTraitData() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "role Fo<caret>o is export {}");
        RakuPackageDecl usage = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), RakuPackageDecl.class);
        assertNotNull(usage);
        List<RakuTrait> traits = usage.getTraits();
        assertTrue(traits.size() != 0);
        assertEquals("is", traits.get(0).getTraitModifier());
        assertEquals("export", traits.get(0).getTraitName());
    }

    public void testDoesTraitData() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "role Fo<caret>o does Bar {}");
        RakuPackageDecl usage = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), RakuPackageDecl.class);
        assertNotNull(usage);
        List<RakuTrait> traits = usage.getTraits();
        assertTrue(traits.size() != 0);
        assertEquals("does", traits.get(0).getTraitModifier());
        assertEquals("Bar", traits.get(0).getTraitName());
    }
}
