say 12341234123412341234 + 1234123412342;


class Foo {
  method foo($aaa1, $aaa1, $aaa1) {
    if True { say "One-liner" }
    else {}

    for ^42 {
      LAST { say "LAST" }
    }
  }

  method answer is copy is copy is copy {
    -> {}
  }
  method empty {}

  class Empty {}
}

grammar Bar {
  token a {}
  token long {
    \w \w \w \w
  }
}

Foo.a-very-long-method-name(424242).a-very-long-method-name(42424242);

my $abc = [12341234, 12342134, 12342134];