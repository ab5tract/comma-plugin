sub MAIN($version) {
    run 'gsutil', 'cp', "../out/commaCT/artifacts/CT-plugins/comma-ct-$version.zip",
        'gs://comma-downloads/';
}
