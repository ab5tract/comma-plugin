package edument.rakuidea.stub;

import com.intellij.lang.FileASTNode;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.containers.ContainerUtil;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;
import edument.rakuidea.psi.RakuPackageDecl;
import edument.rakuidea.psi.stub.*;
import edument.rakuidea.psi.symbols.MOPSymbolsAllowed;
import edument.rakuidea.psi.symbols.RakuSymbol;
import edument.rakuidea.psi.symbols.RakuSymbolKind;
import edument.rakuidea.psi.symbols.RakuVariantsSymbolCollector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RakuStubTest extends CommaFixtureTestCase {
    private StubBuilder myBuilder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myBuilder = new RakuFileStubBuilder();
    }

    @Override
    protected void tearDown() throws Exception {
        myBuilder = null;
        super.tearDown();
    }

    public void testEmpty() {
        doTest("",
                "RakuFileStubImpl\n");
    }

    public void testConstant() {
        StubElement<?> e = doTest("constant $foo = 5;",
                "RakuFileStubImpl\n" +
                        "  CONSTANT:RakuConstantStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        RakuConstantStub stub = (RakuConstantStub) childrenStubs.get(0);
        assertEquals("$foo", stub.getConstantName());
    }

    public void testEnum() {
        StubElement<?> e = doTest("enum Class <Wizard Crusader Priest>;",
                "RakuFileStubImpl\n" +
                        "  ENUM:RakuEnumStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        RakuEnumStub enumStub = (RakuEnumStub)childrenStubs.get(0);
        assertEquals("Class", enumStub.getTypeName());
        assertTrue(enumStub.isExported());
        assertEquals("our", enumStub.getScope());
        assertEquals(Arrays.asList("Wizard", "Crusader", "Priest"), enumStub.getEnumValues());
    }

    public void testRegex() {
        StubElement<?> e = doTest("regex aa <1 2 3 4 5>",
                "RakuFileStubImpl\n" +
                        "  REGEX_DECLARATION:RakuRegexDeclStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        RakuRegexDeclStub stub = (RakuRegexDeclStub) childrenStubs.get(0);
        assertEquals("aa", stub.getRegexName());
    }

    public void testSubset() {
        StubElement<?> e = doTest("subset Alpha of Int;",
                "RakuFileStubImpl\n" +
                        "  SUBSET:RakuSubsetStubImpl\n" +
                        "    TYPE_NAME:RakuTypeNameStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        RakuSubsetStub stub = (RakuSubsetStub) childrenStubs.get(0);
        assertEquals("Alpha", stub.getTypeName());
        assertEquals("Int", stub.getSubsetBaseTypeName());
    }

    public void testNeed() {
        StubElement<?> e = doTest("need Foo::Bar; need Foo::Baz;",
                "RakuFileStubImpl\n" +
                        "  NEED_STATEMENT:RakuNeedStatementStubImpl\n" +
                        "  NEED_STATEMENT:RakuNeedStatementStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(2, childrenStubs.size());
        RakuNeedStatementStub stub1 = (RakuNeedStatementStub) childrenStubs.get(0);
        assertEquals(Collections.singletonList("Foo::Bar"), stub1.getModuleNames());
        RakuNeedStatementStub stub2 = (RakuNeedStatementStub) childrenStubs.get(1);
        assertEquals(Collections.singletonList("Foo::Baz"), stub2.getModuleNames());
    }

    public void testUse() {
        StubElement<?> e = doTest("use Foo::Bar; use Foo::Baz;",
                "RakuFileStubImpl\n" +
                        "  USE_STATEMENT:RakuUseStatementStubImpl\n" +
                        "  USE_STATEMENT:RakuUseStatementStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(2, childrenStubs.size());
        RakuUseStatementStub stub1 = (RakuUseStatementStub) childrenStubs.get(0);
        assertEquals("Foo::Bar", stub1.getModuleName());
        RakuUseStatementStub stub2 = (RakuUseStatementStub) childrenStubs.get(1);
        assertEquals("Foo::Baz", stub2.getModuleName());
    }

    public void testVariableScopedDependantStubbing() {
        doTest("my $foo; our $baz",
                "RakuFileStubImpl\n" +
                "  SCOPED_DECLARATION:RakuScopedDeclStubImpl\n" +
                "    VARIABLE_DECLARATION:RakuVariableDeclStubImpl\n");
        doTest("has $foo;",
                "RakuFileStubImpl\n" +
                        "  SCOPED_DECLARATION:RakuScopedDeclStubImpl\n" +
                        "    VARIABLE_DECLARATION:RakuVariableDeclStubImpl\n");
        doTest("our $bar is export = 10;",
                "RakuFileStubImpl\n" +
                        "  SCOPED_DECLARATION:RakuScopedDeclStubImpl\n" +
                        "    VARIABLE_DECLARATION:RakuVariableDeclStubImpl\n" +
                        "      TRAIT:RakuTraitStubImpl\n");
    }

    public void testClassWithAttributesAndMethods() {
        StubElement<?> e = doTest("class Foo { method mm {}; method !kk {}; has $!foo; has Int $.bar }",
                "RakuFileStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:RakuRoutineDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:RakuRoutineDeclStubImpl\n" +
                        "    SCOPED_DECLARATION:RakuScopedDeclStubImpl\n" +
                        "      VARIABLE_DECLARATION:RakuVariableDeclStubImpl\n" +
                        "    SCOPED_DECLARATION:RakuScopedDeclStubImpl\n" +
                        "      TYPE_NAME:RakuTypeNameStubImpl\n" +
                        "      VARIABLE_DECLARATION:RakuVariableDeclStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        assert childrenStubs.get(0) instanceof RakuPackageDeclStub;
        RakuPackageDeclStub packageDeclStub = (RakuPackageDeclStub) childrenStubs.get(0);
        assertEquals("class", packageDeclStub.getPackageKind());
        childrenStubs = packageDeclStub.getChildrenStubs();
        assertEquals(4, childrenStubs.size());
        RakuRoutineDeclStub routine1 = (RakuRoutineDeclStub) childrenStubs.get(0);
        RakuRoutineDeclStub routine2 = (RakuRoutineDeclStub) childrenStubs.get(1);
        assertFalse(routine1.isPrivate());
        assertEquals("method", routine1.getRoutineKind());
        assertEquals("mm", routine1.getRoutineName());
        assertTrue(routine2.isPrivate());
        assertEquals("method", routine2.getRoutineKind());
        assertEquals("!kk", routine2.getRoutineName());

        RakuScopedDeclStub scopedDeclStub1 = (RakuScopedDeclStub) childrenStubs.get(2);
        RakuScopedDeclStub scopedDeclStub2 = (RakuScopedDeclStub) childrenStubs.get(3);

        // Test of first attr
        assertEquals("has", scopedDeclStub1.getScope());
        childrenStubs = scopedDeclStub1.getChildrenStubs();
        assertEquals(1, childrenStubs.size());
        RakuVariableDeclStub variableDeclStub1 = (RakuVariableDeclStub) childrenStubs.get(0);
        assertEquals("$!foo", variableDeclStub1.getVariableNames()[0]);
        assertEquals("Any", variableDeclStub1.getVariableType());

        // Test of second attr
        assertEquals("has", scopedDeclStub2.getScope());
        childrenStubs = scopedDeclStub2.getChildrenStubs();
        assertEquals(2, childrenStubs.size());
        RakuTypeNameStub typeNameStub = (RakuTypeNameStub) childrenStubs.get(0);
        assertEquals("Int", typeNameStub.getTypeName());
        RakuVariableDeclStub variableDeclStub2 = (RakuVariableDeclStub) childrenStubs.get(1);
        assertEquals("$!bar", variableDeclStub2.getVariableNames()[0]);
        assertEquals("$.bar", variableDeclStub2.getVariableNames()[1]);
        assertEquals("Int", variableDeclStub2.getVariableType());
    }

    public void testPackageTrait() {
        StubElement<?> e = doTest("role One {}; class Two does One {}; class Three is Two {};",
                "RakuFileStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    TRAIT:RakuTraitStubImpl\n" +
                        "      TYPE_NAME:RakuTypeNameStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    TRAIT:RakuTraitStubImpl\n");
        List<?> childrenStubs = e.getChildrenStubs();
        assertEquals(3, childrenStubs.size());
    }

    public void testAlso() {
        StubElement<?> e = doTest("role One {}; class Base {}; class Two { also does One; also is Base; }",
                "RakuFileStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    TRAIT:RakuTraitStubImpl\n" +
                        "      TYPE_NAME:RakuTypeNameStubImpl\n" +
                        "    TRAIT:RakuTraitStubImpl\n");
    }

    public void testTrusts() {
        StubElement<?> e = doTest("class Two { trusts One; trusts Base; }",
                "RakuFileStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    TYPE_NAME:RakuTypeNameStubImpl\n" +
                        "    TYPE_NAME:RakuTypeNameStubImpl\n");
    }

    public void testEmptyConstant() {
        doTest("constant = 1;",
               "RakuFileStubImpl\n");
    }

    public void testStubbedRoleUsageInComposition() {
        StubElement<?> e = doTest("my role Base { method mmm {}; method bbb {}; }; class C does Base { method ddd {}; };",
                "RakuFileStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:RakuRoutineDeclStubImpl\n" +
                        "    ROUTINE_DECLARATION:RakuRoutineDeclStubImpl\n" +
                        "  PACKAGE_DECLARATION:RakuPackageDeclStubImpl\n" +
                        "    TRAIT:RakuTraitStubImpl\n" +
                        "      TYPE_NAME:RakuTypeNameStubImpl\n" +
                        "    ROUTINE_DECLARATION:RakuRoutineDeclStubImpl\n");
        StubElement<?> stub = e.getChildrenStubs().get(1);
        RakuPackageDecl decl = (RakuPackageDecl) stub.getPsi();
        RakuVariantsSymbolCollector collector = new RakuVariantsSymbolCollector(RakuSymbolKind.Method);
        decl.contributeMOPSymbols(collector, new MOPSymbolsAllowed(false, false, false, false));
        List<String> names = ContainerUtil.map(collector.getVariants(), RakuSymbol::getName);
        assertTrue(names.containsAll(Arrays.asList(".ddd", ".mmm", ".bbb")));
    }

    private StubElement<?> doTest(String source, String expected) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, source);
        PsiFile file = myFixture.getFile();
        FileASTNode fileASTNode = file.getNode();
        assertNotNull(fileASTNode);

        StubElement<?> stubTree = myBuilder.buildStubTree(file);

        file.getNode().getChildren(null); // force switch to AST
        StubElement<?> astBasedTree = myBuilder.buildStubTree(file);

        assertEquals(expected, DebugUtil.stubTreeToString(stubTree));
        assertEquals(expected, DebugUtil.stubTreeToString(astBasedTree));
        return stubTree;
    }
}
