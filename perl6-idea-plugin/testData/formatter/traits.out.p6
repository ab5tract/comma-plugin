my $a-variable is cookie
               does cookie = 42;

sub foo() is cookie
          does cookie {}

sub a($a is cookie
         does cookie) {}

token foo is cookie
          is cookie {
    <?>
}

enum quux-to-beyond is cookie
                    does cookie <>;

subset foo is cookie
           does cookie {};

constant $a is cookie
            is cookie = 42;

class CLASSNAME does cookie
                is fuki {}