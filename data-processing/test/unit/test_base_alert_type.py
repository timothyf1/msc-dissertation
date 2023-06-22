from alert_types.base import AlertType
from process_data import map_graph, Graph
import osmnx
import unittest

class TestAlertType(unittest.TestCase):

    def test_create_alert(self):
        location = {
            "latitude" : 45.2,
            "longitude" : -56.2,
            "bearing" : 35
        }

        alert = AlertType.create_alert(3, 3543, location)

        assert alert.latitude == location["latitude"]
        assert alert.longitude == location["longitude"]
        assert alert.bearing == location["bearing"]
        assert alert.id == "354335"
        assert alert.node == 3543
        assert alert.type == 3

    def test_sort_roads_1(self):
        G = map_graph("test/osm-data/graph/map.osm")

        roads = G.edges(-101753, data=True)
        roads_sorted = AlertType.sort_roads(roads)

        assert len(roads_sorted["single"]) == 0
        assert len(roads_sorted["multi"]) == 4

        roads = G.edges(-101800, data=True)
        roads_sorted = AlertType.sort_roads(roads)

        assert len(roads_sorted["single"]) == 1
        assert len(roads_sorted["multi"]) == 2
