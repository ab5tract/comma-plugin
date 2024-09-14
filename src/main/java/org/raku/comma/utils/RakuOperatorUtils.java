package org.raku.comma.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.raku.comma.formatter.RakuCodeStyleSettings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RakuOperatorUtils {
    public static final List<Character> unicodeOperators = new ArrayList<>();
    public static final List<String> asciiOperators = new ArrayList<>();

    static {
        unicodeOperators.add('∈'); asciiOperators.add("(elem)");
        unicodeOperators.add('∋'); asciiOperators.add("(cont)");
        unicodeOperators.add('∖'); asciiOperators.add("(-)");
        unicodeOperators.add('∘'); asciiOperators.add("o");
        unicodeOperators.add('∩'); asciiOperators.add("(&)");
        unicodeOperators.add('∪'); asciiOperators.add("(|)");
        unicodeOperators.add('≅'); asciiOperators.add("=~=");
        unicodeOperators.add('⊂'); asciiOperators.add("(<)");
        unicodeOperators.add('⊃'); asciiOperators.add("(>)");
        unicodeOperators.add('⊆'); asciiOperators.add("(<=)");
        unicodeOperators.add('⊇'); asciiOperators.add("(>=)");
        unicodeOperators.add('⊍'); asciiOperators.add("(.)");
        unicodeOperators.add('⊎'); asciiOperators.add("(+)");
        unicodeOperators.add('⊖'); asciiOperators.add("(^)");
        unicodeOperators.add('∉'); asciiOperators.add("!(elem)");
        unicodeOperators.add('∌'); asciiOperators.add("!(cont)");
        unicodeOperators.add('⊄'); asciiOperators.add("!(<)");
        unicodeOperators.add('⊅'); asciiOperators.add("!(>)");
        unicodeOperators.add('⊈'); asciiOperators.add("!(<=)");
        unicodeOperators.add('⊉'); asciiOperators.add("!(>=)");
        unicodeOperators.add('≡'); asciiOperators.add("(==)");
        unicodeOperators.add('≢'); asciiOperators.add("!(==)");
        unicodeOperators.add('»'); asciiOperators.add(">>");
        unicodeOperators.add('«'); asciiOperators.add("<<");
    }

    public static boolean isUnicodeConversionEnabled(@NotNull Editor editor) {
        CodeStyleSettingsManager styleManager = CodeStyleSettingsManager.getInstance(editor.getProject());
        if (styleManager != null) {
            CodeStyleSettings style = styleManager.getMainProjectCodeStyle();
            return style != null && style.getCustomSettings(RakuCodeStyleSettings.class).CONVERT_TO_UNICODE;
        }
        return false;
    }
}
