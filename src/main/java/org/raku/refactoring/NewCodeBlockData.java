package org.raku.refactoring;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class NewCodeBlockData {
    public final RakuCodeBlockType type;
    public String scope = "";
    public final String name;
    public String returnType = "";
    public final RakuVariableData[] variables;
    public final boolean isPrivateMethod;
    public boolean containsExpression;
    public boolean wantsSemicolon;

    public NewCodeBlockData(RakuCodeBlockType type, String name, RakuVariableData[] variableData) {
        this.type = type;
        this.name = name;
        this.isPrivateMethod = type == RakuCodeBlockType.PRIVATEMETHOD;
        this.variables = variableData;
    }

    public NewCodeBlockData(RakuCodeBlockType type, String scope,
                            String name, String returnType,
                            RakuVariableData[] variableData) {
        this.type = type;
        this.scope = scope;
        this.name = name;
        this.returnType = returnType;
        this.isPrivateMethod = type == RakuCodeBlockType.PRIVATEMETHOD;
        this.variables = variableData;
    }

    public static String formSignature(@NotNull RakuVariableData[] variables, boolean isCall) {
        StringJoiner vars = new StringJoiner(", ");
        for (RakuVariableData var : variables) {
            if (var.isPassed)
                vars.add(var.getPresentation(isCall));
        }
        return vars.toString();
    }

    public String formSignature(boolean isCall) {
        return formSignature(variables, isCall);
    }
}
