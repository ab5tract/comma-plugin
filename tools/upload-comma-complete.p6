sub MAIN($version) {
    my $orig-version = slurp('../build.txt').trim;
    my @releasables = '.tar.gz', '.mac.zip', '.exe';
    for @releasables {
        my $cur-name = "../out/commaCP/artifacts/commaCP-$orig-version$_";
        my $rel-name = "../out/commaCP/artifacts/commaCP-$version$_";
        copy $cur-name, $rel-name;
        run 'gsutil', 'cp', $rel-name, 'gs://comma-downloads/';
    }
}
