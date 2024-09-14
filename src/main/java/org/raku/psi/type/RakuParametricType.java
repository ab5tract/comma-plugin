package org.raku.psi.type;

import java.util.Objects;

public class RakuParametricType implements RakuType {
    private final RakuType genericType;
    private final RakuType[] arguments;

    public RakuParametricType(RakuType genericType, RakuType[] arguments) {
        this.genericType = genericType;
        this.arguments = arguments;
    }

    public RakuType getGenericType() {
        return genericType;
    }

    public RakuType[] getArguments() {
        return arguments;
    }

    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        builder.append(genericType.getName());
        builder.append('[');
        boolean first = true;
        for (RakuType argument : arguments) {
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
        if (!(other instanceof RakuParametricType otherType))
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
    public RakuType dispatchType() {
        return genericType.dispatchType();
    }
}
