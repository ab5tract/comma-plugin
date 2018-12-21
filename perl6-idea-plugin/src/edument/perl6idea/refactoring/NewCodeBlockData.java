package edument.perl6idea.refactoring;

import com.intellij.util.ArrayUtil;

public class NewCodeBlockData {
    public Perl6CodeBlockType type;
    public String scope = "";
    public String name;
    public String returnType = "";
    public String[] signatureParts = ArrayUtil.EMPTY_STRING_ARRAY;
    public boolean isPrivateMethod;

    public NewCodeBlockData(Perl6CodeBlockType type, String name) {
        this.type = type;
        this.name = name;
        this.isPrivateMethod = type == Perl6CodeBlockType.PRIVATEMETHOD;
    }

    public NewCodeBlockData(Perl6CodeBlockType type, String scope,
                            String name, String returnType,
                            String[] signatureParts) {
        this.type = type;
        this.scope = scope;
        this.name = name;
        this.returnType = returnType;
        this.signatureParts = signatureParts;
        this.isPrivateMethod = type == Perl6CodeBlockType.PRIVATEMETHOD;
    }
}
