package org.raku.comma.refactoring;

public class RakuVariableData {
    public boolean isPassed;
    public String originalName;
    public String parameterName;
    public String type;
    public boolean lexicallyAvailable;

    public RakuVariableData(String originalName, String parameterName, String type, boolean lexicallyAvailable, boolean isPassed) {
        this.originalName = originalName;
        this.parameterName = parameterName;
        this.type = type;
        this.lexicallyAvailable = lexicallyAvailable;
        this.isPassed = isPassed;
    }

    public RakuVariableData(String originalName, String type, boolean lexicallyAvailable, boolean isPassed) {
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
