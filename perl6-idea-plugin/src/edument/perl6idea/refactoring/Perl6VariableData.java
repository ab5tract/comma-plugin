package edument.perl6idea.refactoring;

public class Perl6VariableData {
    public boolean isUsed;
    public String name;
    public String type;

    public Perl6VariableData(String name, String type) {
        this.name = name;
        this.type = type;
        this.isUsed = true;
    }

    public String getPresentation(boolean isCall) {
        if (isCall) return name;
        return type.isEmpty() ? name : type + " " + name;
    }
}
