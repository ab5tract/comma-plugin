package edument.perl6idea.refactoring;

import edument.perl6idea.CommaFixtureTestCase;

import java.util.Arrays;

public class NameSuggesterTest extends CommaFixtureTestCase {
  public void testSinglePartName() {
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("test", ""));
    assertEquals(Arrays.asList("$TEST"), Perl6NameSuggester.getNamePieces("TEST", ""));
  }

  public void testSnakeCaseName() {
    assertEquals(Arrays.asList("$test-me", "$me"), Perl6NameSuggester.getNamePieces("test-me", ""));
    assertEquals(Arrays.asList("$do-test-me", "$test-me", "$me"), Perl6NameSuggester.getNamePieces("do-test-me", ""));
    assertEquals(Arrays.asList("$test-me", "$me"), Perl6NameSuggester.getNamePieces("is-test-me", ""));
    assertEquals(Arrays.asList("$test-me", "$me"), Perl6NameSuggester.getNamePieces("get-test-me", ""));
    assertEquals(Arrays.asList("$test-me", "$me"), Perl6NameSuggester.getNamePieces("get-is-test-me", ""));

    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("is-test", ""));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("get-test", ""));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("get-is-test", ""));

    assertEquals(Arrays.asList("$CALL-ME", "$ME"), Perl6NameSuggester.getNamePieces("CALL-ME", ""));
  }

  public void testUnderscoreCaseName() {
    assertEquals(Arrays.asList("$test_me", "$me"), Perl6NameSuggester.getNamePieces("test_me", ""));
    assertEquals(Arrays.asList("$do_test_me", "$test_me", "$me"), Perl6NameSuggester.getNamePieces("do_test_me", ""));
    assertEquals(Arrays.asList("$test_me", "$me"), Perl6NameSuggester.getNamePieces("is_test_me", ""));
    assertEquals(Arrays.asList("$test_me", "$me"), Perl6NameSuggester.getNamePieces("get_test_me", ""));
    assertEquals(Arrays.asList("$test_me", "$me"), Perl6NameSuggester.getNamePieces("get_is_test_me", ""));

    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("is_test", ""));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("get_test", ""));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("get_is_test", ""));

    assertEquals(Arrays.asList("$CALL_ME", "$ME"), Perl6NameSuggester.getNamePieces("CALL_ME", ""));
  }

  public void testCamelCaseName() {
    assertEquals(Arrays.asList("$testMe", "$me"), Perl6NameSuggester.getNamePieces("isTestMe", ""));
    assertEquals(Arrays.asList("$testMe", "$me"), Perl6NameSuggester.getNamePieces("testMe", ""));
    assertEquals(Arrays.asList("$doTestMe", "$testMe", "$me"), Perl6NameSuggester.getNamePieces("doTestMe", ""));
    assertEquals(Arrays.asList("$testMe", "$me"), Perl6NameSuggester.getNamePieces("getTestMe", ""));
    assertEquals(Arrays.asList("$testMe", "$me"), Perl6NameSuggester.getNamePieces("getIsTestMe", ""));

    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("isTest", ""));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("getTest", ""));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("getIsTest", ""));

    assertEquals(Arrays.asList("$itGDPRCompliant", "$gdprCompliant", "$compliant"), Perl6NameSuggester.getNamePieces("isItGDPRCompliant", ""));
  }

  public void testSigil() {
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("test", "Any"));
    assertEquals(Arrays.asList("$test"), Perl6NameSuggester.getNamePieces("test", "Mu"));

    assertEquals(Arrays.asList("%test"), Perl6NameSuggester.getNamePieces("test", "Hash[Int]"));
    assertEquals(Arrays.asList("%test"), Perl6NameSuggester.getNamePieces("test", "Map[Int]"));

    assertEquals(Arrays.asList("@test"), Perl6NameSuggester.getNamePieces("test", "List[Int]"));
    assertEquals(Arrays.asList("@test"), Perl6NameSuggester.getNamePieces("test", "Array[Int]"));
    assertEquals(Arrays.asList("@test"), Perl6NameSuggester.getNamePieces("test", "Positional[Int]"));
    assertEquals(Arrays.asList("@test"), Perl6NameSuggester.getNamePieces("test", "Seq[Int]"));
    assertEquals(Arrays.asList("@test"), Perl6NameSuggester.getNamePieces("test", "Iterable[Int]"));
  }
}
