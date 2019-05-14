package edument.perl6idea.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GoToDeclarationTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/reference";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testLocalVariable1() {
        doTest("my $a = 5; say $a<caret>;", 1,
                Perl6Variable.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(3, decl.getTextOffset());
                });
    }

    public void testLocalVariable2() {
        doTest("our $a = 5; say $a<caret>;", 1,
                Perl6Variable.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(4, decl.getTextOffset());
                });
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
        doTest("class Foo; Fo<caret>o.new;", 1,
                Perl6TypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(6, decl.getTextOffset());
                });
    }

    public void testUseLocalTypeMultiPart() {
        doTest("class Foo::Bar::Baz; Foo::<caret>Bar::Baz.new;", 1,
                Perl6TypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(6, decl.getTextOffset());
                });
    }

    public void testUseLocalTypeParametrized() {
        doTest("class Foo::Bar[TypeName]; Foo::<caret>Bar.new;", 1,
                Perl6TypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(6, decl.getTextOffset());
                });
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
        doTest("class Foo { has $.foo; method test { $.fo<caret>o; } }", 0,
                Perl6MethodCall.class, (call) -> {
                    assertTrue(call instanceof Perl6VariableDecl);
                });
    }

    public void testOverloadedPrivateMethodReference() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Foo { method !a{} }; class Bar does Foo { method !a{}; method !b{ self!<caret>a; } }");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();
        PsiElement resolved = usage.getReference().resolve();
        Perl6PackageDecl class_ = PsiTreeUtil.getParentOfType(resolved, Perl6PackageDecl.class);
        assertNotNull(class_);
        assertEquals("Bar", class_.getPackageName());
    }

    public void testAttributeByCall() {
        doTest("class Foo { has $.foo; method test { $.fo<caret>o; } }", 0,
                Perl6MethodCall.class, (call) -> {
                    assertTrue(call instanceof Perl6VariableDecl);
                });
    }

    public void testMultipleInheritance() {
        doTest("role Foo {}; class Bar does Foo {}; Ba<caret>r.new;", 1,
                Perl6TypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(19, decl.getTextOffset());
                });
    }

    public void testEnumType() {
        doTest("enum Foos <Foo1 Foo2>; my Fo<caret>o1 $foo;", 1,
                Perl6TypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(5, decl.getTextOffset());
                });
    }

    public void testEnumFullType() {
        doTest("enum Foos <Foo1 Foo2>; my Foos::Fo<caret>o1 $foo;", 1,
                Perl6TypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(5, decl.getTextOffset());
                });
    }

    public void testNamelessPackageSelf() {
        doTest("say 13; class { method { say se<caret>lf; } }", 1,
                Perl6Self.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(8, decl.getTextOffset());
                });
    }

    public void doTest(String text, int offset, Class<? extends Perl6PsiElement> clazz, Consumer<PsiElement> check) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - offset);
        Perl6PsiElement var = PsiTreeUtil.getParentOfType(usage, clazz);
        check.accept(var.getReference().resolve());
    }
}
