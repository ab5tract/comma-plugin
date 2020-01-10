sub MAIN($version) {
    run 'gsutil', 'cp', "../out/commaCT/artifacts/CT-plugins/comma-ct.zip",
        "gs://comma-downloads/comma-ct-plugin-$version.zip";
}
