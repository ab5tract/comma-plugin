# Basic precedence
say ~$a++ ~ "xx" x 5;

# Associativity
say 1 + 2 + 3;
say 2 ** 3 ** 4;

# Assignment precedence and list precedence
my $a = 1 + 2 + 3;
my $b = 1, 2, 3;
$b = 1, 2, 3;
my @b = 1, 2, 3;
@b = 1, 2, 3;

# Adverbs
if not %h<a>:exists {
    say "hello";
}
if !%h<a>:exists {
    say "hello";
}