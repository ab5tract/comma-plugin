package edument.perl6idea.refactoring.introduce;

import edument.perl6idea.refactoring.RakuNameValidator;

public class IntroduceValidator implements RakuNameValidator {
    public boolean isNameValid(String name) {
        return name != null && isIdentifier(name);
    }

    private static boolean isIdentifier(String name) {
        return !name.endsWith("-") && !name.startsWith("-") && !(Character.isDigit(name.charAt(0))) &&
               name.startsWith("$") || name.startsWith("@") || name.startsWith("%") || name.startsWith("&") ||
               name.startsWith("\\");
    }
}
