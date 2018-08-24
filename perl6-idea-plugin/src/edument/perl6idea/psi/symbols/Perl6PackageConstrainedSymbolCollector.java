package edument.perl6idea.psi.symbols;

public abstract class Perl6PackageConstrainedSymbolCollector implements Perl6SymbolCollector {
    private int packageDepth = 0;
    private boolean areInternalPartsCollected = true;
    private String enclosingPackageKind = null;
    private String enclosingPackageName = null;
    private int inheritanceNesting = 0;

    public void increasePackageDepth() {
        packageDepth++;
    }

    public boolean areInstanceSymbolsRelevant() {
        return packageDepth == 0;
    }

    protected boolean acceptablyScoped(Perl6Symbol test) {
        return !test.isInstanceScoped() || areInstanceSymbolsRelevant();
    }

    public String enclosingPackageKind() {
        return enclosingPackageKind;
    }
    public void setEnclosingPackageKind(String kind) {
        enclosingPackageKind = kind;
    }

    public String enclosingPackageName() {
        return enclosingPackageName;
    }
    public void setEnclosingPackageName(String name) {
        enclosingPackageName = name;
    }

    public void setAreInternalPartsCollected(boolean flag) {
        areInternalPartsCollected = flag;
    }
    public boolean areInternalPartsCollected() {
        return areInternalPartsCollected;
    }

    public void setNestingLevel(int level) {
        inheritanceNesting = level;
    }

    public int getNestingLevel() {
        return inheritanceNesting;
    }
}
