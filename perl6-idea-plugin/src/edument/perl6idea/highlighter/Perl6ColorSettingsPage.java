package edument.perl6idea.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class Perl6ColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Bad Syntax", Perl6Highlighter.BAD_CHARACTER),
            new AttributesDescriptor("Type Name", Perl6Highlighter.TYPE_NAME),
            new AttributesDescriptor("Statement Terminator", Perl6Highlighter.STATEMENT_TERMINATOR),
            new AttributesDescriptor("Statement Control", Perl6Highlighter.STATEMENT_CONTROL),
            new AttributesDescriptor("Phaser", Perl6Highlighter.PHASER),
            new AttributesDescriptor("Label Name", Perl6Highlighter.LABEL_NAME),
            new AttributesDescriptor("Label Colon", Perl6Highlighter.LABEL_COLON),
            new AttributesDescriptor("Statement Prefix", Perl6Highlighter.STATEMENT_PREFIX),
            new AttributesDescriptor("Statement Modifier", Perl6Highlighter.STATEMENT_MOD),
            new AttributesDescriptor("Scope Keyword", Perl6Highlighter.SCOPE_DECLARATOR),
            new AttributesDescriptor("Multi Keyword", Perl6Highlighter.MULTI_DECLARATOR),
            new AttributesDescriptor("Routine Keyword", Perl6Highlighter.ROUTINE_DECLARATOR),
            new AttributesDescriptor("Package Keyword", Perl6Highlighter.PACKAGE_DECLARATOR),
            new AttributesDescriptor("Routine Name", Perl6Highlighter.ROUTINE_NAME),
            new AttributesDescriptor("Prefix Operator", Perl6Highlighter.PREFIX),
            new AttributesDescriptor("Infix Operator", Perl6Highlighter.INFIX),
            new AttributesDescriptor("Postfix Operator", Perl6Highlighter.POSTFIX),
            new AttributesDescriptor("Meta-operator", Perl6Highlighter.METAOP),
            new AttributesDescriptor("Array Indexer", Perl6Highlighter.ARRAY_INDEXER),
            new AttributesDescriptor("Hash Indexer", Perl6Highlighter.HASH_INDEXER),
            new AttributesDescriptor("Lambda (-> and <->)", Perl6Highlighter.LAMBDA),
            new AttributesDescriptor("Variable", Perl6Highlighter.VARIABLE),
            new AttributesDescriptor("Contextualizer", Perl6Highlighter.CONTEXTUALIZER),
            new AttributesDescriptor("Variable Shape Declaration", Perl6Highlighter.SHAPE_DECLARATION),
            new AttributesDescriptor("Type Declarator (enum, subset, constant)", Perl6Highlighter.TYPE_DECLARATOR),
            new AttributesDescriptor("Term Declaration Backslash (my \\answer = 42)", Perl6Highlighter.TERM_DECLARATION_BACKSLASH),
            new AttributesDescriptor("Numeric Literal", Perl6Highlighter.NUMERIC_LITERAL),
            new AttributesDescriptor("Version Literal", Perl6Highlighter.VERSION),
            new AttributesDescriptor("Comment", Perl6Highlighter.COMMENT),
            new AttributesDescriptor("Sub Call Name", Perl6Highlighter.SUB_CALL_NAME),
            new AttributesDescriptor("Method Call Name", Perl6Highlighter.METHOD_CALL_NAME),
            new AttributesDescriptor("Parentheses", Perl6Highlighter.PARENTHESES),
            new AttributesDescriptor("Block Curly Braces", Perl6Highlighter.BLOCK_CURLY_BRACKETS),
            new AttributesDescriptor("Whatever", Perl6Highlighter.WHATEVER),
            new AttributesDescriptor("Stub Code (..., ???, !!!)", Perl6Highlighter.STUB_CODE),
            new AttributesDescriptor("Argument Capture (\\$foo, \\($a, $b))", Perl6Highlighter.CAPTURE_TERM),
            new AttributesDescriptor("Other Terms (Including User Defined)", Perl6Highlighter.TERM),
            new AttributesDescriptor("Current Object (self, sigil in $.foo(...))", Perl6Highlighter.SELF),
            new AttributesDescriptor("Only Star (Protos)", Perl6Highlighter.ONLY_STAR),
            new AttributesDescriptor("Parameter Separator", Perl6Highlighter.PARAMETER_SEPARATOR),
            new AttributesDescriptor("Named Parameter Colon and Parentheses", Perl6Highlighter.NAMED_PARAMETER_SYNTAX),
            new AttributesDescriptor("Named Parameter Name Alias", Perl6Highlighter.NAMED_PARAMETER_NAME_ALIAS),
            new AttributesDescriptor("Parameter Quantifier (Slurpy, Optional, Required)", Perl6Highlighter.PARAMETER_QUANTIFIER),
            new AttributesDescriptor("Parameter or variable constraint (where)", Perl6Highlighter.WHERE_CONSTRAINT),
            new AttributesDescriptor("Return Type Arrow (-->)", Perl6Highlighter.RETURN_ARROW),
            new AttributesDescriptor("String Literal Quote", Perl6Highlighter.STRING_LITERAL_QUOTE),
            new AttributesDescriptor("String Literal Value", Perl6Highlighter.STRING_LITERAL_CHAR),
            new AttributesDescriptor("String Literal Escape", Perl6Highlighter.STRING_LITERAL_ESCAPE),
            new AttributesDescriptor("String Literal Invalid Escape", Perl6Highlighter.STRING_LITERAL_BAD_ESCAPE),
            new AttributesDescriptor("Regex Literal Quote", Perl6Highlighter.QUOTE_REGEX),
            new AttributesDescriptor("Quote Pair (on string and regex literals)", Perl6Highlighter.QUOTE_PAIR),
            new AttributesDescriptor("Quote Modifier", Perl6Highlighter.QUOTE_MOD),
            new AttributesDescriptor("Array Composer ([...])", Perl6Highlighter.ARRAY_COMPOSER),
            new AttributesDescriptor("Pair (Colon Pair or Key Before =>)", Perl6Highlighter.PAIR_KEY),
            new AttributesDescriptor("Trait Keyword", Perl6Highlighter.TRAIT),
            new AttributesDescriptor("Type Parameter Brackets", Perl6Highlighter.TYPE_PARAMETER_BRACKET),
            new AttributesDescriptor("Type Coercion Parentheses", Perl6Highlighter.TYPE_COERCION_PARENTHESES),
            new AttributesDescriptor("Regex Infix (Alternation, Conjunction, Goal)", Perl6Highlighter.REGEX_INFIX),
            new AttributesDescriptor("Regex Anchor", Perl6Highlighter.REGEX_ANCHOR),
            new AttributesDescriptor("Regex Group (Square Brackets)", Perl6Highlighter.REGEX_GROUP_BRACKET),
            new AttributesDescriptor("Regex Capture", Perl6Highlighter.REGEX_CAPTURE),
            new AttributesDescriptor("Regex Quantifier", Perl6Highlighter.REGEX_QUANTIFIER),
            new AttributesDescriptor("Regex Built-in Character Class", Perl6Highlighter.REGEX_BUILTIN_CCLASS),
            new AttributesDescriptor("Regex Invalid Backslash Sequence", Perl6Highlighter.REGEX_BACKSLASH_BAD),
            new AttributesDescriptor("Regex Assertion Angle Brackets", Perl6Highlighter.REGEX_ASSERTION_ANGLE),
            new AttributesDescriptor("Regex Lookaround (? and !)", Perl6Highlighter.REGEX_LOOKAROUND),
            new AttributesDescriptor("Regex Character Class Syntax", Perl6Highlighter.REGEX_CCLASS_SYNTAX),
            new AttributesDescriptor("Regex Modifier", Perl6Highlighter.REGEX_MOD),
            new AttributesDescriptor("Rule Sigspace (implicit <.ws> call)", Perl6Highlighter.REGEX_SIG_SPACE),
            new AttributesDescriptor("Transliteration Literal Character", Perl6Highlighter.TRANS_CHAR),
            new AttributesDescriptor("Transliteration Escape", Perl6Highlighter.TRANS_ESCAPE),
            new AttributesDescriptor("Transliteration Range Operator", Perl6Highlighter.TRANS_RANGE),
            new AttributesDescriptor("Transliteration Invalid Syntax", Perl6Highlighter.TRANS_BAD),
            new AttributesDescriptor("Pod Directive", Perl6Highlighter.POD_DIRECTIVE),
            new AttributesDescriptor("Pod Typename", Perl6Highlighter.POD_TYPENAME),
            new AttributesDescriptor("Pod Configuration", Perl6Highlighter.POD_CONFIGURATION),
            new AttributesDescriptor("Pod Text", Perl6Highlighter.POD_TEXT),
            new AttributesDescriptor("Pod Text (Bold)", Perl6Highlighter.POD_TEXT_BOLD),
            new AttributesDescriptor("Pod Text (Italic)", Perl6Highlighter.POD_TEXT_ITALIC),
            new AttributesDescriptor("Pod Text (Underlined)", Perl6Highlighter.POD_TEXT_UNDERLINE),
            new AttributesDescriptor("Pod Format Code", Perl6Highlighter.POD_FORMAT_CODE),
            new AttributesDescriptor("Pod Format Delimiters", Perl6Highlighter.POD_FORMAT_QUOTES),
            new AttributesDescriptor("Quasi Quote", Perl6Highlighter.QUASI),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new Perl6SyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "use JSON::Tiny;\n\n" +
               "grammar IPv4 {\n" +
               "    token TOP { <seg> ** 4 % '.' }\n" +
               "    token seg { \\d+ { 0 <= $/ <= 255 } }\n" +
               "}\n\n" +
               "# Find all IPv4 data sources and show them.\n" +
               "my @data = from-json(slurp 'input.json');\n" +
               "for @data.map(*<from>) -> $from {\n" +
               "    if IPv4.parse($from) {\n" +
               "        say \"Address: $from\";\n" +
               "    }\n" +
               "}\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Raku";
    }
}
