package org.raku.utils;

import org.raku.RakuIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class RakuConstants {
    public static final Map<String, Icon> PACKAGE_TYPES = new HashMap<>();
    static {
        PACKAGE_TYPES.put("class", RakuIcons.PACKAGE);
        PACKAGE_TYPES.put("role", RakuIcons.PACKAGE);
        PACKAGE_TYPES.put("grammar", RakuIcons.PACKAGE);
        PACKAGE_TYPES.put("module", RakuIcons.PACKAGE);
        PACKAGE_TYPES.put("package", RakuIcons.PACKAGE);
        PACKAGE_TYPES.put("monitor", RakuIcons.PACKAGE);
    }
}
