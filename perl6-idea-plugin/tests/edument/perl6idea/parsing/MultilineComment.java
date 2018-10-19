package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class MultilineComment extends ParsingTestCase {
  public MultilineComment() {
    super("", "p6", new Perl6ParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/parsing/multiline-comment";
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