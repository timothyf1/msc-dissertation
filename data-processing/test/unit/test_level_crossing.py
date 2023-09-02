from alert_types.level_crossing import LevelCrossing
from graph import Graph
import unittest

class TestLevelCrossing(unittest.TestCase):
    def test_level_crossing_two_roads_single_track(self):
        G = Graph.create_map_graph("test/osm-data/level_crossing/map_level_crossing_single_track.osm")
        level_crossing = LevelCrossing()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(level_crossing.alert_locations(G, node))

        assert len(alert_points) == 4

    def test_level_crossing_two_roads_double_track(self):
        G = Graph.create_map_graph("test/osm-data/level_crossing/map_level_crossing_double_track.osm")
        level_crossing = LevelCrossing()

        alert_points = []
        for node in G.nodes:
            alert_points.extend(level_crossing.alert_locations(G, node))

        assert len(alert_points) == 4
