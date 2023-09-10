$fileName = $args[0]

if (Test-Path $fileName) {
    $file = Get-ChildItem $fileName

    if ($file.Extension -eq ".pbf") {
        $fileBase = $file.baseName

        if (!(Test-Path osm-raw)) {
            mkdir "osm-raw" | Out-Null
        }

        write-host "Begin conversion to .osm"
        ./osmconvert64 $file --out-osm -o="osm-raw/$fileBase"
        write-host "Conversion to .osm completed."
    } elseif ($file.Extension -eq ".osm") {
        $fileBase = $file.Name
    } else {
        throw "Input file is not .osm or .osm.pbf file"
    }

    if (!(Test-Path osm-filtered)) {
        mkdir "osm-filtered" | Out-Null
    }

    write-host "Begin filter."
    ./osmfilter osm-raw/$fileBase --parameter-file=filter.txt -o="osm-filtered/$fileBase"
    write-host "Filtering completed"

} else {
    write-host "The file name provided is not valid"
}
