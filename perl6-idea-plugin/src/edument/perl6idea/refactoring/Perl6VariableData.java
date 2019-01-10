package edument.perl6idea.refactoring;

public class Perl6VariableData {
    private String name;
    private String type;

    public Perl6VariableData(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getPresentation() {
        String result = "";
        if (!type.isEmpty())
            result += type + " ";
        result += name;
        return result;
    }
}
