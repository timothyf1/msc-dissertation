$fileName = $args[0]

if (Test-Path $fileName) {
    $file = Get-ChildItem $fileName

    if (! ($file.Extension -eq ".osm")) {
        $fileBase = $file.baseName
        write-host "Begin conversion to .osm"
        ./osmconvert64-0.8.8p $file --out-osm -o="osm-raw/$fileBase"
        write-host "Conversion to .osm completed."
    } else {
        $fileBase = $file.Name
    }

    write-host "Begin filter."
    ./osmfilter osm-raw/$fileBase --parameter-file=filter.txt -o="osm-filtered/$fileBase"
    write-host "Filtering completed"

} else {
    write-host "The file name provided is not valid"
}
