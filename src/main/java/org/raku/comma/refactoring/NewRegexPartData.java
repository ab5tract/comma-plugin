package org.raku.comma.refactoring;

public class NewRegexPartData {
    public RakuRegexPartType type;
    public RakuRegexPartType parentType;
    public String name;
    public String signature;
    public boolean isCapture;
    public boolean isLexical;

    public NewRegexPartData(RakuRegexPartType type, String name, String signature, boolean isCapture, boolean isLexical,
                            RakuRegexPartType parentType) {
        this.type = type;
        this.name = name;
        this.signature = signature;
        this.isCapture = isCapture;
        this.isLexical = isLexical;
        this.parentType = parentType;
    }
}
