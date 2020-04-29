package edument.perl6idea.refactoring;

public class NewRegexPartData {
    public Perl6RegexPartType type;
    public String name;
    public String signature;
    public boolean isCapture;
    public boolean isLexical;

    public NewRegexPartData(Perl6RegexPartType type, String name, String signature, boolean isCapture, boolean isLexical) {
        this.type = type;
        this.name = name;
        this.signature = signature;
        this.isCapture = isCapture;
        this.isLexical = isLexical;
    }
}
