from alert_types.road_widens import RoadWidens
from process_data import map_graph
import unittest

class TestRoadWidens(unittest.TestCase):
    def test_unclass_to_tert(self):
        G = map_graph("test/osm-data/road_widens/unclass_to_tert.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_resd_to_tert(self):
        G = map_graph("test/osm-data/road_widens/resd_to_tert.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_unclass_1_to_2_lanes(self):
        G = map_graph("test/osm-data/road_widens/unclass_1_to_2_lanes.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_unclass_2_lanes(self):
        G = map_graph("test/osm-data/road_widens/unclass_2_lanes.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_resd_1_to_2_lanes_a(self):
        G = map_graph("test/osm-data/road_widens/resd_1_to_2_lanes_a.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_resd_1_to_2_lanes_b(self):
        G = map_graph("test/osm-data/road_widens/resd_1_to_2_lanes_b.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 1

    def test_resd_1_lane(self):
        G = map_graph("test/osm-data/road_widens/resd_1_lane.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 0

    def test_tert_1_to_2_lanes(self):
        G = map_graph("test/osm-data/road_widens/tert_1_to_2_lanes.osm")

        alert_points = []
        for node in G.nodes:
            alert_points.extend(RoadWidens.alert_locations(G, node))

        assert len(alert_points) == 0
