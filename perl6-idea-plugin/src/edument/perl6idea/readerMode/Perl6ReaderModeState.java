package edument.perl6idea.readerMode;

public enum Perl6ReaderModeState {
    CODE, DOCS, SPLIT;

    public String toString() {
        return switch (this) {
            case CODE -> "Code";
            case DOCS -> "Documentation";
            case SPLIT -> "Live Preview";
        };
    }
}
