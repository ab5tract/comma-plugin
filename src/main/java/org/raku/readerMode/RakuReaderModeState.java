package org.raku.readerMode;

public enum RakuReaderModeState {
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
