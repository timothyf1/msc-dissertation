from alert_types.junction import Junction
from graph import Graph
import osmnx
import unittest

class TestJunction(unittest.TestCase):
    def test_roundabout_2_tert_1_rest1lane_1_unclass(self):
        G = Graph.create_map_graph("test/osm-data/junction/roundabout_2_tert_1_rest1lane_1_unclass.osm")
        junction = Junction()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(junction.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_junction_unclass_oneway_into_secondary(self):
        G = Graph.create_map_graph("test/osm-data/junction/junction_unclass_oneway_into_secondary.osm")
        junction = Junction()

        alert_points = []

        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(junction.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_junction_unclass_oneway_leaving_secondary(self):
        G = Graph.create_map_graph("test/osm-data/junction/junction_unclass_oneway_leaving_secondary.osm")
        junction = Junction()

        alert_points = []
        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(junction.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_junction_2_cuclass_intercept(self):
        G = Graph.create_map_graph("test/osm-data/junction/junction_2_cuclass_intercept.osm")
        junction = Junction()

        alert_points = []
        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(junction.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_tert_1_lane_joining_resd(self):
        G = Graph.create_map_graph("test/osm-data/junction/tert_1_lane_joining_resd.osm")
        junction = Junction()

        alert_points = []
        for node in G.nodes:
            edges = G.edges(node, data=True)
            alert_points.extend(junction.alert_locations(G, node))

        assert len(alert_points) == 1
