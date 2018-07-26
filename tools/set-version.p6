sub MAIN($version) {
    with $version ~~ /^ (\d**4) '.' (\d**1..2) '.' (\d+) $/ -> ($maj, $min, $build, |) {
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
        spurt '../build.txt', "$version\n";
    }
    else {
        note "Version must be of format YYYY.M[M].N";
        exit 1;
    }
}
