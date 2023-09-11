from alert_types.road_widens import RoadWidens
from graph import Graph
import unittest

class TestRoadWidens(unittest.TestCase):
    def test_unclass_to_tert(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/unclass_to_tert.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_resd_to_tert(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/resd_to_tert.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_unclass_1_to_2_lanes(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/unclass_1_to_2_lanes.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_unclass_2_lanes(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/unclass_2_lanes.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_resd_1_to_2_lanes_a(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/resd_1_to_2_lanes_a.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_resd_1_to_2_lanes_b(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/resd_1_to_2_lanes_b.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_resd_1_lane(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/resd_1_lane.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_tert_1_to_2_lanes(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/tert_1_to_2_lanes.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_road_coming_into_one_way(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/road_coming_into_one_way.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_unclass_1_to_2_lanes_one_way(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/unclass_1_to_2_lanes_one_way.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_one_way_1_to_2_lanes(self):
        G = Graph.create_map_graph("test/osm-data/road_widens/one_way_1_to_2_lanes.osm")
        road_widens = RoadWidens()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(road_widens.alert_locations(G, node))

        assert len(alert_points) == 0
