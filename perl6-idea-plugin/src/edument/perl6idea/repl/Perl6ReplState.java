package edument.perl6idea.repl;

import com.intellij.execution.console.LanguageConsoleView;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;

import java.util.ArrayList;
import java.util.List;

/** Keeps track of previous executions, so we can do auto-completions based upon
 * them. */
public class Perl6ReplState {
    private final Perl6ReplConsole console;
    private final List<Perl6File> executionHistory = new ArrayList<>();
    public static final Key<Perl6ReplState> PERL6_REPL_STATE = Key.create("PERL6_REPL_STATE");

    public Perl6ReplState(Perl6ReplConsole console) {
        this.console = console;
    }

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
                executionHistory.add((Perl6File)psiFile);

            // Make sure the REPL state is attached to the console virtual file.
            consoleFile.putUserDataIfAbsent(PERL6_REPL_STATE, this);
        });
    }

    public void contributeFromHistory(Perl6SymbolCollector collector) {
        for (int i = executionHistory.size() - 1; i >= 0; i--) {
            for (Perl6LexicalSymbolContributor contributor : executionHistory.get(i).getSymbolContributors()) {
                contributor.contributeLexicalSymbols(collector);
            }
        }
    }
}
