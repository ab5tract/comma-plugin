package edument.perl6idea.sdk;

import edument.perl6idea.psi.symbols.*;

import java.util.*;
import java.util.stream.Collectors;

public class Perl6ExternalNamesParser {
    private final List<String> names;
    private List<Perl6Symbol> result = new ArrayList<>();
    private Perl6ExternalPackage currentPackage = null;
    private Set<String> seen = new HashSet<>();
    private Map<String, Perl6ExternalPackage> externalClasses = new HashMap<>();

    public Perl6ExternalNamesParser(List<String> names) {
        this.names = names;
        parseNames();
    }

    private void parseNames() {
        for (String line : names) {
            if (line.startsWith("D:") || line.startsWith("V:") ||
                line.startsWith("E:") || line.startsWith("S:")) {
                finishCurrentPackage();
                line = line.substring(2);
                if (line.startsWith("&")) processSub(line);
                else processNonSub(line);
            } else if (line.startsWith("R:") || line.startsWith("C:")) {
                finishCurrentPackage();
                result.add(new Perl6ExternalSymbol(Perl6SymbolKind.TypeOrConstant, line.substring(2)));
                currentPackage = new Perl6ExternalPackage(
                        line.substring(2),
                        line.charAt(0) == 'R'
                        ? Perl6PackageKind.ROLE
                        : Perl6PackageKind.CLASS);
            } else {
                if (line.startsWith("!")) {
                    currentPackage.addPrivateMethod(line);
                } else if (line.startsWith("$") || line.startsWith("@") ||
                           line.startsWith("&") || line.startsWith("%")) {
                    currentPackage.addAttribute(line);
                } else {
                    currentPackage.addMethod(line);
                }
            }
        }
    }

    private void finishCurrentPackage() {
        if (currentPackage == null) return;
        if (!seen.contains(currentPackage.getName())) {
            result.add(currentPackage);
            externalClasses.put(currentPackage.getName(), currentPackage);
            seen.add(currentPackage.getName());
        }
        currentPackage = null;
    }

    private void processNonSub(String name) {
        result.add(new Perl6ExternalSymbol(
                Character.isLetter(name.charAt(0))
                ? Perl6SymbolKind.TypeOrConstant
                : Perl6SymbolKind.Variable, name
        ));
    }

    private void processSub(String name) {
        result.add(new Perl6ExternalSymbol(Perl6SymbolKind.Variable, name));
        result.add(new Perl6ExternalSymbol(Perl6SymbolKind.Routine, name.substring(1)));
    }

    public List<Perl6Symbol> result() {
        return result;
    }

    public Map<String, Perl6ExternalPackage> getExternal() {
        return externalClasses;
    }
}
