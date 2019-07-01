sub MAIN($version) {
    run 'gsutil', 'cp', "../out/comma/artifacts/comma-ct-$version.zip",
        "gs://comma-downloads/comma-ct-plugin-$version.zip";
}
