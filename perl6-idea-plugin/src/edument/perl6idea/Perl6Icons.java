package edument.perl6idea;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Perl6Icons {
    public static final Icon CAMELIA_13x13 = IconLoader.getIcon("/icons/camelia-13x13.png");
    public static final Icon CAMELIA = IconLoader.getIcon("/icons/camelia.png");
    public static final Icon RUN_WITH_TIMELINE = IconLoader.getIcon("/icons/run_timeline_icon.png");
    public static final Icon CAMELIA_STOPWATCH = IconLoader.getIcon("/icons/camelia_stopwatch.png");
    public static final Icon SUB = IconLoader.getIcon("/icons/sub.png");
    public static final Icon METHOD = IconLoader.getIcon("/icons/method.png");
    public static final Icon REGEX = IconLoader.getIcon("/icons/regex.png");
    public static final Icon CONSTANT = IconLoader.getIcon("/icons/constant.png");
    public static final Icon ATTRIBUTE = IconLoader.getIcon("/icons/attribute.png");

    public static final Icon MODULE = IconLoader.getIcon("/icons/module.png");
    public static final Icon CLASS = IconLoader.getIcon("/icons/class.png");
    public static final Icon GRAMMAR = IconLoader.getIcon("/icons/grammar.png");
    public static final Icon ROLE = IconLoader.getIcon("/icons/role.png");
    public static final Icon SUBSET = IconLoader.getIcon("/icons/subset.png");
    public static final Icon ENUM = IconLoader.getIcon("/icons/enum.png");
    public static final Icon PACKAGE = IconLoader.getIcon("/icons/package.png");

    public static final Icon CRO = IconLoader.getIcon("/icons/cro.png");

    public static Icon iconForPackageDeclarator(String declarator) {
        switch (declarator) {
            case "module":
                return MODULE;
            case "class":
            case "monitor":
                return CLASS;
            case "role":
                return ROLE;
            case "grammar":
                return GRAMMAR;
            default:
                return PACKAGE;
        }
    }
}
