# Msc Dissertation

This is the repository for my Msc Computing Dissertation.

There are two parts to this project.

- Data processing to produce a set of alert points
- Mobile application to give audible alerts while driving


## Used Libraries

### Python

#### OSMnx

[OSMnx](https://osmnx.readthedocs.io/en/stable/) is a Python library for working with OpenStreetMap data. It is licensed under the [MIT license](https://github.com/gboeing/osmnx/blob/main/LICENSE.txt)

#### geopy

[geopy](https://geopy.readthedocs.io/en/stable/) is a Python client for several popular geocoding web services. It is licensed under the [MIT license](https://github.com/geopy/geopy/blob/master/LICENSE)

#### progressbar2

[progressbar2](https://github.com/WoLpH/python-progressbar) is a Python library to show a progress bar for a typically long running operation. It is licensed under [BSD license](https://github.com/wolph/python-progressbar/blob/develop/LICENSE)

### Java

#### SimpleLatLng

[SimpleLatLng](https://github.com/JavadocMD/simplelatlng) is a Java library for latitude and longitude calculations.
This is licensed using the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)

## Data Processing

There are two scripts required for to run the data processing. Before running any scripts the following is required

- The `.osm` or `.osm.pbf` file will need to be put inside the `data-processing/osm-raw` directory.
- Both [Osmconvert](https://wiki.openstreetmap.org/wiki/Osmconvert) and [Osmfilter](https://wiki.openstreetmap.org/wiki/Osmfilter) need to be downloaded and placed in the `data-processing` directory with the file names `osmconvert64.exe` and `osmfilter.exe` respectively.

Once the above is completed the two scripts are called for the data processing.

- First the raw data needs initial filtering by running `.\filter-raw-osm-data.ps1 .\osm-raw\NAME_OF_OSM_FILE.osm.pbf`.
  Once completed this will create a `NAME_OF_OSM_FILE.osm` file in the `osm-filtered` directory.
- Second run the python script with the following command `python .\process_data.py .\osm-filtered\NAME_OF_OSM_FILE.osm`
  When completed the alerts will be saved to a JSON file `NAME_OF_OSM_FILE.json` in the `alerts` directory.

## Android Application

The alert JSON file is copied into the `app/app/src/main/res/raw` directory. Then in `AlertCheckerService.java` within `app/app/src/main/java/com/example/gpssafetydrivingapp/alerts` within the `loadAlertPoints()` method the line `getApplicationContext().getResources().openRawResource(R.raw.NAME_OF_JSON)` will need NAME_OF_JSON to be replaced with JSON filename without the extension.

The Android application can then be built.
