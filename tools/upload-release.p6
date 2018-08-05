sub MAIN() {
    my $version = slurp('../build.txt').trim;
    my @releasables = 
        "../out/comma-se/artifacts/commaCO-$version.tar.gz",
        "../out/comma-se/artifacts/commaCO-$version.mac.zip",
        "../out/comma-se/artifacts/commaCO-$version.exe",
        "perl6-idea-plugin/build/distributions/perl6-idea-plugin-$version.zip";
    for @releasables {
        run 'gsutil', 'cp', $_, 'gs://comma-downloads/';
    }
}
