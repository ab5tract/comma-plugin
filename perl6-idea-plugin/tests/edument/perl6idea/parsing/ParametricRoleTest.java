package edument.perl6idea.parsing;

import com.intellij.testFramework.ParsingTestCase;

public class ParametricRoleTest extends ParsingTestCase {
  public ParametricRoleTest() {
    super("", "p6", new Perl6ParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "perl6-idea-plugin/testData/parsing/parametric-role";
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
