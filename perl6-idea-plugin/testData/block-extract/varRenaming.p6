sub foo-bar(Int $bbb) {
    say $bbb;
}
{
    my $aaa = 5;
    foo-bar($aaa);
}