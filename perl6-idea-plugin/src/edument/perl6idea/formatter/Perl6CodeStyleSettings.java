package edument.perl6idea.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;

public class Perl6CodeStyleSettings extends CustomCodeStyleSettings {
    static final String[] BRACE_PLACEMENT_OPTIONS = new String[] {"End of line", "Next line"};
    static final int[] BRACE_PLACEMENT_VALUES = new int[]{ 1, 2 };
    public int PACKAGE_DECL_BRACE_STYLE = 1;
    public int ROUTINE_DECL_BRACE_STYLE = 1;
    public int REGEX_DECL_BRACE_STYLE = 1;
    public int PHASER_BRACE_STYLE = 1;
    public int OTHER_BRACE_STYLE = 1;

    public boolean ROUTINES_DECLARATION_IN_ONE_LINE = true;
    public boolean REGEX_DECLARATION_IN_ONE_LINE = true;
    public boolean PACKAGE_DECLARATION_IN_ONE_LINE = true;
    public boolean POINTY_BLOCK_IN_ONE_LINE = true;

    public Perl6CodeStyleSettings(CodeStyleSettings container) {
        super(Perl6Language.INSTANCE.getID(), container);
    }
}
