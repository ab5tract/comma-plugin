package edument.perl6idea.psi.type;

import java.util.Objects;

public class Perl6ParametricType implements Perl6Type {
    private final Perl6Type genericType;
    private final Perl6Type[] arguments;

    public Perl6ParametricType(Perl6Type genericType, Perl6Type[] arguments) {
        this.genericType = genericType;
        this.arguments = arguments;
    }

    public Perl6Type getGenericType() {
        return genericType;
    }

    public Perl6Type[] getArguments() {
        return arguments;
    }

    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        builder.append(genericType.getName());
        builder.append('[');
        boolean first = true;
        for (Perl6Type argument : arguments) {
            if (first)
                first = false;
            else
                builder.append(", ");
            if (argument == null)
                builder.append('?');
            else
                builder.append(argument.getName());
        }
        builder.append(']');
        return builder.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Perl6ParametricType otherType))
            return false;
        if (!otherType.genericType.equals(genericType))
            return false;
        if (otherType.arguments.length != arguments.length)
            return false;
        for (int i = 0; i < arguments.length; i++)
            if (!Objects.equals(arguments[i], otherType.arguments[i]))
                return false;
        return true;
    }

    @Override
    public Perl6Type dispatchType() {
        return genericType.dispatchType();
    }
}
