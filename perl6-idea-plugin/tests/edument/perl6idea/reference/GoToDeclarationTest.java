package edument.perl6idea.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;

public class GoToDeclarationTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/reference";
    }

    public void testLocalVarReference() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a = 5; say $a<caret>;");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        assertNotNull(usage);
        PsiElement rawWar = usage.getParent();
        Perl6Variable var = (Perl6Variable) rawWar;
        assertNotNull(var);
        PsiElement decl = myFixture.getFile().findElementAt(1);
        assertNotNull(decl);
        PsiReference reference = var.getReference();
        assertNotNull(reference);
        PsiElement scopedDecl = PsiTreeUtil.getParentOfType(reference.resolve(), Perl6ScopedDecl.class);
        assertEquals(decl.getParent(), scopedDecl);
    }

    public void testUseExternalReference() {
        // TODO
    }

    public void testPrivateMethodsReference() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Foo { method !a{} }; class Bar does Foo { method !b{ self!<caret>a; } }");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();
        PsiElement resolved = usage.getReference().resolve();
        PsiElement role = PsiTreeUtil.getParentOfType(resolved, Perl6PackageDecl.class);
        assertNotNull(role);
        assertTrue(role instanceof Perl6PackageDecl);
        assertEquals("Foo", ((Perl6PackageDecl)role).getPackageName());
    }

    public void testOverloadedPrivateMethodReference() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Foo { method !a{} }; class Bar does Foo { method !a{}; method !b{ self!<caret>a; } }");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();
        PsiElement resolved = usage.getReference().resolve();
        PsiElement class_ = PsiTreeUtil.getParentOfType(resolved, Perl6PackageDecl.class);
        assertNotNull(class_);
        assertTrue(class_ instanceof Perl6PackageDecl);
        assertEquals("Bar", ((Perl6PackageDecl)class_).getPackageName());
    }
}
