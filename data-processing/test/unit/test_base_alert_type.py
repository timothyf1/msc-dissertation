from alert_types.base import TestAlertTypeInstance
from graph import Graph
import osmnx
import unittest

class TestAlertType(unittest.TestCase):

    def test_create_alert(self):
        location = {
            "latitude" : 45.2,
            "longitude" : -56.2,
            "bearing" : 35
        }

        test_instance = TestAlertTypeInstance()
        alert = test_instance.create_alert(3543, location)

        assert alert.latitude == location["latitude"]
        assert alert.longitude == location["longitude"]
        assert alert.bearing == location["bearing"]
        assert alert.id == "354335"
        assert alert.node == 3543
        assert alert.type == 0

    def test_alert_locations(self):
        test_instance = TestAlertTypeInstance()
        assert test_instance.alert_locations("a", "b") == None

    def test_find_alert_location_1(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        test_instance = TestAlertTypeInstance()
        node = -102069

        in_road_lanes = G.sort_roads(node, True)

        alert_location = test_instance.find_alert_location(G, node, in_road_lanes["single"][0], 50)

        assert alert_location["latitude"] == 51.35721666673582
        assert alert_location["longitude"] == -1.0919827977286938
        assert alert_location["bearing"] == 88.07327

    def test_find_alert_location_2(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        test_instance = TestAlertTypeInstance()
        node = -102069

        in_road_lanes = G.sort_roads(node, True)

        alert_location = test_instance.find_alert_location(G, node, in_road_lanes["single"][1], 50)
        print(alert_location)

        assert alert_location["latitude"] == 51.35701908374297
        assert alert_location["longitude"] == -1.090646875858708
        assert alert_location["bearing"] == 320.88173

    def test_find_alert_location_3(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        test_instance = TestAlertTypeInstance()
        node = -102067

        in_road_lanes = G.sort_roads(node, True)

        alert_location = test_instance.find_alert_location(G, node, in_road_lanes["single"][1], 50)

        assert alert_location["latitude"] == 51.35568477700531
        assert alert_location["longitude"] == -1.0965863553748094
        assert alert_location["bearing"] == 18.17436
