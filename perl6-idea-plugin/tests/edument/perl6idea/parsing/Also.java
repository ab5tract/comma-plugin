package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class Also extends ParsingTestCase {
  public Also() {
    super("", "p6", new Perl6ParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "perl6-idea-plugin/testData/parsing/also";
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
