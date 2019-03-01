package edument.perl6idea.refactoring.introduce;

public class IntroduceValidator {
    public static boolean isNameValid(String name) {
        return name != null && isIdentifier(name);
    }

    private static boolean isIdentifier(String name) {
        return !name.endsWith("-") && !name.startsWith("-") && !(Character.isDigit(name.charAt(0))) &&
               name.startsWith("$") || name.startsWith("@") || name.startsWith("%") || name.startsWith("&") ||
               name.startsWith("\\");
    }
}
