package edument.perl6idea.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.NotNull;

public class GoToDeclarationTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/reference";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
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
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(25);
        Perl6TypeName type = (Perl6TypeName)PsiTreeUtil.findFirstParent(usage, true, p -> p instanceof Perl6TypeName);
        PsiElement resolved = type.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
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
