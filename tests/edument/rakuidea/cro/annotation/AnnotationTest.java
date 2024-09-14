package edument.rakuidea.cro.annotation;

import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.cro.template.CroTemplateFileType;

public class AnnotationTest extends CommaFixtureTestCase {
    public void testDuplicateCroTemplateSeparator() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
            "<@things>\n" +
            "  <$_>\n" +
            "  <:separator><hr></:>\n" +
            "  <error descr=\"Duplicate separator\"><:separator><hr></:></error>\n" +
            "</@>");
        myFixture.checkHighlighting();
    }

    public void testMisplacedCroTemplateSeparator() {
        myFixture.configureByText(CroTemplateFileType.INSTANCE,
            "<?.foo>\n" +
            "  <error descr=\"Separator may only occur directly in an iteration\"><:separator><hr></:></error>\n" +
            "</?>");
        myFixture.checkHighlighting();
    }
}
