from process_data import map_graph
import unittest

class TestMapGraph(unittest.TestCase):

    def test_input_graph(self):
        G = map_graph("test/osm-data/graph/map.osm")

        assert len(G.nodes) == 33
        assert len(G.edges) == 64
