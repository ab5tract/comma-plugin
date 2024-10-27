package org.raku.comma.repl;

import com.intellij.execution.console.LanguageConsoleView;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import org.raku.comma.RakuLanguage;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.RakuVariableReference;
import org.raku.comma.psi.symbols.RakuExplicitSymbol;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Keeps track of previous executions, so we can do auto-completions based upon
 * them.
 */
public class RakuReplState {
    private static class HistoryEntry {
        public final RakuFile file;
        public boolean compiledOk;

        private HistoryEntry(RakuFile file) {
            this.file = file;
        }
    }

    private final RakuReplConsole console;
    private final List<HistoryEntry> executionHistory = new ArrayList<>();
    public static final Key<RakuReplState> RAKU_REPL_STATE = Key.create("RAKU_REPL_STATE");
    private final List<Runnable> newHistoryListeners = new ArrayList<>();
    private Collection<PsiNamedElement> lastRegexVars = null;

    public RakuReplState(RakuReplConsole console) {
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

            // Form a RakuFile holding the history entry.
            String historyEntryFilename = consoleFile.getName() + executionHistory.size() + ".p6";
            LightVirtualFile file = new LightVirtualFile(historyEntryFilename, RakuLanguage.INSTANCE, code);
            file.setCharset(StandardCharsets.UTF_8);
            file.setWritable(false);
            PsiFile psiFile = ((PsiFileFactoryImpl)PsiFileFactory.getInstance(project)).trySetupPsiForFile(
              file, RakuLanguage.INSTANCE, true, false);
            if (psiFile instanceof RakuFile)
                executionHistory.add(new HistoryEntry((RakuFile)psiFile));
            RakuFile regexFile = RakuElementFactory.createFileFromText(project, code + "; $0;");
            RakuVariable markerVariable = PsiTreeUtil.findElementOfClassAtOffset(regexFile, code.length() + 2, RakuVariable.class, false);
            if (markerVariable != null) {
                Collection<PsiNamedElement> regexDrivenVars = RakuVariableReference.obtainRegexDrivenVars(markerVariable);
                if (regexDrivenVars != null)
                    lastRegexVars = regexDrivenVars;
            }

            // Make sure the REPL state is attached to the console virtual file.
            consoleFile.putUserDataIfAbsent(RAKU_REPL_STATE, this);

            // Fire any new history listeners.
            for (Runnable listener : newHistoryListeners)
                listener.run();
        });
    }

    public void contributeFromHistory(RakuSymbolCollector collector) {
        for (int i = executionHistory.size() - 1; i >= 0; i--) {
            HistoryEntry entry = executionHistory.get(i);
            if (entry.compiledOk) {
                for (RakuLexicalSymbolContributor contributor : entry.file.getSymbolContributors()) {
                    contributor.contributeLexicalSymbols(collector);
                }
            }
        }
        if (lastRegexVars != null) {
            for (PsiNamedElement symbol : lastRegexVars) {
                collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Variable, symbol));
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
