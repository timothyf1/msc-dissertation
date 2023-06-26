from alert_types.base import AlertType
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

        alert = AlertType.create_alert(3, 3543, location)

        assert alert.latitude == location["latitude"]
        assert alert.longitude == location["longitude"]
        assert alert.bearing == location["bearing"]
        assert alert.id == "354335"
        assert alert.node == 3543
        assert alert.type == 3

    def test_sort_roads_1(self):
        G = Graph.create_map_graph("test/osm-data/graph/map.osm")

        roads = G.edges(-101753, data=True)
        roads_sorted = AlertType.sort_roads(roads)

        assert len(roads_sorted["single"]) == 0
        assert len(roads_sorted["multi"]) == 4

    def test_sort_roads_2(self):
        G = Graph.create_map_graph("test/osm-data/graph/map.osm")

        roads = G.edges(-101800, data=True)
        roads_sorted = AlertType.sort_roads(roads)

        assert len(roads_sorted["single"]) == 1
        assert len(roads_sorted["multi"]) == 2

    def test_sort_roads_3(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")

        roads = G.edges(-102069, data=True)
        roads_sorted = AlertType.sort_roads(roads)

        assert len(roads_sorted["single"]) == 2
        for road in roads_sorted["single"]:
            assert road[2]["lanes"] == "1"
        assert len(roads_sorted["multi"]) == 2
        for road in roads_sorted["multi"]:
            assert road[2]["lanes"] == "2"

    def test_alert_locations(self):
        assert AlertType.alert_locations("a", "b") == None

    def test_find_alert_location_1(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        node = -102069

        in_roads = G.in_edges(node, data=True)
        in_road_lanes = AlertType.sort_roads(in_roads)

        alert_location = AlertType.find_alert_location(G, node, in_road_lanes["single"][0], 50)

        assert alert_location["latitude"] == 51.35721666673582
        assert alert_location["longitude"] == -1.0919827977286938
        assert alert_location["bearing"] == 88.07327

    def test_find_alert_location_2(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        node = -102069

        in_roads = G.in_edges(node, data=True)
        in_road_lanes = AlertType.sort_roads(in_roads)

        alert_location = AlertType.find_alert_location(G, node, in_road_lanes["single"][1], 50)

        assert alert_location["latitude"] == 51.35701908374297
        assert alert_location["longitude"] == -1.090646875858708
        assert alert_location["bearing"] == 320.88173

    def test_find_alert_location_3(self):
        G = Graph.create_map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        node = -102067

        in_roads = G.in_edges(node, data=True)
        in_road_lanes = AlertType.sort_roads(in_roads)

        alert_location = AlertType.find_alert_location(G, node, in_road_lanes["single"][1], 50)

        assert alert_location["latitude"] == 51.35568477700531
        assert alert_location["longitude"] == -1.0965863553748094
        assert alert_location["bearing"] == 18.17436
