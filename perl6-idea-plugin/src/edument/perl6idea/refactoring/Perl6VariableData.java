package edument.perl6idea.refactoring;

public class Perl6VariableData {
    public boolean isPassed;
    public String name;
    public String type;
    public boolean lexicallyAvailable;

    public Perl6VariableData(String name, String type, boolean lexicallyAvailable, boolean isPassed) {
        this.name = name;
        this.type = type;
        this.lexicallyAvailable = lexicallyAvailable;
        this.isPassed = isPassed;
    }

    public String getPresentation(boolean isCall) {
        if (isCall) return name;
        return type.isEmpty() ? name : type + " " + name;
    }

    public String getLexicalState() {
        return lexicallyAvailable ? "YES" : "NO";
    }
}
