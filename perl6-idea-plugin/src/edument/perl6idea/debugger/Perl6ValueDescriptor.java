package edument.perl6idea.debugger;

public class Perl6ValueDescriptor {
    private String name;
    private String type;
    private String value;

    Perl6ValueDescriptor(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isExpandableNode() {
        return type.equals("obj");
    }
}
