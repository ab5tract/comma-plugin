#!/usr/bin/env perl6


loop {
    state $num = 1;
    say do
        if $num %% 5 and $num %% 3 { "FizzBuzz" }
        elsif $num %% 3 { "Fizz" }
        elsif $num %% 5 { "Buzz" }
        else { $num }
    ;
    last if ++$num > 100;
}