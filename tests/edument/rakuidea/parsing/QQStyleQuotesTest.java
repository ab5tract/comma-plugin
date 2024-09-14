package edument.rakuidea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class QQStyleQuotesTest extends ParsingTestCase {
  public QQStyleQuotesTest() {
    super("", "p6", new RakuParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "rakuidea-idea-plugin/testData/parsing/qqquotes";
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
