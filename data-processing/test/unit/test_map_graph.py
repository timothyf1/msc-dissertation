from graph import Graph
import unittest

class TestMapGraph(unittest.TestCase):

    def test_input_graph(self):
        G = Graph.create_map_graph("test/osm-data/graph/map.osm")

        assert len(G.nodes) == 35
        assert len(G.edges) == 74
