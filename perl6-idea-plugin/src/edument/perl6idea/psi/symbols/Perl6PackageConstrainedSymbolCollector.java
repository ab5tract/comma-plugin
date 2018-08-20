package edument.perl6idea.psi.symbols;

public abstract class Perl6PackageConstrainedSymbolCollector implements Perl6SymbolCollector {
    private int packageDepth = 0;
    private boolean areInternalPartsCollected = true;
    private int inheritanceNesting = 0;
    private boolean classInheritanceBarrier = true;

    public void increasePackageDepth() {
        packageDepth++;
    }

    public boolean areInstanceSymbolsRelevant() {
        return packageDepth == 0;
    }

    protected boolean acceptablyScoped(Perl6Symbol test) {
        return !test.isInstanceScoped() || areInstanceSymbolsRelevant();
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

    @Override
    public boolean getClassInheritanceBarrier() {
        return classInheritanceBarrier;
    }

    @Override
    public void setClassInheritanceBarrier(boolean classInheritanceBarrier) {
        this.classInheritanceBarrier = classInheritanceBarrier;
    }
}
