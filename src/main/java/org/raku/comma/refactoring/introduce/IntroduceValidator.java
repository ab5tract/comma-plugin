package org.raku.comma.refactoring.introduce;

import org.raku.comma.refactoring.RakuNameValidator;

public class IntroduceValidator implements RakuNameValidator {
    @Override
    public boolean isNameValid(String name) {
        return name != null && isIdentifier(name);
    }

    private static boolean isIdentifier(String name) {
        return !name.endsWith("-") && !name.startsWith("-") && !(Character.isDigit(name.charAt(0))) &&
               name.startsWith("$") || name.startsWith("@") || name.startsWith("%") || name.startsWith("&") ||
               name.startsWith("\\");
    }
}
