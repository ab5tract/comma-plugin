package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.JBFont;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.repl.Perl6ReplState;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Perl6FileImpl extends PsiFileBase implements Perl6File {
    public static final Map<String, Perl6SettingTypeId> VARIABLE_SYMBOLS = new HashMap<>();

    static {
        // compile time variables
        VARIABLE_SYMBOLS.put("$?FILE", Perl6SettingTypeId.Str);
        VARIABLE_SYMBOLS.put("$?LINE", Perl6SettingTypeId.Int);
        VARIABLE_SYMBOLS.put("$?LANG", null);
        VARIABLE_SYMBOLS.put("%?RESOURCES", null);
        VARIABLE_SYMBOLS.put("$?PACKAGE", null);
        // pod vars
        VARIABLE_SYMBOLS.put("$=pod", Perl6SettingTypeId.Array);
        // special variables
        VARIABLE_SYMBOLS.put("$_", null);
        VARIABLE_SYMBOLS.put("$/", Perl6SettingTypeId.Match);
        VARIABLE_SYMBOLS.put("$!", Perl6SettingTypeId.Exception);
        // dynamic variables
        VARIABLE_SYMBOLS.put("$*ARGFILES", null);
        VARIABLE_SYMBOLS.put("@*ARGS", Perl6SettingTypeId.Array);
        VARIABLE_SYMBOLS.put("$*IN", Perl6SettingTypeId.IO__Handle);
        VARIABLE_SYMBOLS.put("$*OUT", Perl6SettingTypeId.IO__Handle);
        VARIABLE_SYMBOLS.put("$*ERR", Perl6SettingTypeId.IO__Handle);
        VARIABLE_SYMBOLS.put("%*ENV", Perl6SettingTypeId.Hash);
        VARIABLE_SYMBOLS.put("$*REPO", null);
        VARIABLE_SYMBOLS.put("$*INIT-DISTANT", Perl6SettingTypeId.Instant);
        VARIABLE_SYMBOLS.put("$*TZ", Perl6SettingTypeId.Int);
        VARIABLE_SYMBOLS.put("$*CWD", Perl6SettingTypeId.IO__Path);
        VARIABLE_SYMBOLS.put("$*KERNEL", Perl6SettingTypeId.Kernel);
        VARIABLE_SYMBOLS.put("$*DISTRO", Perl6SettingTypeId.Distro);
        VARIABLE_SYMBOLS.put("$*VM", Perl6SettingTypeId.VM);
        VARIABLE_SYMBOLS.put("$*PERL", Perl6SettingTypeId.Perl);
        VARIABLE_SYMBOLS.put("$*RAKU", Perl6SettingTypeId.Raku);
        VARIABLE_SYMBOLS.put("$*PID", Perl6SettingTypeId.Int);
        VARIABLE_SYMBOLS.put("$*PROGRAM-NAME", Perl6SettingTypeId.Str);
        VARIABLE_SYMBOLS.put("$*PROGRAM", Perl6SettingTypeId.IO__Path);
        VARIABLE_SYMBOLS.put("&*EXIT", null);
        VARIABLE_SYMBOLS.put("$*EXECUTABLE", Perl6SettingTypeId.IO__Path);
        VARIABLE_SYMBOLS.put("$*EXECUTABLE-NAME", Perl6SettingTypeId.Str);
        VARIABLE_SYMBOLS.put("$*USER", Perl6SettingTypeId.IntStr);
        VARIABLE_SYMBOLS.put("$*GROUP", Perl6SettingTypeId.IntStr);
        VARIABLE_SYMBOLS.put("$*HOMEDRIVE", null);
        VARIABLE_SYMBOLS.put("$*HOMEPATH", null);
        VARIABLE_SYMBOLS.put("$*HOME", Perl6SettingTypeId.IO__Path);
        VARIABLE_SYMBOLS.put("$*SPEC", Perl6SettingTypeId.IO__Spec);
        VARIABLE_SYMBOLS.put("$*TMPDIR", Perl6SettingTypeId.IO__Path);
        VARIABLE_SYMBOLS.put("$*THREAD", Perl6SettingTypeId.Thread);
        VARIABLE_SYMBOLS.put("$*SCHEDULER", Perl6SettingTypeId.ThreadPoolScheduler);
        VARIABLE_SYMBOLS.put("$*SAMPLER", null);
        VARIABLE_SYMBOLS.put("$*COLLATION", Perl6SettingTypeId.Collation);
        VARIABLE_SYMBOLS.put("$*TOLERANCE", Perl6SettingTypeId.Num);
        VARIABLE_SYMBOLS.put("$*DEFAULT-READ-ELEMS", Perl6SettingTypeId.Int);
    }

    public Perl6FileImpl(FileViewProvider viewProvider) {
        super(viewProvider, Perl6Language.INSTANCE);
    }

    @Override
    public boolean isReal() {
        return true;
    }

    @Override
    public String renderPod() {
        // Render all of the blocks, collecting any semantic blocks.
        Perl6StatementList stmts = PsiTreeUtil.getChildOfType(this, Perl6StatementList.class);
        PodRenderingContext context = new PodRenderingContext();
        PodBlock[] blocks = PsiTreeUtil.getChildrenOfType(stmts, PodBlock.class);
        List<String> renderedBlocks = new ArrayList<>();
        if (blocks != null)
            for (PodBlock pod : blocks)
                    renderedBlocks.add(pod.renderPod(context));

        // Prepare HTML header with style sheet.
        StringBuilder builder = new StringBuilder();
        builder.append("<html>\n<head>\n<style>\n");
        builder.append("body { background-color: rgb(");
        builder.append(htmlColor(JBColor.background()));
        builder.append("); color: rgb(");
        builder.append(htmlColor(JBColor.foreground()));
        builder.append("); font-family: \"");
        builder.append(JBFont.label().getFamily());
        builder.append("\", sans-serif; }\n"); // Fallback if font not available in browser
        builder.append("a { color: rgb(");
        builder.append(htmlColor(JBColor.BLUE));
        builder.append("); }\n");
        builder.append("h1, h2, h3, h4, h5, h6 {\n");
        builder.append("  border-bottom: 1px solid rgb(");
        builder.append(htmlColor(JBColor.foreground().darker()));
        builder.append(");}\n");
        builder.append("header > h1, h3 {\n");
        builder.append("  color: rgb(");
        builder.append(htmlColor(JBColor.foreground()));
        builder.append(");\n  border-bottom: none; margin-top: 0; margin-bottom: 0; \n}\n");
        builder.append("header {\n  padding-bottom: 5px; margin-bottom: 10px;\n  border-bottom: 1px solid rgb(");
        builder.append(htmlColor(JBColor.foreground().darker()));
        builder.append(");\n}\n");
        builder.append("</style>\n</head>\n<body>\n");

        // If there is a title or subtitle, use it has a header.
        Map<String, String> semanticBlocks = context.getSemanticBlocks();
        if (semanticBlocks.containsKey("TITLE")) {
            String title = semanticBlocks.get("TITLE");
            builder.append("<header>\n<h1>");
            builder.append(title);
            builder.append("</h1>\n");
            if (semanticBlocks.containsKey("SUBTITLE")) {
                String subtitle = semanticBlocks.get("SUBTITLE");
                builder.append("<h3>");
                builder.append(subtitle);
                builder.append("</h3>\n");
            }
            builder.append("</header>\n");
        }

        // Append rendered non-semantic blocks.
        for (String rendered : renderedBlocks)
            builder.append(rendered);

        // Finish and return HTML.
        builder.append("</body>\n</html>");
        return builder.toString();
    }

    private static String htmlColor(Color color) {
        return String.format("%s, %s, %s", color.getRed(), color.getGreen(), color.getBlue());
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
                else if (current instanceof Perl6VariableDeclStub) {
                    if (((Perl6VariableDeclStub)current).isExported() ||((Perl6VariableDeclStub)current).getScope().equals("our"))
                        ((Perl6VariableDeclStub)current).getPsi().contributeLexicalSymbols(collector);
                }
                else if (current instanceof Perl6PackageDeclStub) {
                    if (((Perl6PackageDeclStub)current).getPackageKind().equals("module"))
                        addChildren = true;
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
                else if (current instanceof Perl6RoutineDeclStub) {
                    if (((Perl6RoutineDeclStub)current).isExported() || ((Perl6RoutineDeclStub)current).getScope().equals("our"))
                        ((Perl6RoutineDeclStub)current).getPsi().contributeLexicalSymbols(collector);
                }
                else if (current instanceof Perl6EnumStub) {
                    if (((Perl6EnumStub)current).isExported() || ((Perl6EnumStub)current).getScope().equals("our"))
                        ((Perl6EnumStub)current).getPsi().contributeLexicalSymbols(collector);
                }
                else if (current instanceof Perl6SubsetStub) {
                    if (((Perl6SubsetStub)current).isExported() || ((Perl6SubsetStub)current).getScope().equals("our"))
                        collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, ((Perl6SubsetStub)current).getPsi()));
                }
                else if (current instanceof Perl6UseStatementStub) {
                    Perl6UseStatementStub use = (Perl6UseStatementStub)current;
                    contributeTransitive(collector, seen, "use", use.getModuleName());
                }
                else if (current instanceof Perl6NeedStatementStub) {
                    Perl6NeedStatementStub need = (Perl6NeedStatementStub)current;
                    for (String name : need.getModuleNames())
                        contributeTransitive(collector, seen, "need", name);
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
                if (collector.isSatisfied())
                    return;
                Perl6PsiElement current = visit.remove();
                boolean addChildren = false;
                if (current == this) {
                    addChildren = true;
                }
                else if (current instanceof Perl6VariableDecl) {
                    if (((Perl6VariableDecl)current).isExported() || ((Perl6VariableDecl)current).getScope().equals("our"))
                        ((Perl6VariableDecl)current).contributeLexicalSymbols(collector);
                }
                else if (current instanceof Perl6PackageDecl) {
                    if (((Perl6PackageDecl)current).getPackageKind().equals("module"))
                        addChildren = true;
                    Perl6PackageDecl nested = (Perl6PackageDecl)current;
                    String scope = nested.getScope();
                    if (scope.equals("our") || scope.equals("unit")) {
                        String topName = nested.getName();
                        if (topName != null && !topName.isEmpty()) {
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                nested, topName));
                            if (!collector.isSatisfied())
                                nested.contributeNestedPackagesWithPrefix(collector, topName + "::");
                        }
                    }
                }
                else if (current instanceof Perl6RoutineDecl) {
                    if (((Perl6RoutineDecl)current).isExported() || ((Perl6RoutineDecl)current).getScope().equals("our"))
                        ((Perl6RoutineDecl)current).contributeLexicalSymbols(collector);
                }
                else if (current instanceof Perl6Enum) {
                    Perl6Enum perl6Enum = (Perl6Enum)current;
                    String scope = perl6Enum.getScope();
                    if (scope.equals("our")) {
                        perl6Enum.contributeLexicalSymbols(collector);
                    }
                }
                else if (current instanceof Perl6Subset) {
                    if (((Perl6Subset)current).isExported() || ((Perl6Subset)current).getScope().equals("our"))
                        collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, (PsiNamedElement)current));
                }
                else if (current instanceof Perl6UseStatement) {
                    Perl6UseStatement use = (Perl6UseStatement)current;
                    contributeTransitive(collector, seen, "use", use.getModuleName());
                }
                else if (current instanceof Perl6NeedStatement) {
                    Perl6NeedStatement need = (Perl6NeedStatement)current;
                    for (String name : need.getModuleNames())
                        contributeTransitive(collector, seen, "need", name);
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

    private void contributeTransitive(Perl6SymbolCollector collector, Set<String> seen, String directive, String name) {
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
            Perl6File needFile = Perl6SdkType.getInstance().getPsiFileForModule(project, name,directive + " " + name);
            needFile.contributeGlobals(collector, new HashSet<>());
        }
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
       for (String symbol : VARIABLE_SYMBOLS.keySet()) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, symbol));
            if (collector.isSatisfied())
                return;
        }
        Perl6SdkType.getInstance().getCoreSettingFile(getProject()).contributeGlobals(collector, new HashSet<>());
        if (collector.isSatisfied())
            return;
        PsiElement list = PsiTreeUtil.getChildOfType(this, Perl6StatementList.class);
        if (list != null) {
            PsiElement finish = PsiTreeUtil.findChildOfType(list, PodBlockFinish.class);
            if (finish != null) {
                Perl6Symbol finishBlock = new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$=finish");
                collector.offerSymbol(finishBlock);
            }
        }

        VirtualFile virtualFile = getOriginalFile().getVirtualFile();
        if (virtualFile != null) {
            Perl6ReplState replState = virtualFile.getUserData(Perl6ReplState.PERL6_REPL_STATE);
            if (replState != null)
                replState.contributeFromHistory(collector);
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
            public Object[] getDependencies() {
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

    private static boolean isSymbolDeclarator(Perl6Statement stmt) {
        PsiElement scoped = PsiTreeUtil.getChildOfType(stmt, Perl6ScopedDecl.class);
        Perl6PsiElement declChild = PsiTreeUtil.getChildOfAnyType(scoped != null ? scoped : stmt,
                Perl6PackageDecl.class, Perl6UseStatement.class, Perl6NeedStatement.class,
                Perl6Subset.class, Perl6Enum.class, Perl6StubCode.class);
        return declChild != null;
    }

    private static boolean isUncoverableDeclarator(Perl6Statement stmt) {
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
