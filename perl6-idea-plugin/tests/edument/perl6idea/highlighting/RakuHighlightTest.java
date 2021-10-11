package edument.perl6idea.highlighting;

import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class RakuHighlightTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/highlight";
    }

    public void testDuplicatedSubs() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "sub foo { 1 }; <error descr=\"Re-declaration of foo from aaa.p6:1\">sub <weak_warning descr=\"Unused subroutine\">foo</weak_warning></error> { 2 }; foo;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "multi sub b {}; multi <error descr=\"Re-declaration of b from aaa.p6:1\">sub b</error> {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "multi sub c {};\nmulti sub c(Int $n) { $n; };\nmulti <error descr=\"Re-declaration of c from aaa.p6:1\">sub c</error> {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\n\nmulti sub d {};\nmulti sub d(Int $n) { $n; };\nmulti <error descr=\"Re-declaration of d from aaa.p6:4\">sub d</error>(Int $n) { $n; };\nmulti <error descr=\"Re-declaration of d from aaa.p6:3\">sub d</error> {}");
        myFixture.checkHighlighting();
    }

    public void testDuplicatedMethods() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A { method a {}; method b {}; <error descr=\"Re-declaration of a from aaa.p6:1\">method a</error> {} }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "grammar A { rule my-rule {<?>}; rule <error descr=\"Re-declaration of my-rule from aaa.p6:1\">my-rule</error> {<?>}; }");
        myFixture.checkHighlighting();
    }

    public void testDuplicatedRules() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "grammar A {\nrule my-rule {<?>};\ntoken <error descr=\"Re-declaration of my-rule from aaa.p6:2\">my-rule</error> {<?>};\n<error descr=\"Re-declaration of my-rule from aaa.p6:2\">method my-rule</error> { 42 }; { 42 };\n}");
        myFixture.checkHighlighting();
    }

    public void testDuplicateMultiMethods() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class B { multi method b {}; multi <error descr=\"Re-declaration of b from aaa.p6:1\">method b</error> {} }");
        myFixture.checkHighlighting();
    }

    public void testDuplicatedPackages() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\n\nclass A {};\n\n<error descr=\"Re-declaration of A from aaa.p6:3\">class A</error> {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "grammar Baz {}; <error descr=\"Re-declaration of Baz from aaa.p6:1\">grammar Baz</error> {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A { class A {} };");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\nclass A {\nclass B {}\n};\n<error descr=\"Re-declaration of A::B from aaa.p6:3\">class A::B</error> {};");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\nclass A {\nclass B {}\n};\n<error descr=\"Re-declaration of A::B from aaa.p6:3\">class A::B</error> {};");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\nclass A::B {};\nclass A {\n<error descr=\"Re-declaration of A::B from aaa.p6:2\">class B</error> {}\n};");
        myFixture.checkHighlighting();
    }

    public void testDuplicatedRoles() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Baz {}; <error descr=\"Re-declaration of Baz from aaa.p6:1\">role Baz</error> {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Bar[Type] {}; <error descr=\"Re-declaration of Bar from aaa.p6:1\">role Bar</error>[Type] {}; role Bar {};");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Saz {}; role Saz[Type] {}; role Saz[Type1, Type2] {};");
        myFixture.checkHighlighting();
    }

    public void testDuplicatedVariables() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo; say $foo; my <warning descr=\"Re-declaration of $foo from aaa.p6:1\"><weak_warning descr=\"Unused variable\">$foo</weak_warning></warning>; say $foo;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\nmy $bar;\nsay $bar;\n{ my $bar; say $bar; };\nmy <warning descr=\"Re-declaration of $bar from aaa.p6:2\"><weak_warning descr=\"Unused variable\">$bar</weak_warning></warning>;\nsay $bar;");
        myFixture.checkHighlighting();
    }

    public void testDuplicatesInExternal() {
        myFixture.configureByFiles("User.rakumod", "Base.rakumod");
        myFixture.checkHighlighting();
    }
}
