package edument.perl6idea.repl;

import com.intellij.execution.console.LanguageConsoleView;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;

import java.util.*;

/**
 * Keeps track of previous executions, so we can do auto-completions based upon
 * them.
 */
public class Perl6ReplState {
    private static class HistoryEntry {
        public final Perl6File file;
        public boolean compiledOk;

        private HistoryEntry(Perl6File file) {
            this.file = file;
        }
    }

    private final Perl6ReplConsole console;
    private final List<HistoryEntry> executionHistory = new ArrayList<>();
    public static final Key<Perl6ReplState> PERL6_REPL_STATE = Key.create("PERL6_REPL_STATE");
    private List<Runnable> newHistoryListeners = new ArrayList<>();
    private Collection<PsiNamedElement> lastRegexVars = null;

    public Perl6ReplState(Perl6ReplConsole console) {
        this.console = console;
    }

    public void addNewHistoryListener(Runnable listener) {
        newHistoryListeners.add(listener);
    }

    /**
     * Adds an executed line. Note that it may not compile, and so we don't yet
     * use it to contribute symbols until we're told it worked out OK.
     */
    public void addExecuted(String code) {
        ReadAction.run(() -> {
            // Obtain the virtual file for the console view.
            Project project = console.getProject();
            LanguageConsoleView view = console.getConsoleView();
            VirtualFile consoleFile = view.getVirtualFile();

            // Form a Perl6File holding the history entry.
            String historyEntryFilename = consoleFile.getName() + executionHistory.size() + ".p6";
            LightVirtualFile file = new LightVirtualFile(historyEntryFilename, Perl6Language.INSTANCE, code);
            file.setCharset(CharsetToolkit.UTF8_CHARSET);
            file.setWritable(false);
            PsiFile psiFile = ((PsiFileFactoryImpl)PsiFileFactory.getInstance(project)).trySetupPsiForFile(
                file, Perl6Language.INSTANCE, true, false);
            if (psiFile instanceof Perl6File)
                executionHistory.add(new HistoryEntry((Perl6File)psiFile));
            Perl6File regexFile = Perl6ElementFactory.createFileFromText(project, code + "; $0;");
            Perl6Variable markerVariable = PsiTreeUtil.findElementOfClassAtOffset(regexFile, code.length() + 2, Perl6Variable.class, false);
            if (markerVariable != null) {
                Collection<PsiNamedElement> regexDrivenVars = Perl6VariableReference.obtainRegexDrivenVars(markerVariable);
                if (regexDrivenVars != null)
                    lastRegexVars = regexDrivenVars;
            }

            // Make sure the REPL state is attached to the console virtual file.
            consoleFile.putUserDataIfAbsent(PERL6_REPL_STATE, this);

            // Fire any new history listeners.
            for (Runnable listener : newHistoryListeners)
                listener.run();
        });
    }

    public void contributeFromHistory(Perl6SymbolCollector collector) {
        for (int i = executionHistory.size() - 1; i >= 0; i--) {
            HistoryEntry entry = executionHistory.get(i);
            if (entry.compiledOk) {
                for (Perl6LexicalSymbolContributor contributor : entry.file.getSymbolContributors()) {
                    contributor.contributeLexicalSymbols(collector);
                }
            }
        }
        if (lastRegexVars != null) {
            for (PsiNamedElement symbol : lastRegexVars) {
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, symbol));
            }
        }
    }

    public void markLatestCompiledOk() {
        executionHistory.get(executionHistory.size() - 1).compiledOk = true;
    }

    public int getHistorySize() {
        return executionHistory.size();
    }

    public CharSequence getExecutionText(int index) {
        return executionHistory.get(index).file.getText();
    }
}
