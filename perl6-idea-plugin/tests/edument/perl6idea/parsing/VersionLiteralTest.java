package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class VersionLiteralTest extends ParsingTestCase {
  public VersionLiteralTest() {
    super("", "p6", new Perl6ParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "perl6-idea-plugin/testData/parsing/version-literal";
  }

  @Override
  protected boolean skipSpaces() {
    return false;
  }

  @Override
  protected boolean includeRanges() {
    return true;
  }
}
