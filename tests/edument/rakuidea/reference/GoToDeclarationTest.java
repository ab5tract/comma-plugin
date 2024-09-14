package edument.rakuidea.reference;

import com.intellij.ide.actions.GotoRelatedSymbolAction;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;
import edument.rakuidea.psi.*;

import java.util.List;
import java.util.function.Consumer;

public class GoToDeclarationTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/reference";
    }

    public void testLocalVariable1() {
        doTest("my $a = 5; say $a<caret>;", 1,
               RakuVariable.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(3, decl.getTextOffset());
                });
    }

    public void testLocalVariable2() {
        doTest("our $a = 5; say $a<caret>;", 1,
               RakuVariable.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(4, decl.getTextOffset());
                });
    }

    public void testExternalVariable1() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(42);
        RakuVariable var = PsiTreeUtil.getParentOfType(usage, RakuVariable.class);
        PsiElement resolved = var.getReference().resolve();
        assertNull(resolved);
    }

    public void testExternalVariable2() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(60);
        RakuVariable var = PsiTreeUtil.getParentOfType(usage, RakuVariable.class);
        PsiElement resolved = var.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
    }

    public void testUseLocalType() {
        doTest("class Foo; Fo<caret>o.new;", 1,
               RakuTypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(6, decl.getTextOffset());
                });
    }

    public void testUseLocalTypeMultiPart() {
        doTest("class Foo::Bar::Baz; Foo::<caret>Bar::Baz.new;", 1,
               RakuTypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(6, decl.getTextOffset());
                });
    }

    public void testUseLocalTypeParametrized() {
        doTest("class Foo::Bar[TypeName]; Foo::<caret>Bar.new;", 1,
               RakuTypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(6, decl.getTextOffset());
                });
    }

    public void testUseExternalType() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(25);
        RakuTypeName type = PsiTreeUtil.getParentOfType(usage, RakuTypeName.class);
        PsiElement resolved = type.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
    }

    public void testUseExternalRoutine1() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(80);
        RakuSubCallName call = PsiTreeUtil.getParentOfType(usage, RakuSubCallName.class);
        PsiElement resolved = call.getReference().resolve();
        assertNotNull(resolved);
        PsiFile file = resolved.getContainingFile();
        assertEquals("Bar.pm6", file.getName());
    }

    public void testUseExternalRoutine2() {
        myFixture.configureByFiles("IdeaFoo/Baz.pm6", "IdeaFoo/Bar.pm6");
        PsiElement usage = myFixture.getFile().findElementAt(100);
        RakuSubCallName call = PsiTreeUtil.getParentOfType(usage, RakuSubCallName.class);
        PsiElement resolved = call.getReference().resolve();
        assertNull(resolved);
    }

    public void testPrivateMethodsReference() {
        doTest("class Foo { has $.foo; method test { $.fo<caret>o; } }", 0,
               RakuMethodCall.class, (call) -> assertTrue(call instanceof RakuVariableDecl));
    }

    public void testOverloadedPrivateMethodReference() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "role Foo { method !a{} }; class Bar does Foo { method !a{}; method !b{ self!<caret>a; } }");
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();
        PsiElement resolved = usage.getReference().resolve();
        RakuPackageDecl class_ = PsiTreeUtil.getParentOfType(resolved, RakuPackageDecl.class);
        assertNotNull(class_);
        assertEquals("Bar", class_.getPackageName());
    }

    public void testAttributeByCall() {
        doTest("class Foo { has $.foo; method test { $.fo<caret>o; } }", 0,
               RakuMethodCall.class, (call) -> assertTrue(call instanceof RakuVariableDecl));
    }

    public void testMultipleInheritance() {
        doTest("role Foo {}; class Bar does Foo {}; Ba<caret>r.new;", 1,
               RakuTypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(19, decl.getTextOffset());
                });
    }

    public void testEnumType() {
        doTest("enum Foos <Foo1 Foo2>; my Fo<caret>o1 $foo;", 1,
               RakuTypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(5, decl.getTextOffset());
                });
    }

    public void testEnumFullType() {
        doTest("enum Foos <Foo1 Foo2>; my Foos::Fo<caret>o1 $foo;", 1,
               RakuTypeName.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(5, decl.getTextOffset());
                });
    }

    public void testNamelessPackageSelf() {
        doTest("say 13; class { method { say se<caret>lf; } }", 1,
               RakuSelf.class, (decl) -> {
                    assertNotNull(decl);
                    assertEquals(8, decl.getTextOffset());
                });
    }

    public void testMultiVariableDeclaration() {
        doTest("class A { has ($.aaa, $.bbb); method { say $!a<caret>aa } }", 1,
               RakuVariable.class, (var) -> {
                assertNotNull(var);
                assertEquals("($.aaa, $.bbb)", var.getText());
            });
    }

    public void testIndirectPrivateMethod() {
        doTest("class A { class C { method !p() { say 42 }; method m() { my $c = A::C.new; $c!<caret>p } }; }; A::C.m;", 1,
               RakuMethodCall.class, (decl) -> {
                assertNotNull(decl);
                assertEquals("method !p() { say 42 }", decl.getText());
        });
    }

    public void testFormalParameters() {
        doTest("sub foo { $^a + $<caret>a }", 1, RakuVariable.class, (var) -> {
            assertNotNull(var);
            assertEquals("$^a", var.getText());
        });
        doTest("sub foo { $:a + $<caret>a }", 1, RakuVariable.class, (var) -> {
            assertNotNull(var);
            assertEquals("$:a", var.getText());
        });
    }

    public void testDeferredType() {
        doTest("class A {...}; class B { has <caret>A $.x; }; class A { has $.foo; }", 0, RakuTypeName.class, (pkg) -> assertNotNull(pkg));
    }

    public void testDynamicVariable() {
        doTest("{ say $*DYNAM<caret>IC; }; { my $*DYNAMIC }", 0, RakuVariable.class, (decl) -> {
            assertNotNull(decl);
            assertEquals(25, decl.getTextOffset());
        });
    }

    public void testJumpToTemplateFile() throws InterruptedException {
        ensureModuleIsLoaded("Cro::WebApp::Template");
        myFixture.configureByFiles("IdeaFoo/TemplateUser.pm6",
                                   "IdeaFoo/content.crotmp",
                                   "IdeaFoo/templates/inner-content.crotmp",
                                   "IdeaFoo/templates2/inner-content2.crotmp");
        myFixture.getEditor().getCaretModel().moveToOffset(96);
        List<GotoRelatedItem> items = GotoRelatedSymbolAction.getItems(myFixture.getFile(), myFixture.getEditor(), null);
        assertEquals(1, items.size());
    }

    public void testJumpToTemplateFileInDirectory() throws InterruptedException {
        ensureModuleIsLoaded("Cro::WebApp::Template");
        myFixture.configureByFiles("IdeaFoo/TemplateUser.pm6",
                                   "IdeaFoo/content.crotmp",
                                   "IdeaFoo/templates/inner-content.crotmp",
                                   "IdeaFoo/templates2/inner-content2.crotmp");
        myFixture.getEditor().getCaretModel().moveToOffset(207);
        List<GotoRelatedItem> items = GotoRelatedSymbolAction.getItems(myFixture.getFile(), myFixture.getEditor(), null);
        assertEquals(1, items.size());
    }

    public void testJumpToTemplateFileInDirectoryAbsolute() throws InterruptedException {
        ensureModuleIsLoaded("Cro::WebApp::Template");
        myFixture.configureByFiles("IdeaFoo/TemplateUser.pm6",
                                   "IdeaFoo/content.crotmp",
                                   "IdeaFoo/templates/inner-content.crotmp",
                                   "IdeaFoo/templates/inner-content2.crotmp",
                                   "IdeaFoo/templates2/inner-content2.crotmp");
        myFixture.getEditor().getCaretModel().moveToOffset(405);
        List<GotoRelatedItem> items = GotoRelatedSymbolAction.getItems(myFixture.getFile(), myFixture.getEditor(), null);
        assertEquals(1, items.size());
    }

    public void doTest(String text, int offset, Class<? extends RakuPsiElement> clazz, Consumer<PsiElement> check) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        PsiElement usage = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - offset);
        RakuPsiElement var = PsiTreeUtil.getParentOfType(usage, clazz);
        check.accept(var.getReference().resolve());
    }
}
