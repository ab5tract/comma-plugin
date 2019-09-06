package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6FileStub;
import edument.perl6idea.psi.stub.Perl6NeedStatementStub;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Perl6FileImpl extends PsiFileBase implements Perl6File {
    public static final Map<String, String> VARIABLE_SYMBOLS = new HashMap<>();

    static {
        // compile time variables
        VARIABLE_SYMBOLS.put("$?FILE", "Str");
        VARIABLE_SYMBOLS.put("$?LINE", "Int");
        VARIABLE_SYMBOLS.put("$?LANG", null);
        VARIABLE_SYMBOLS.put("%?RESOURCES", null);
        VARIABLE_SYMBOLS.put("$?PACKAGE", null);
        // pod vars
        VARIABLE_SYMBOLS.put("$=pod", "Array");
        // special variables
        VARIABLE_SYMBOLS.put("$_", null);
        VARIABLE_SYMBOLS.put("$/", "Match");
        VARIABLE_SYMBOLS.put("$!", "Exception");
        // dynamic variables
        VARIABLE_SYMBOLS.put("$*ARGFILES", null);
        VARIABLE_SYMBOLS.put("@*ARGS", "Array");
        VARIABLE_SYMBOLS.put("$*IN", "IO::Special");
        VARIABLE_SYMBOLS.put("$*OUT", "IO::Special");
        VARIABLE_SYMBOLS.put("$*ERR", "IO::Special");
        VARIABLE_SYMBOLS.put("%*ENV", "Hash");
        VARIABLE_SYMBOLS.put("$*REPO", null);
        VARIABLE_SYMBOLS.put("$*INIT-DISTANT", "Instant");
        VARIABLE_SYMBOLS.put("$*TZ", "Int");
        VARIABLE_SYMBOLS.put("$*CWD", "IO::Path");
        VARIABLE_SYMBOLS.put("$*KERNEL", "Kernel");
        VARIABLE_SYMBOLS.put("$*DISTRO", "Distro");
        VARIABLE_SYMBOLS.put("$*VM", "VM");
        VARIABLE_SYMBOLS.put("$*PERL", "Perl");
        VARIABLE_SYMBOLS.put("$*PID", "Int");
        VARIABLE_SYMBOLS.put("$*PROGRAM-NAME", "Str");
        VARIABLE_SYMBOLS.put("$*PROGRAM", "IO::Path");
        VARIABLE_SYMBOLS.put("&*EXIT", null);
        VARIABLE_SYMBOLS.put("$*EXECUTABLE", "IO::Path");
        VARIABLE_SYMBOLS.put("$*EXECUTABLE-NAME", "Str");
        VARIABLE_SYMBOLS.put("$*USER", "IntStr");
        VARIABLE_SYMBOLS.put("$*GROUP", "IntStr");
        VARIABLE_SYMBOLS.put("$*HOMEDRIVE", null);
        VARIABLE_SYMBOLS.put("$*HOMEPATH", null);
        VARIABLE_SYMBOLS.put("$*HOME", "IO::Path");
        VARIABLE_SYMBOLS.put("$*SPEC", "IO::Spec");
        VARIABLE_SYMBOLS.put("$*TMPDIR", "IO::Path");
        VARIABLE_SYMBOLS.put("$*THREAD", "Thread");
        VARIABLE_SYMBOLS.put("$*SCHEDULER", "ThreadPoolScheduler");
        VARIABLE_SYMBOLS.put("$*SAMPLER", null);
        VARIABLE_SYMBOLS.put("$*COLLATION", "Collation");
        VARIABLE_SYMBOLS.put("$*TOLERANCE", "Num");
        VARIABLE_SYMBOLS.put("$*DEFAULT-READ-ELEMS", "Int");
    }

    public Perl6FileImpl(FileViewProvider viewProvider) {
        super(viewProvider, Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Perl6ModuleFileType.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getContainingFile();
    }

    @Override
    public List<Perl6PsiDeclaration> getExports() {
        // If possible, get the result from the stub, to avoid having to
        // build and walk the full PSI tree.
        Stub stub = getStub();
        if (stub instanceof Perl6FileStub)
            return ((Perl6FileStub)stub).getExports();

        // Otherwise, we need to walk the PSI tree.
        return PsiTreeUtil.findChildrenOfType(this, Perl6PsiDeclaration.class).stream()
                          .filter(Perl6PsiDeclaration::isExported)
                          .collect(Collectors.toList());
    }

    @Override
    public void contributeGlobals(Perl6SymbolCollector collector, Set<String> seen) {
        // Walk from the top of the PSI tree to find top-level, our-scoped packages.
        // Contribute those.
        Stub stub = this.getStub();
        if (stub != null) {
            Queue<Stub> visit = new LinkedList<>();
            visit.add(stub);
            while (!visit.isEmpty()) {
                if (collector.isSatisfied())
                    return;
                Stub current = visit.remove();
                boolean addChildren = false;
                if (current == stub) {
                    addChildren = true;
                }
                else if (current instanceof Perl6PackageDeclStub) {
                    Perl6PackageDeclStub nested = (Perl6PackageDeclStub)current;
                    String scope = nested.getScope();
                    if (scope.equals("our") || scope.equals("unit")) {
                        String topName = nested.getTypeName();
                        if (topName != null && !topName.isEmpty()) {
                            Perl6PackageDecl psi = nested.getPsi();
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                psi, topName));
                            if (!collector.isSatisfied())
                                psi.contributeNestedPackagesWithPrefix(collector, topName + "::");
                        }
                    }
                }
                else if (current instanceof Perl6UseStatementStub) {
                    Perl6UseStatementStub use = (Perl6UseStatementStub)current;
                    contributeTransitive(collector, seen, use.getModuleName());
                }
                else if (current instanceof Perl6NeedStatementStub) {
                    Perl6NeedStatementStub need = (Perl6NeedStatementStub)current;
                    for (String name : need.getModuleNames())
                        contributeTransitive(collector, seen, name);
                }
                else {
                    addChildren = true;
                }
                if (addChildren)
                    visit.addAll(current.getChildrenStubs());
            }
        }
        else {
            Queue<Perl6PsiElement> visit = new LinkedList<>();
            visit.add(this);
            while (!visit.isEmpty()) {
                Perl6PsiElement current = visit.remove();
                boolean addChildren = false;
                if (current == this) {
                    addChildren = true;
                }
                else if (current instanceof Perl6PackageDecl) {
                    Perl6PackageDecl nested = (Perl6PackageDecl)current;
                    String scope = nested.getScope();
                    if (scope.equals("our") || scope.equals("unit")) {
                        String topName = nested.getName();
                        if (topName != null && !topName.isEmpty()) {
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                nested, topName));
                            nested.contributeNestedPackagesWithPrefix(collector, topName + "::");
                        }
                    }
                }
                else if (current instanceof Perl6Enum) {
                    Perl6Enum perl6Enum = (Perl6Enum)current;
                    String scope = perl6Enum.getScope();
                    if (scope.equals("our")) {
                        perl6Enum.contributeLexicalSymbols(collector);
                    }
                }
                else if (current instanceof Perl6UseStatement) {
                    Perl6UseStatement use = (Perl6UseStatement)current;
                    contributeTransitive(collector, seen, use.getModuleName());
                }
                else if (current instanceof Perl6NeedStatement) {
                    Perl6NeedStatement need = (Perl6NeedStatement)current;
                    for (String name : need.getModuleNames())
                        contributeTransitive(collector, seen, name);
                }
                else if (!(current instanceof Perl6PsiScope)) {
                    addChildren = true;
                }
                if (addChildren)
                    for (PsiElement e : current.getChildren())
                        if (e instanceof Perl6PsiElement)
                            visit.add((Perl6PsiElement)e);
            }
        }
    }

    private void contributeTransitive(Perl6SymbolCollector collector, Set<String> seen, String name) {
        if (name == null || seen.contains(name))
            return;
        seen.add(name);

        Project project = getProject();
        Collection<Perl6File> found = ProjectModulesStubIndex.getInstance()
                .get(name, project, GlobalSearchScope.projectScope(project));
        if (found.size() > 0) {
            Perl6File file = found.iterator().next();
            file.contributeGlobals(collector, seen);
        }
        else {
            // We only have globals, not exports, transitively available.
            for (Perl6Symbol sym : Perl6SdkType.getInstance().getNamesForNeed(project, name)) {
                collector.offerSymbol(sym);
                if (collector.isSatisfied())
                    return;
            }
        }
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        for (String symbol : VARIABLE_SYMBOLS.keySet()) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, symbol));
            if (collector.isSatisfied())
                return;
        }
        for (Perl6Symbol symbol : Perl6SdkType.getInstance().getCoreSettingSymbols(this)) {
            collector.offerSymbol(symbol);
            if (collector.isSatisfied())
                return;
        }
        PsiElement list = PsiTreeUtil.getChildOfType(this, Perl6StatementList.class);
        if (list == null) return;
        PsiElement finish = PsiTreeUtil.findChildOfType(list, PodBlockFinish.class);
        if (finish != null) {
            Perl6Symbol finishBlock = new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$=finish");
            collector.offerSymbol(finishBlock);
        }
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        String name = getEnclosingPerl6ModuleName();
        if (name == null)
            name = getName();
        String finalName = name;
        return new PsiMetaData() {
            @Override
            public PsiElement getDeclaration() {
                return Perl6FileImpl.this;
            }

            @Override
            public String getName(PsiElement context) {
                return finalName;
            }

            @Override
            public String getName() {
                return finalName;
            }

            @Override
            public void init(PsiElement element) {
            }

            @NotNull
            @Override
            public Object[] getDependences() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }

    /* Builds a map from statement start line number to the line(s) that the statement
     * spans. The start line is always included, but any line numbers where an inner
     * statement starts will be excluded (so the values may be sparse). Any line that
     * does not have an entry is not "interesting" statement (e.g. one that is meaningful
     * in coverage or could be hit by a breakpoint).
     */
    @Override
    public Map<Integer, List<Integer>> getStatementLineMap() {
        Map<Integer, List<Integer>> result = new HashMap<>();
        Set<Integer> covered = new HashSet<>();
        Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread() && application.isWriteAccessAllowed())
            PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        FileViewProvider fileViewProvider = getViewProvider();
        Document document = fileViewProvider.getDocument();
        Perl6StatementList stmts = PsiTreeUtil.getChildOfType(this, Perl6StatementList.class);
        buildStatementLineMap(result, covered, document, stmts);
        return result;
    }

    private void buildStatementLineMap(Map<Integer, List<Integer>> result,
                                       Set<Integer> covered,
                                       Document document,
                                       Perl6StatementList stmts) {
        if (stmts == null)
            return;
        for (Perl6Statement stmt : PsiTreeUtil.getChildrenOfTypeAsList(stmts, Perl6Statement.class)) {
            // Get the start line and, if not seen already add it to the set of
            // covered statements.
            int startLine = document.getLineNumber(stmt.getTextOffset());
            boolean seen = covered.contains(startLine);
            List<Integer> spanned = null;
            if (!seen) {
                if (!isUncoverableDeclarator(stmt)) {
                    covered.add(startLine);
                    if (!isSymbolDeclarator(stmt)) {
                        spanned = new ArrayList<>();
                        result.put(startLine, spanned);
                        spanned.add(startLine);
                    }
                }
            }

            // Visit statement lists enclosed in this file.
            findNestedStatements(result, covered, document, stmt);

            // Now add uncovered lines up to the end of this statement.
            if (!seen) {
                try {
                    int endLine = document.getLineNumber(stmt.getTextOffset() +
                                                         stmt.getText().replaceFirst("\\s+$", "").length() - 1);
                    for (int i = startLine + 1; i <= endLine; i++) {
                        if (!covered.contains(i)) {
                            covered.add(i);
                            if (spanned != null)
                                spanned.add(i);
                        }
                    }
                }
                catch (IndexOutOfBoundsException ignored) {
                    // Code piece was updated in the middle of building statement line map,
                    // so just ignore the exception until next rebuilding
                }
            }
        }
    }

    private boolean isSymbolDeclarator(Perl6Statement stmt) {
        PsiElement scoped = PsiTreeUtil.getChildOfType(stmt, Perl6ScopedDecl.class);
        Perl6PsiElement declChild = PsiTreeUtil.getChildOfAnyType(scoped != null ? scoped : stmt,
                Perl6PackageDecl.class, Perl6UseStatement.class, Perl6NeedStatement.class,
                Perl6Subset.class, Perl6Enum.class, Perl6StubCode.class);
        return declChild != null;
    }

    private boolean isUncoverableDeclarator(Perl6Statement stmt) {
        Perl6ScopedDecl scopedDecl = PsiTreeUtil.getChildOfType(stmt, Perl6ScopedDecl.class);
        Perl6PsiElement consider = scopedDecl == null ? stmt : scopedDecl;

        Perl6PsiElement codeChild = PsiTreeUtil.getChildOfAnyType(consider,
                Perl6RoutineDecl.class, Perl6MultiDecl.class, Perl6RegexDecl.class);
        if (codeChild != null)
            return true;

        Perl6VariableDecl varChild = PsiTreeUtil.getChildOfType(consider, Perl6VariableDecl.class);
        if (varChild != null && !varChild.hasInitializer())
            return true;

        return false;
    }

    private void findNestedStatements(Map<Integer, List<Integer>> result, Set<Integer> covered, Document document, Perl6PsiElement node) {
        for (PsiElement child : node.getChildren()) {
            if (child instanceof Perl6StatementList)
                buildStatementLineMap(result, covered, document, (Perl6StatementList)child);
            else
                findNestedStatements(result, covered, document, (Perl6PsiElement)child);
        }
    }
}
