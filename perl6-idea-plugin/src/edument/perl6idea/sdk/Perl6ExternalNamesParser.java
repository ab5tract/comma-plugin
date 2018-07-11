package edument.perl6idea.sdk;

import edument.perl6idea.psi.symbols.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Perl6ExternalNamesParser {
    private final List<String> names;
    private List<Perl6Symbol> result = new ArrayList<>();
    private Perl6ExternalPackage currentPackage;

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
        if (currentPackage != null)
            result.add(currentPackage);
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
}
