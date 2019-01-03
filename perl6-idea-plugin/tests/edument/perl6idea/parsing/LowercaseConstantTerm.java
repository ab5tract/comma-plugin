package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class LowercaseConstantTerm extends ParsingTestCase {
  public LowercaseConstantTerm() {
    super("", "p6", new Perl6ParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/parsing/lowercase-constant-term";
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
