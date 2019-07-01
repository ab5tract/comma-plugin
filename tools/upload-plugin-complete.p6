sub MAIN($version) {
    run 'gsutil', 'cp', "../out/comma/artifacts/comma-cp-$version.zip",
        "gs://comma-downloads/comma-cp-plugin-$version.zip";
}
