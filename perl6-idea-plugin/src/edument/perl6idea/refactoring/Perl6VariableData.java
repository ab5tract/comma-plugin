package edument.perl6idea.refactoring;

public class Perl6VariableData {
    public boolean isPassed;
    public String originalName;
    public String parameterName;
    public String type;
    public boolean lexicallyAvailable;

    public Perl6VariableData(String originalName, String parameterName, String type, boolean lexicallyAvailable, boolean isPassed) {
        this.originalName = originalName;
        this.parameterName = parameterName;
        this.type = type;
        this.lexicallyAvailable = lexicallyAvailable;
        this.isPassed = isPassed;
    }

    public Perl6VariableData(String originalName, String type, boolean lexicallyAvailable, boolean isPassed) {
        this.originalName = originalName;
        this.parameterName = originalName;
        this.type = type;
        this.lexicallyAvailable = lexicallyAvailable;
        this.isPassed = isPassed;
    }

    public String getPresentation(boolean isCall) {
        if (isCall) return originalName;
        return type.isEmpty() ? parameterName : type + " " + parameterName;
    }

    public String getLexicalState() {
        return lexicallyAvailable ? "YES" : "NO";
    }
}
