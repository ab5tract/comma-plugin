package edument.perl6idea.component;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.symbols.Perl6ExternalSymbol;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Perl6NameCache implements ProjectComponent {
    private Map<String, List<Perl6Symbol>> useNameCache = new ConcurrentHashMap<>();
    private Map<String, List<Perl6Symbol>> needNameCache = new ConcurrentHashMap<>();
    private static Logger LOG = Logger.getInstance(Perl6NameCache.class);

    public List<Perl6Symbol> getNamesForUse(Project project, String name) {
        List<Perl6Symbol> cached = useNameCache.get(name);
        if (cached != null)
            return cached;
        return useNameCache.put(name, loadModuleSymbols(project, "use", name));
    }

    public List<Perl6Symbol> getNamesForNeed(Project project, String name) {
        List<Perl6Symbol> cached = needNameCache.get(name);
        if (cached != null)
            return cached;
        return needNameCache.put(name, loadModuleSymbols(project, "need", name));
    }

    private List<Perl6Symbol> loadModuleSymbols(Project project, String directive, String name) {
        String homePath = Perl6SdkType.getSdkHomeByProject(project);
        if (homePath == null) {
            LOG.error(new ExecutionException("SDK path is not set"));
            return new ArrayList<>();
        }
        File moduleSymbols = Perl6CommandLine.getResourceAsFile(this,"symbols/perl6-module-symbols.p6");
        GeneralCommandLine cmd = Perl6CommandLine.getPerl6CommandLine(
                System.getProperty("java.io.tmpdir"),
                homePath);
        cmd.addParameter(moduleSymbols.getPath());
        cmd.addParameter(directive);
        cmd.addParameter(name);

        List<String> symbols = Perl6CommandLine.execute(cmd);
        return symbols == null ? new ArrayList<>() : externalNamesToSymbols(symbols);
    }

    private List<Perl6Symbol> externalNamesToSymbols(List<String> names) {
        return names.stream()
                    .flatMap(this::nameToSymbols)
                    .collect(Collectors.toList());
    }

    private Stream<Perl6Symbol> nameToSymbols(String name) {
        if (name.startsWith("&")) {
            return Stream.of(
                new Perl6ExternalSymbol(Perl6SymbolKind.Variable, name),
                new Perl6ExternalSymbol(Perl6SymbolKind.Routine, name.substring(1))
            );
        }
        else {
            return Stream.of(new Perl6ExternalSymbol(
                Character.isLetter(name.charAt(0))
                ? Perl6SymbolKind.TypeOrConstant
                : Perl6SymbolKind.Variable,
                name));
        }
    }
}
