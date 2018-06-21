package edument.perl6idea.psi.symbols;

public abstract class Perl6PackageConstrainedSymbolCollector implements Perl6SymbolCollector {
    private int packageDepth = 0;

    public void increasePackageDepth() {
        packageDepth++;
    }

    public boolean areInstanceSymbolsRelevant() {
        return packageDepth == 0;
    }

    protected boolean acceptablyScoped(Perl6Symbol test) {
        return !test.isInstanceScoped() || areInstanceSymbolsRelevant();
    }
}
