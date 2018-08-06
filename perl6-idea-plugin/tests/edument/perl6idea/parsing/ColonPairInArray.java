package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class ColonPairInArray extends ParsingTestCase {
  public ColonPairInArray() {
    super("", "p6", new Perl6ParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/parsing/colonpair-in-array";
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
