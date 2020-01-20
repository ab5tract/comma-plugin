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
            new AttributesDescriptor("Bad Syntax", CroTemplateHighlighter.BAD_CHARACTER),
            new AttributesDescriptor("Template Tag Markup", CroTemplateHighlighter.TEMPLATE_TAG),
            new AttributesDescriptor("Declaration Keyword", CroTemplateHighlighter.DECLARATION),
            new AttributesDescriptor("Variable Name", CroTemplateHighlighter.VARIABLE),
            new AttributesDescriptor("Call Name", CroTemplateHighlighter.CALL_NAME),
            new AttributesDescriptor("Infix Operator", CroTemplateHighlighter.INFIX),
            new AttributesDescriptor("String Literal Quote", CroTemplateHighlighter.STRING_LITERAL_QUOTE),
            new AttributesDescriptor("String Literal Value", CroTemplateHighlighter.STRING_LITERAL_CHAR),
            new AttributesDescriptor("Numeric Literal", CroTemplateHighlighter.NUMERIC_LITERAL),
            new AttributesDescriptor("Boolean Literal", CroTemplateHighlighter.BOOL_LITERAL),
            new AttributesDescriptor("Literal Tag Markup", CroTemplateHighlighter.LITERAL_TAG),
            new AttributesDescriptor("Literal Tag Name", CroTemplateHighlighter.LITERAL_TAG_NAME),
            new AttributesDescriptor("Literal Attribute Name", CroTemplateHighlighter.LITERAL_ATTRIBUTE_NAME),
            new AttributesDescriptor("Literal Attribute Value", CroTemplateHighlighter.LITERAL_ATTRIBUTE_VALUE),
            new AttributesDescriptor("Literal Text", CroTemplateHighlighter.LITERAL_TEXT),
            new AttributesDescriptor("Parentheses", CroTemplateHighlighter.PARENS),
            new AttributesDescriptor("Array Indexer", CroTemplateHighlighter.BRACKETS),
            new AttributesDescriptor("Curly Braces", CroTemplateHighlighter.BRACES),
            new AttributesDescriptor("Comma", CroTemplateHighlighter.COMMA),
            new AttributesDescriptor("Comment", CroTemplateHighlighter.COMMENT),
            new AttributesDescriptor("Named Argument Syntax", CroTemplateHighlighter.NAMED_ARGUMENT_SYNTAX),
            new AttributesDescriptor("Module Name", CroTemplateHighlighter.MODULE_NAME),
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
