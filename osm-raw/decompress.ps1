$fileName = $args[0]

if (Test-Path $fileName) {
    $file = Get-ChildItem $fileName

    $fileBase = $file.baseName

    ./osmconvert64-0.8.8p $file --out-osm -o="$fileBase"
}
