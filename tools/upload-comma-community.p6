sub MAIN($version) {
    my $orig-version = slurp('../build.txt').trim;
    my @releasables = '.tar.gz', '.exe';
    for @releasables {
        my $cur-name = "../out/comma/artifacts/commaCT-$orig-version$_";
        my $rel-name = "../out/comma/artifacts/commaCT-$version$_";
        copy $cur-name, $rel-name;
        run 'gsutil', 'cp', $rel-name, 'gs://comma-downloads/';
    }
}
