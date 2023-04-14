sub MAIN($version) {
    run 'gsutil', 'cp', "../out/commaCT/artifacts/CT-plugins/comma-$version.zip",
        "gs://comma-downloads/comma-ct-plugin-$version.zip";
}
