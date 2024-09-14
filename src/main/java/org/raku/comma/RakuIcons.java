package org.raku.comma;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;

import javax.swing.*;
import java.util.Objects;

public class RakuIcons {
    public static final Icon CAMELIA_13x13 = IconLoader.getIcon("/icons/camelia-13x13.png",
                                                                Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon CAMELIA = IconLoader.getIcon("/icons/camelia.png",
                                                          Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon RUN_WITH_TIMELINE = IconLoader.getIcon("/icons/run_timeline_icon.png",
                                                                    Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon CAMELIA_STOPWATCH = IconLoader.getIcon("/icons/camelia_stopwatch.png",
                                                                    Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon SUB = IconLoader.getIcon("/icons/sub.png",
                                                      Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon METHOD = IconLoader.getIcon("/icons/method.png",
                                                         Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon REGEX = IconLoader.getIcon("/icons/regex.png",
                                                        Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon CONSTANT = IconLoader.getIcon("/icons/constant.png",
                                                           Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon ATTRIBUTE = IconLoader.getIcon("/icons/attribute.png",
                                                            Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));

    public static final Icon MODULE = IconLoader.getIcon("/icons/module.png",
                                                         Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon CLASS = IconLoader.getIcon("/icons/class.png",
                                                        Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon GRAMMAR = IconLoader.getIcon("/icons/grammar.png",
                                                          Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon ROLE = IconLoader.getIcon("/icons/role.png",
                                                       Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon SUBSET = IconLoader.getIcon("/icons/subset.png",
                                                         Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon ENUM = IconLoader.getIcon("/icons/enum.png",
                                                       Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon PACKAGE = IconLoader.getIcon("/icons/package.png",
                                                          Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));

    public static final Icon CRO = IconLoader.getIcon("/icons/cro.png",
                                                      Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));

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
