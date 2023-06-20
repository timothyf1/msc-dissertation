from alert_types.junction import Junction
from process_data import map_graph, Graph
import osmnx
import unittest

class TestJunction(unittest.TestCase):
    def test_roundabout_2_tert_1_rest1lane_1_unclass(self):
        G = map_graph("test/osm-data/junction/roundabout_2_tert_1_rest1lane_1_unclass.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(Junction.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_junction_unclass_oneway_into_secondary(self):
        G = map_graph("test/osm-data/junction/junction_unclass_oneway_into_secondary.osm")

        alert_points = []
        print("Into")
        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(Junction.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_junction_unclass_oneway_leaving_secondary(self):
        G = map_graph("test/osm-data/junction/junction_unclass_oneway_leaving_secondary.osm")

        alert_points = []
        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(Junction.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_tert_1_lane_joining_resd(self):
        G = map_graph("test/osm-data/junction/tert_1_lane_joining_resd.osm")

        alert_points = []
        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(Junction.alert_locations(G, node))

        assert len(alert_points) == 1
