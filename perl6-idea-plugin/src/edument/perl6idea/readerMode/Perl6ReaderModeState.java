package edument.perl6idea.readerMode;

public enum Perl6ReaderModeState {
    CODE, DOCS, SPLIT;

    public String toString() {
        switch (this) {
            case CODE:
                return "Code";
            case DOCS:
                return "Documentation";
            case SPLIT:
                return "Live Preview";
        }
        return "";
    }
}
