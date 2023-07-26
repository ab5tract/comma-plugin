package edument.perl6idea.pm;

public enum RakuPackageManagerKind {
    EMPTY, ZEF, PAKKU;

    public String getName() {
        return switch (this) {
            case ZEF -> "zef";
            case PAKKU -> "Pakku";
            default -> "...";
        };
    }
}
