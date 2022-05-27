package edument.perl6idea.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

@InternalIgnoreDependencyViolation
public class Perl6ColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Bad syntax", Perl6Highlighter.BAD_CHARACTER),
            new AttributesDescriptor("Type name", Perl6Highlighter.TYPE_NAME),
            new AttributesDescriptor("Statement terminator", Perl6Highlighter.STATEMENT_TERMINATOR),
            new AttributesDescriptor("Statement control", Perl6Highlighter.STATEMENT_CONTROL),
            new AttributesDescriptor("Phaser", Perl6Highlighter.PHASER),
            new AttributesDescriptor("Label name", Perl6Highlighter.LABEL_NAME),
            new AttributesDescriptor("Label colon", Perl6Highlighter.LABEL_COLON),
            new AttributesDescriptor("Statement prefix", Perl6Highlighter.STATEMENT_PREFIX),
            new AttributesDescriptor("Statement modifier", Perl6Highlighter.STATEMENT_MOD),
            new AttributesDescriptor("Scope keyword", Perl6Highlighter.SCOPE_DECLARATOR),
            new AttributesDescriptor("Multi keyword", Perl6Highlighter.MULTI_DECLARATOR),
            new AttributesDescriptor("Routine keyword", Perl6Highlighter.ROUTINE_DECLARATOR),
            new AttributesDescriptor("Package keyword", Perl6Highlighter.PACKAGE_DECLARATOR),
            new AttributesDescriptor("Routine name", Perl6Highlighter.ROUTINE_NAME),
            new AttributesDescriptor("Prefix operator", Perl6Highlighter.PREFIX),
            new AttributesDescriptor("Infix operator", Perl6Highlighter.INFIX),
            new AttributesDescriptor("Postfix operator", Perl6Highlighter.POSTFIX),
            new AttributesDescriptor("Meta-operator", Perl6Highlighter.METAOP),
            new AttributesDescriptor("Array indexer", Perl6Highlighter.ARRAY_INDEXER),
            new AttributesDescriptor("Hash indexer", Perl6Highlighter.HASH_INDEXER),
            new AttributesDescriptor("Lambda (-> and <->)", Perl6Highlighter.LAMBDA),
            new AttributesDescriptor("Variable", Perl6Highlighter.VARIABLE),
            new AttributesDescriptor("Contextualizer", Perl6Highlighter.CONTEXTUALIZER),
            new AttributesDescriptor("Variable shape declaration", Perl6Highlighter.SHAPE_DECLARATION),
            new AttributesDescriptor("Type Declarator (enum, subset, constant)", Perl6Highlighter.TYPE_DECLARATOR),
            new AttributesDescriptor("Term Declaration Backslash (my \\answer = 42)", Perl6Highlighter.TERM_DECLARATION_BACKSLASH),
            new AttributesDescriptor("Numeric literal", Perl6Highlighter.NUMERIC_LITERAL),
            new AttributesDescriptor("Version literal", Perl6Highlighter.VERSION),
            new AttributesDescriptor("Comment", Perl6Highlighter.COMMENT),
            new AttributesDescriptor("Sub call name", Perl6Highlighter.SUB_CALL_NAME),
            new AttributesDescriptor("Method call name", Perl6Highlighter.METHOD_CALL_NAME),
            new AttributesDescriptor("Parentheses", Perl6Highlighter.PARENTHESES),
            new AttributesDescriptor("Block curly braces", Perl6Highlighter.BLOCK_CURLY_BRACKETS),
            new AttributesDescriptor("Whatever", Perl6Highlighter.WHATEVER),
            new AttributesDescriptor("Stub Code (..., ???, !!!)", Perl6Highlighter.STUB_CODE),
            new AttributesDescriptor("Argument Capture (\\$foo, \\($a, $b))", Perl6Highlighter.CAPTURE_TERM),
            new AttributesDescriptor("Other terms (including user defined)", Perl6Highlighter.TERM),
            new AttributesDescriptor("Current Object (self, sigil in $.foo(...))", Perl6Highlighter.SELF),
            new AttributesDescriptor("Only Star (Protos)", Perl6Highlighter.ONLY_STAR),
            new AttributesDescriptor("Parameter separator", Perl6Highlighter.PARAMETER_SEPARATOR),
            new AttributesDescriptor("Named parameter colon and parentheses", Perl6Highlighter.NAMED_PARAMETER_SYNTAX),
            new AttributesDescriptor("Named parameter name alias", Perl6Highlighter.NAMED_PARAMETER_NAME_ALIAS),
            new AttributesDescriptor("Parameter quantifier (slurpy, optional, required)", Perl6Highlighter.PARAMETER_QUANTIFIER),
            new AttributesDescriptor("Parameter or variable constraint (where)", Perl6Highlighter.WHERE_CONSTRAINT),
            new AttributesDescriptor("Return type arrow (-->)", Perl6Highlighter.RETURN_ARROW),
            new AttributesDescriptor("String literal quote", Perl6Highlighter.STRING_LITERAL_QUOTE),
            new AttributesDescriptor("String literal value", Perl6Highlighter.STRING_LITERAL_CHAR),
            new AttributesDescriptor("String literal escape", Perl6Highlighter.STRING_LITERAL_ESCAPE),
            new AttributesDescriptor("String literal invalid escape", Perl6Highlighter.STRING_LITERAL_BAD_ESCAPE),
            new AttributesDescriptor("Regex literal quote", Perl6Highlighter.QUOTE_REGEX),
            new AttributesDescriptor("Quote Pair (on string and regex literals)", Perl6Highlighter.QUOTE_PAIR),
            new AttributesDescriptor("Quote modifier", Perl6Highlighter.QUOTE_MOD),
            new AttributesDescriptor("Array Composer ([...])", Perl6Highlighter.ARRAY_COMPOSER),
            new AttributesDescriptor("Hash Composer ({...})", Perl6Highlighter.ARRAY_COMPOSER),
            new AttributesDescriptor("Pair (colon pair or key before =>)", Perl6Highlighter.PAIR_KEY),
            new AttributesDescriptor("Trait keyword", Perl6Highlighter.TRAIT),
            new AttributesDescriptor("Type parameter brackets", Perl6Highlighter.TYPE_PARAMETER_BRACKET),
            new AttributesDescriptor("Type coercion parentheses", Perl6Highlighter.TYPE_COERCION_PARENTHESES),
            new AttributesDescriptor("Regex infix (alternation, conjunction, goal)", Perl6Highlighter.REGEX_INFIX),
            new AttributesDescriptor("Regex anchor", Perl6Highlighter.REGEX_ANCHOR),
            new AttributesDescriptor("Regex group (square brackets)", Perl6Highlighter.REGEX_GROUP_BRACKET),
            new AttributesDescriptor("Regex capture", Perl6Highlighter.REGEX_CAPTURE),
            new AttributesDescriptor("Regex quantifier", Perl6Highlighter.REGEX_QUANTIFIER),
            new AttributesDescriptor("Regex built-in character class", Perl6Highlighter.REGEX_BUILTIN_CCLASS),
            new AttributesDescriptor("Regex invalid backslash sequence", Perl6Highlighter.REGEX_BACKSLASH_BAD),
            new AttributesDescriptor("Regex assertion angle brackets", Perl6Highlighter.REGEX_ASSERTION_ANGLE),
            new AttributesDescriptor("Regex Lookaround (? and !)", Perl6Highlighter.REGEX_LOOKAROUND),
            new AttributesDescriptor("Regex character class syntax", Perl6Highlighter.REGEX_CCLASS_SYNTAX),
            new AttributesDescriptor("Regex modifier", Perl6Highlighter.REGEX_MOD),
            new AttributesDescriptor("Rule Sigspace (implicit <.ws> call)", Perl6Highlighter.REGEX_SIG_SPACE),
            new AttributesDescriptor("Transliteration literal character", Perl6Highlighter.TRANS_CHAR),
            new AttributesDescriptor("Transliteration escape", Perl6Highlighter.TRANS_ESCAPE),
            new AttributesDescriptor("Transliteration range operator", Perl6Highlighter.TRANS_RANGE),
            new AttributesDescriptor("Transliteration invalid syntax", Perl6Highlighter.TRANS_BAD),
            new AttributesDescriptor("Pod directive", Perl6Highlighter.POD_DIRECTIVE),
            new AttributesDescriptor("Pod typename", Perl6Highlighter.POD_TYPENAME),
            new AttributesDescriptor("Pod configuration", Perl6Highlighter.POD_CONFIGURATION),
            new AttributesDescriptor("Pod text", Perl6Highlighter.POD_TEXT),
            new AttributesDescriptor("Pod Text (Bold)", Perl6Highlighter.POD_TEXT_BOLD),
            new AttributesDescriptor("Pod Text (Italic)", Perl6Highlighter.POD_TEXT_ITALIC),
            new AttributesDescriptor("Pod Text (Underlined)", Perl6Highlighter.POD_TEXT_UNDERLINE),
            new AttributesDescriptor("Pod code block", Perl6Highlighter.POD_CODE),
            new AttributesDescriptor("Pod format code", Perl6Highlighter.POD_FORMAT_CODE),
            new AttributesDescriptor("Pod format delimiters", Perl6Highlighter.POD_FORMAT_QUOTES),
            new AttributesDescriptor("Quasi quote", Perl6Highlighter.QUASI),
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
        return "use v6;\n" +
               "use JSON::Tiny;\n" +
               "\n" +
               "role R[:param] {}\n" +
               "\n" +
               "grammar IPv4 is default {\n" +
               "    token TOP { <seg> ** 4 % '.' }\n" +
               "    token seg {\n" +
               "        \\d+ { 0 <= $/ <= 255 }\n" +
               "    }\n" +
               "\n" +
               "    proto method dummy() {*}\n" +
               "    multi method dummy(Cool(Int) $coerced) {!!!}\n" +
               "    multi method dummy($pos? where 1 ; :alias($named) --> Nil) {\n" +
               "        constant \\foo = key => 'value';\n" +
               "        LABEL: [+] self.dummy(:$named);\n" +
               "    }\n" +
               "}\n" +
               "\n" +
               "# Find all IPv4 data sources and show them.\n" +
               "my @data = from-json(slurp 'input.json');\n" +
               "for @data.map(*<from>) -> $from {\n" +
               "    if IPv4.parse($from) {\n" +
               "        do say \"Address: $from\";\n" +
               "    }\n" +
               "}\n" +
               "\n" +
               "BEGIN {\n" +
               "    my $capture = \\(\"capture\\n\", 42);\n" +
               "    quasi { ++$capture[42]++; }\n" +
               "    my $array[1;1] = ['composed'][0];\n" +
               "    say @$array, $$array; # <- contextualizers work...\n" +
               "    \n" +
               "}\n" +
               "\n" +
               "# Regex fun begins!\n" +
               "'foo' ~~ m:g!^ [(f) <[o]> $<foo>=[]] || 'constant' \\invalid !;\n" +
               "\n" +
               "\\";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Raku";
    }
}
