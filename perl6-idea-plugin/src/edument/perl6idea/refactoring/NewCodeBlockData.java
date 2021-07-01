package edument.perl6idea.refactoring;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class NewCodeBlockData {
    public final Perl6CodeBlockType type;
    public String scope = "";
    public final String name;
    public String returnType = "";
    public final Perl6VariableData[] variables;
    public final boolean isPrivateMethod;
    public boolean containsExpression;
    public boolean wantsSemicolon;

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

    public static String formSignature(@NotNull Perl6VariableData[] variables, boolean isCall) {
        StringJoiner vars = new StringJoiner(", ");
        for (Perl6VariableData var : variables) {
            if (var.isPassed)
                vars.add(var.getPresentation(isCall));
        }
        return vars.toString();
    }

    public String formSignature(boolean isCall) {
        return formSignature(variables, isCall);
    }
}
