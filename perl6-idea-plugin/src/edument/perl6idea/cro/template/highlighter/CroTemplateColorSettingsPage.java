package edument.perl6idea.cro.template.highlighter;

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

public class CroTemplateColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Bad syntax", CroTemplateHighlighter.BAD_CHARACTER),
            new AttributesDescriptor("Template tag markup", CroTemplateHighlighter.TEMPLATE_TAG),
            new AttributesDescriptor("Declaration keyword", CroTemplateHighlighter.DECLARATION),
            new AttributesDescriptor("Variable name", CroTemplateHighlighter.VARIABLE),
            new AttributesDescriptor("Call name", CroTemplateHighlighter.CALL_NAME),
            new AttributesDescriptor("Infix operator", CroTemplateHighlighter.INFIX),
            new AttributesDescriptor("String literal quote", CroTemplateHighlighter.STRING_LITERAL_QUOTE),
            new AttributesDescriptor("String literal value", CroTemplateHighlighter.STRING_LITERAL_CHAR),
            new AttributesDescriptor("Numeric literal", CroTemplateHighlighter.NUMERIC_LITERAL),
            new AttributesDescriptor("Boolean literal", CroTemplateHighlighter.BOOL_LITERAL),
            new AttributesDescriptor("Literal tag markup", CroTemplateHighlighter.LITERAL_TAG),
            new AttributesDescriptor("Literal tag name", CroTemplateHighlighter.LITERAL_TAG_NAME),
            new AttributesDescriptor("Literal attribute name", CroTemplateHighlighter.LITERAL_ATTRIBUTE_NAME),
            new AttributesDescriptor("Literal attribute value", CroTemplateHighlighter.LITERAL_ATTRIBUTE_VALUE),
            new AttributesDescriptor("Literal text", CroTemplateHighlighter.LITERAL_TEXT),
            new AttributesDescriptor("Parentheses", CroTemplateHighlighter.PARENS),
            new AttributesDescriptor("Array indexer", CroTemplateHighlighter.BRACKETS),
            new AttributesDescriptor("Curly braces", CroTemplateHighlighter.BRACES),
            new AttributesDescriptor("Comma", CroTemplateHighlighter.COMMA),
            new AttributesDescriptor("Comment", CroTemplateHighlighter.COMMENT),
            new AttributesDescriptor("Named argument syntax", CroTemplateHighlighter.NAMED_ARGUMENT_SYNTAX),
            new AttributesDescriptor("Module name", CroTemplateHighlighter.MODULE_NAME),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Perl6Icons.CRO;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new CroTemplateSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "<:macro layout($title)>\n" +
               "  <html>\n" +
               "    <head><title><$title></title></head>\n" +
               "    <body>\n" +
               "      <:body>\n" +
               "    </body>\n" +
               "  </html>\n" +
               "</:>\n" +
               "<:sub heading($text)>\n" +
               "  <h1><$text></h1>\n" +
               "</:>\n" +
               "\n" +
               "<|layout('Cro')>\n" +
               "  <&heading('Reasons Cro is awesome')>\n" +
               "  <@reasons>\n" +
               "    <p><.number>: <.explanation></p>\n" +
               "  </@>\n" +
               "</|>\n";
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
        return "Cro Template";
    }
}
