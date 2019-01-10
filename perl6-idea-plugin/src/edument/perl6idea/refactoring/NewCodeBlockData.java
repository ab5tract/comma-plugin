package edument.perl6idea.refactoring;

public class NewCodeBlockData {
    public Perl6CodeBlockType type;
    public String scope = "";
    public String name;
    public String returnType = "";
    public final Perl6VariableData[] variables;
    public boolean isPrivateMethod;

    public NewCodeBlockData(Perl6CodeBlockType type, String name, Perl6VariableData[] variableData) {
        this.type = type;
        this.name = name;
        this.isPrivateMethod = type == Perl6CodeBlockType.PRIVATEMETHOD;
        this.variables = variableData;
    }

    public NewCodeBlockData(Perl6CodeBlockType type, String scope,
                            String name, String returnType,
                            Perl6VariableData[] variableData) {
        this.type = type;
        this.scope = scope;
        this.name = name;
        this.returnType = returnType;
        this.isPrivateMethod = type == Perl6CodeBlockType.PRIVATEMETHOD;
        this.variables = variableData;
    }
}
