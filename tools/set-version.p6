sub MAIN($version) {
    my $number = "CO-$version";
    my $date = Date.new(DateTime.now).Str.subst('-', '', :g);

    with $version ~~ /^ (\d**4) '.' (\d**1..2) '.' (\d+)? $/ -> ($maj, $min, $build, |) {
        given slurp('community/resources/idea/CommaCoreApplicationInfo.xml') {
            spurt 'community/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<version ' <( 'major="' \d+ '" minor="' \d+ '" micro="' \d+ '"' )>/,
                    qq|major="$maj" minor="$min" micro="$build"|);
        }
        given slurp('community/resources/idea/CommaCoreApplicationInfo.xml') {
            my $date = Date.today.yyyy-mm-dd.subst('-', '', :g);
            spurt 'community/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<build ' <( 'number="' <-["]>+ '" date="' \d+ '"' )>/,
                    qq|number="CT-$maj.$min.$build" date="$date"|);
        }
        given slurp('complete/resources/idea/CommaCoreApplicationInfo.xml') {
            spurt 'complete/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<version ' <( 'major="' \d+ '" minor="' \d+ '" micro="' \d+ '"' )>/,
                    qq|major="$maj" minor="$min" micro="$build"|);
        }
        given slurp('complete/resources/idea/CommaCoreApplicationInfo.xml') {
            my $date = Date.today.yyyy-mm-dd.subst('-', '', :g);
            spurt 'complete/resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<build ' <( 'number="' <-["]>+ '" date="' \d+ '"' )>/,
                    qq|number="CP-$maj.$min.$build" date="$date"|);
        }
        given slurp('community/resources/META-INF/plugin.xml') {
            spurt 'community/resources/META-INF/plugin.xml',
                .subst(/'<version>' <( .+? )> '</version>'/, $version);
        }
        given slurp('complete/resources/META-INF/plugin.xml') {
            spurt 'complete/resources/META-INF/plugin.xml',
                .subst(/'<version>' <( .+? )> '</version>'/, $version);
        }
    }
    else {
        note "Version must be of format YYYY.[M]M.B";
        exit 1;
    }
}
