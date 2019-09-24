package edument.perl6idea.stub;

import com.intellij.lang.FileASTNode;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.symbols.MOPSymbolsAllowed;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.psi.symbols.Perl6VariantsSymbolCollector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Perl6StubTest extends CommaFixtureTestCase {
    private StubBuilder myBuilder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myBuilder = new Perl6FileStubBuilder();
    }

    @Override
    protected void tearDown() throws Exception {
        myBuilder = null;
        super.tearDown();
    }

    public void testEmpty() {
        doTest("",
                "Perl6FileStubImpl\n");
    }

    public void testConstant() {
        StubElement e = doTest("constant $foo = 5;",
                "Perl6FileStubImpl\n" +
                        "  CONSTANT:Perl6ConstantStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        Perl6ConstantStub stub = (Perl6ConstantStub) childrenStubs.get(0);
        assertEquals("$foo", stub.getConstantName());
    }

    public void testEnum() {
        StubElement e = doTest("enum Class <Wizard Crusader Priest>;",
                "Perl6FileStubImpl\n" +
                        "  ENUM:Perl6EnumStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        Perl6EnumStub enumStub = (Perl6EnumStub)childrenStubs.get(0);
        assertEquals("Class", enumStub.getTypeName());
        assertFalse(enumStub.isExported());
        assertEquals("our", enumStub.getScope());
        assertEquals(Arrays.asList("Wizard", "Crusader", "Priest"), enumStub.getEnumValues());
    }

    public void testRegex() {
        StubElement e = doTest("regex aa <1 2 3 4 5>",
                "Perl6FileStubImpl\n" +
                        "  REGEX_DECLARATION:Perl6RegexDeclStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        Perl6RegexDeclStub stub = (Perl6RegexDeclStub) childrenStubs.get(0);
        assertEquals("aa", stub.getRegexName());
    }

    public void testSubset() {
        StubElement e = doTest("subset Alpha of Int;",
                "Perl6FileStubImpl\n" +
                        "  SUBSET:Perl6SubsetStubImpl\n" +
                        "    TYPE_NAME:Perl6TypeNameStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        Perl6SubsetStub stub = (Perl6SubsetStub) childrenStubs.get(0);
        assertEquals("Alpha", stub.getTypeName());
        assertEquals("Int", stub.getSubsetBaseTypeName());
    }

    public void testNeed() {
        StubElement e = doTest("need Foo::Bar; need Foo::Baz;",
                "Perl6FileStubImpl\n" +
                        "  NEED_STATEMENT:Perl6NeedStatementStubImpl\n" +
                        "  NEED_STATEMENT:Perl6NeedStatementStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(2, childrenStubs.size());
        Perl6NeedStatementStub stub1 = (Perl6NeedStatementStub) childrenStubs.get(0);
        assertEquals(Collections.singletonList("Foo::Bar"), stub1.getModuleNames());
        Perl6NeedStatementStub stub2 = (Perl6NeedStatementStub) childrenStubs.get(1);
        assertEquals(Collections.singletonList("Foo::Baz"), stub2.getModuleNames());
    }

    public void testUse() {
        StubElement e = doTest("use Foo::Bar; use Foo::Baz;",
                "Perl6FileStubImpl\n" +
                        "  USE_STATEMENT:Perl6UseStatementStubImpl\n" +
                        "  USE_STATEMENT:Perl6UseStatementStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(2, childrenStubs.size());
        Perl6UseStatementStub stub1 = (Perl6UseStatementStub) childrenStubs.get(0);
        assertEquals("Foo::Bar", stub1.getModuleName());
        Perl6UseStatementStub stub2 = (Perl6UseStatementStub) childrenStubs.get(1);
        assertEquals("Foo::Baz", stub2.getModuleName());
    }

    public void testVariableScopedDependantStubbing() {
        doTest("my $foo; our $baz",
                "Perl6FileStubImpl\n");
        doTest("has $foo;",
                "Perl6FileStubImpl\n" +
                        "  SCOPED_DECLARATION:Perl6ScopedDeclStubImpl\n" +
                        "    VARIABLE_DECLARATION:Perl6VariableDeclStubImpl\n");
        doTest("our $bar is export = 10;",
                "Perl6FileStubImpl\n" +
                        "  SCOPED_DECLARATION:Perl6ScopedDeclStubImpl\n" +
                        "    VARIABLE_DECLARATION:Perl6VariableDeclStubImpl\n" +
                        "      TRAIT:Perl6TraitStubImpl\n");
    }

    public void testClassWithAttributesAndMethods() {
        StubElement e = doTest("class Foo { method mm {}; method !kk {}; has $!foo; has Int $.bar }",
                "Perl6FileStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:Perl6RoutineDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:Perl6RoutineDeclStubImpl\n" +
                        "    SCOPED_DECLARATION:Perl6ScopedDeclStubImpl\n" +
                        "      VARIABLE_DECLARATION:Perl6VariableDeclStubImpl\n" +
                        "    SCOPED_DECLARATION:Perl6ScopedDeclStubImpl\n" +
                        "      TYPE_NAME:Perl6TypeNameStubImpl\n" +
                        "      VARIABLE_DECLARATION:Perl6VariableDeclStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        assert childrenStubs.get(0) instanceof Perl6PackageDeclStub;
        Perl6PackageDeclStub packageDeclStub = (Perl6PackageDeclStub) childrenStubs.get(0);
        assertEquals("class", packageDeclStub.getPackageKind());
        childrenStubs = packageDeclStub.getChildrenStubs();
        assertEquals(4, childrenStubs.size());
        Perl6RoutineDeclStub routine1 = (Perl6RoutineDeclStub) childrenStubs.get(0);
        Perl6RoutineDeclStub routine2 = (Perl6RoutineDeclStub) childrenStubs.get(1);
        assertFalse(routine1.isPrivate());
        assertEquals("method", routine1.getRoutineKind());
        assertEquals("mm", routine1.getRoutineName());
        assertTrue(routine2.isPrivate());
        assertEquals("method", routine2.getRoutineKind());
        assertEquals("!kk", routine2.getRoutineName());

        Perl6ScopedDeclStub scopedDeclStub1 = (Perl6ScopedDeclStub) childrenStubs.get(2);
        Perl6ScopedDeclStub scopedDeclStub2 = (Perl6ScopedDeclStub) childrenStubs.get(3);

        // Test of first attr
        assertEquals("has", scopedDeclStub1.getScope());
        childrenStubs = scopedDeclStub1.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        Perl6VariableDeclStub variableDeclStub1 = (Perl6VariableDeclStub) childrenStubs.get(0);
        assertEquals("$!foo", variableDeclStub1.getVariableNames()[0]);
        assertNull(variableDeclStub1.getVariableType());

        // Test of second attr
        assertEquals("has", scopedDeclStub2.getScope());
        childrenStubs = scopedDeclStub2.getChildrenStubs();
        assertEquals(2, childrenStubs.size());
        Perl6TypeNameStub typeNameStub = (Perl6TypeNameStub) childrenStubs.get(0);
        assertEquals("Int", typeNameStub.getTypeName());
        Perl6VariableDeclStub variableDeclStub2 = (Perl6VariableDeclStub) childrenStubs.get(1);
        assertEquals("$.bar", variableDeclStub2.getVariableNames()[0]);
        assertEquals("Int", variableDeclStub2.getVariableType());
    }

    public void testPackageTrait() {
        StubElement e = doTest("role One {}; class Two does One {}; class Three is Two {};",
                "Perl6FileStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    TRAIT:Perl6TraitStubImpl\n" +
                        "      TYPE_NAME:Perl6TypeNameStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    TRAIT:Perl6TraitStubImpl\n");
        List childrenStubs = e.getChildrenStubs();
        assertEquals(3, childrenStubs.size());
    }

    public void testAlso() {
        StubElement e = doTest("role One {}; class Base {}; class Two { also does One; also is Base; }",
                "Perl6FileStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    TRAIT:Perl6TraitStubImpl\n" +
                        "      TYPE_NAME:Perl6TypeNameStubImpl\n" +
                        "    TRAIT:Perl6TraitStubImpl\n");
    }

    public void testTrusts() {
        StubElement e = doTest("class Two { trusts One; trusts Base; }",
                "Perl6FileStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    TYPE_NAME:Perl6TypeNameStubImpl\n" +
                        "    TYPE_NAME:Perl6TypeNameStubImpl\n");
    }

    public void testEmptyConstant() {
        doTest("constant = 1;",
               "Perl6FileStubImpl\n");
    }

    public void testStubbedRoleUsageInComposition() {
        StubElement e = doTest("my role Base { method mmm {}; method bbb {}; }; class C does Base { method ddd {}; };",
                "Perl6FileStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:Perl6RoutineDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:Perl6RoutineDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:Perl6PackageDeclStubImpl\n" +
                        "    TRAIT:Perl6TraitStubImpl\n" +
                        "      TYPE_NAME:Perl6TypeNameStubImpl\n" +
                        "    ROUTINE_DECLARATION:Perl6RoutineDeclStubImpl\n");
        StubElement stub = (StubElement) e.getChildrenStubs().get(1);
        Perl6PackageDecl decl = (Perl6PackageDecl) stub.getPsi();
        Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        decl.contributeMOPSymbols(collector, new MOPSymbolsAllowed(false, false, false , false));
        List<String> names = collector.getVariants().stream().map(Perl6Symbol::getName).collect(Collectors.toList());
        assertTrue(names.containsAll(Arrays.asList(".ddd", ".mmm", ".bbb")));
    }

    private StubElement doTest(String source, String expected) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, source);
        PsiFile file = myFixture.getFile();
        FileASTNode fileASTNode = file.getNode();
        assertNotNull(fileASTNode);

        StubElement stubTree = myBuilder.buildStubTree(file);

        file.getNode().getChildren(null); // force switch to AST
        StubElement astBasedTree = myBuilder.buildStubTree(file);

        assertEquals(expected, DebugUtil.stubTreeToString(stubTree));
        assertEquals(expected, DebugUtil.stubTreeToString(astBasedTree));
        return stubTree;
    }
}
