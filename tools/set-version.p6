sub MAIN($version) {
    my $number = "CO-$version";
    my $date = Date.new(DateTime.now).Str.subst('-', '', :g);

    with $version ~~ /^ (\d**4) '.' (\d**1..2) '.' (\d+)? $/ -> ($maj, $min, $build, |) {
        given slurp('community/resources/idea/CommaCoreApplicationInfo.xml') {
            spurt 'community/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<version ' <( 'major="' \d+ '" minor="' \d+ '"' 'micro="' \d+ '"' )>/,
                    qq|major="$maj" minor="$min" micro="$build"|);
        }
        given slurp('community/resources/idea/CommaCoreApplicationInfo.xml') {
            my $date = Date.today.yyyy-mm-dd.subst('-', '', :g);
            spurt 'community/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<build ' <( 'number="' <-["]>+ '" date="' \d+ '"' )>/,
                    qq|number="CO-$maj.$min.$build" date="$date"|);
        }
        given slurp('complete/resources/idea/CommaCoreApplicationInfo.xml') {
            spurt 'complete/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<version ' <( 'major="' \d+ '" minor="' \d+ '"' 'micro="' \d+ '"' )>/,
                    qq|major="$maj" minor="$min" micro="$build"|);
        }
        given slurp('complete/resources/idea/CommaCoreApplicationInfo.xml') {
            my $date = Date.today.yyyy-mm-dd.subst('-', '', :g);
            spurt 'complete/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<build ' <( 'number="' <-["]>+ '" date="' \d+ '"' )>/,
                    qq|number="CO-$maj.$min.$build" date="$date"|);
        }
    }
    else {
        note "Version must be of format YYYY.[M]M.B";
        exit 1;
    }
}
