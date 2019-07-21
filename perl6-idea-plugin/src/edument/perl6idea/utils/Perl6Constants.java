package edument.perl6idea.utils;

import edument.perl6idea.Perl6Icons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Perl6Constants {
    public static final Map<String, Icon> PACKAGE_TYPES = new HashMap<>();
    static {
        PACKAGE_TYPES.put("class", Perl6Icons.PACKAGE);
        PACKAGE_TYPES.put("role", Perl6Icons.PACKAGE);
        PACKAGE_TYPES.put("grammar", Perl6Icons.PACKAGE);
        PACKAGE_TYPES.put("module", Perl6Icons.PACKAGE);
        PACKAGE_TYPES.put("package", Perl6Icons.PACKAGE);
        PACKAGE_TYPES.put("monitor", Perl6Icons.PACKAGE);
    }
}
