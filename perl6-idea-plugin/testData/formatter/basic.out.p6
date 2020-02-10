use Foo::Bar;

class C {
    method meth(Int $x, Str $y) {
        if $y.chars > $x {
            say "long" ~
                $y.substr(0, $x) ~
                "...";
        }
        else {
            say "short";
        }
    }
}

if $here {
    say "blah";
}
else {
    say "wah";
}

c.meth(10,
       "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
