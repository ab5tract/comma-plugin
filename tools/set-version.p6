sub MAIN($version) {
    my $number = "CO-$version";
    my $date = Date.new(DateTime.now).Str.subst('-', '', :g);

    with $version ~~ /^ (\d**4) '.' (\d**1..2) '.' (\d+)? $/ -> ($maj, $min, $build, |) {
        given slurp('ide/src/META-INF/comma-core.xml') {
            spurt 'ide/src/META-INF/comma-core.xml',
                .subst(/ '<version>' <( .+? )> '</version>' /, $version);
        }
        given slurp('perl6-idea-plugin/gradle.properties') {
            spurt 'perl6-idea-plugin/gradle.properties',
                .subst(/'pluginVersion=' <( \N+ )>/, $version);
        }
        given slurp('perl6-idea-plugin/resources/about/pluginChanges.html') {
            spurt 'perl6-idea-plugin/resources/about/pluginChanges.html',
                .subst(/ '<h2>' <( .+? )> '</h2>' /, $version);
        }
        given slurp('resources/idea/CommaCoreApplicationInfo.xml') {
            spurt 'resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<version ' <( 'major="' \d+ '" minor="' \d+ '"' )>/,
                    qq|major="$maj" minor="$min"|);
        }
        given slurp('resources/idea/CommaCoreApplicationInfo.xml') {
            my $date = Date.today.yyyy-mm-dd.subst('-', '', :g);
            spurt 'resources/idea/CommaCoreApplicationInfo.xml',
                .subst(/'<build ' <( 'number="' <-["]>+ '" date="' \d+ '"' )>/,
                    qq|number="CO-$maj.$min.$build" date="$date"|);
        }
        spurt '../build.txt', "$version\n";
    }
    else {
        note "Version must be of format YYYY.[M]M.B";
        exit 1;
    }
}
