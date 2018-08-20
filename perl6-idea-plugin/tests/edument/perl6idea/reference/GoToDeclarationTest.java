package edument.perl6idea.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.*;
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

    public void testLocalVariable1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a = 5; say $a<caret>;");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        Perl6Variable var = PsiTreeUtil.getParentOfType(usage, Perl6Variable.class);
        PsiElement decl = var.getReference().resolve();
        assertNotNull(decl);
        assertEquals(3, decl.getTextOffset());
    }

    public void testLocalVariable2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our $a = 5; say $a<caret>;");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        Perl6Variable var = PsiTreeUtil.getParentOfType(usage, Perl6Variable.class);
        PsiElement decl = var.getReference().resolve();
        assertNotNull(decl);
        assertEquals(4, decl.getTextOffset());
    }

    public void testExternalVariable1() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(42);
        Perl6Variable var = PsiTreeUtil.getParentOfType(usage, Perl6Variable.class);
        PsiElement resolved = var.getReference().resolve();
        assertNull(resolved);
    }

    public void testExternalVariable2() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(60);
        Perl6Variable var = PsiTreeUtil.getParentOfType(usage, Perl6Variable.class);
        PsiElement resolved = var.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
    }

    public void testUseLocalType() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo; Fo<caret>o.new;");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        Perl6TypeName var = PsiTreeUtil.getParentOfType(usage, Perl6TypeName.class);
        PsiElement decl = var.getReference().resolve();
        assertNotNull(decl);
        assertEquals(0, decl.getTextOffset());
    }

    public void testUseLocalTypeMultiPart() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo::Bar::Baz; Foo::<caret>Bar::Baz.new;");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        Perl6TypeName var = PsiTreeUtil.getParentOfType(usage, Perl6TypeName.class);
        PsiElement decl = var.getReference().resolve();
        assertNotNull(decl);
        assertEquals(0, decl.getTextOffset());
    }

    public void testUseLocalTypeParametrized() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo::Bar[TypeName]; Foo::<caret>Bar.new;");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        Perl6TypeName var = PsiTreeUtil.getParentOfType(usage, Perl6TypeName.class);
        PsiElement decl = var.getReference().resolve();
        assertNotNull(decl);
        assertEquals(0, decl.getTextOffset());
    }

    public void testUseExternalType() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(25);
        Perl6TypeName type = PsiTreeUtil.getParentOfType(usage, Perl6TypeName.class);
        PsiElement resolved = type.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
    }

    public void testUseExternalRoutine1() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(80);
        Perl6SubCallName call = PsiTreeUtil.getParentOfType(usage, Perl6SubCallName.class);
        PsiElement resolved = call.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
    }

    public void testUseExternalRoutine2() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(100);
        Perl6SubCallName call = PsiTreeUtil.getParentOfType(usage, Perl6SubCallName.class);
        PsiElement resolved = call.getReference().resolve();
        assertNull(resolved);
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

    public void testAttributeByCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { has $.foo; method test { $.fo<caret>o; } }");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        Perl6MethodCall call = PsiTreeUtil.getParentOfType(usage, Perl6MethodCall.class);
        PsiElement resolved = call.getReference().resolve();
        assertTrue(resolved instanceof Perl6VariableDecl);
    }
}
