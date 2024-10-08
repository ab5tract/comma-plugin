package org.raku.comma.readerMode;

public enum RakuReaderModeState {
    CODE, DOCS, SPLIT;

    public String toString() {
        return switch (this) {
            case CODE  -> "Code";
            case DOCS  -> "Documentation";
            case SPLIT -> "Live Preview";
        };
    }
}
