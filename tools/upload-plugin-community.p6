sub MAIN($version) {
    run 'gsutil', 'cp', "../out/commaCP/artifacts/CP-plugins/comma-ct-$version.zip",
        "gs://comma-downloads/comma-ct-plugin-$version.zip";
}
