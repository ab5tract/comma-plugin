package org.raku.cro.template.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.raku.RakuIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

@InternalIgnoreDependencyViolation
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
        return RakuIcons.CRO;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new CroTemplateSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
            <:macro layout($title)>
              <html>
                <head><title><$title></title></head>
                <body>
                  <:body>
                </body>
              </html>
            </:>
            <:sub heading($text)>
              <h1><$text></h1>
            </:>

            <|layout('Cro')>
              <&heading('Reasons Cro is awesome')>
              <@reasons>
                <p><.number>: <.explanation></p>
              </@>
            </|>
            """;
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
        return "Cro Template";
    }
}
