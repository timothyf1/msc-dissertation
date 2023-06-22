from process_data import map_graph, Graph
import unittest

class TestGraph(unittest.TestCase):

    def test_node_num_of_roads(self):
        G = map_graph("test/osm-data/graph/map.osm")

        assert G.node_num_of_roads(-101752) == 2
        assert G.node_num_of_roads(-101753) == 4
        assert G.node_num_of_roads(-101754) == 2
        assert G.node_num_of_roads(-101755) == 3
        assert G.node_num_of_roads(-101756) == 1
        assert G.node_num_of_roads(-101757) == 1
        assert G.node_num_of_roads(-101758) == 2
        assert G.node_num_of_roads(-101759) == 2
        assert G.node_num_of_roads(-101760) == 3
        assert G.node_num_of_roads(-101761) == 1
        assert G.node_num_of_roads(-101762) == 2
        assert G.node_num_of_roads(-101763) == 3
        assert G.node_num_of_roads(-101764) == 2
        assert G.node_num_of_roads(-101765) == 2
        assert G.node_num_of_roads(-101766) == 2
        assert G.node_num_of_roads(-101767) == 2
        assert G.node_num_of_roads(-101768) == 3
        assert G.node_num_of_roads(-101769) == 2
        assert G.node_num_of_roads(-101770) == 1
        assert G.node_num_of_roads(-101771) == 1
        assert G.node_num_of_roads(-101800) == 3
        assert G.node_num_of_roads(-101801) == 1
        assert G.node_num_of_roads(-101802) == 3
        assert G.node_num_of_roads(-101803) == 2
        assert G.node_num_of_roads(-101804) == 2
        assert G.node_num_of_roads(-101805) == 2
        assert G.node_num_of_roads(-101823) == 1
        assert G.node_num_of_roads(-101824) == 2
        assert G.node_num_of_roads(-101825) == 3
        assert G.node_num_of_roads(-101826) == 3
        assert G.node_num_of_roads(-101827) == 1
        assert G.node_num_of_roads(-101828) == 1
        assert G.node_num_of_roads(-101829) == 1

    def test_node_part_of_roundabout(self):
        G = map_graph("test/osm-data/graph/map_roundabout.osm")

        assert G.node_part_of_roundabout(-101905) == True
        assert G.node_part_of_roundabout(-101906) == True
        assert G.node_part_of_roundabout(-101907) == True
        assert G.node_part_of_roundabout(-101908) == True
        assert G.node_part_of_roundabout(-101909) == True
        assert G.node_part_of_roundabout(-101910) == True
        assert G.node_part_of_roundabout(-101911) == True
        assert G.node_part_of_roundabout(-101912) == True
        assert G.node_part_of_roundabout(-101913) == True
        assert G.node_part_of_roundabout(-101914) == True
        assert G.node_part_of_roundabout(-101915) == True
        assert G.node_part_of_roundabout(-101916) == True
        assert G.node_part_of_roundabout(-101917) == False
        assert G.node_part_of_roundabout(-101918) == False
        assert G.node_part_of_roundabout(-101919) == False
        assert G.node_part_of_roundabout(-101920) == False
        assert G.node_part_of_roundabout(-101921) == False
        assert G.node_part_of_roundabout(-101922) == False
        assert G.node_part_of_roundabout(-101923) == False
        assert G.node_part_of_roundabout(-101924) == False
        assert G.node_part_of_roundabout(-101925) == False
        assert G.node_part_of_roundabout(-101926) == False
        assert G.node_part_of_roundabout(-101927) == True
        assert G.node_part_of_roundabout(-101928) == False
        assert G.node_part_of_roundabout(-101929) == False
        assert G.node_part_of_roundabout(-101930) == False
        assert G.node_part_of_roundabout(-101952) == True
        assert G.node_part_of_roundabout(-101953) == True
        assert G.node_part_of_roundabout(-101954) == True
        assert G.node_part_of_roundabout(-101955) == True
        assert G.node_part_of_roundabout(-101956) == True
        assert G.node_part_of_roundabout(-101957) == True
        assert G.node_part_of_roundabout(-101958) == True
        assert G.node_part_of_roundabout(-101959) == True
        assert G.node_part_of_roundabout(-101960) == True
        assert G.node_part_of_roundabout(-101961) == True
        assert G.node_part_of_roundabout(-101962) == True
        assert G.node_part_of_roundabout(-101967) == True
        assert G.node_part_of_roundabout(-101969) == False
        assert G.node_part_of_roundabout(-101970) == False
        assert G.node_part_of_roundabout(-101971) == False
        assert G.node_part_of_roundabout(-101972) == False
        assert G.node_part_of_roundabout(-101973) == True
        assert G.node_part_of_roundabout(-101974) == False
        assert G.node_part_of_roundabout(-101975) == False
        assert G.node_part_of_roundabout(-101976) == True
        assert G.node_part_of_roundabout(-101977) == False
        assert G.node_part_of_roundabout(-101978) == True
        assert G.node_part_of_roundabout(-101979) == False
        assert G.node_part_of_roundabout(-101980) == False
        assert G.node_part_of_roundabout(-101981) == True

    def test_node_alert_points_1(self):
        G = map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        node = -102069

        alert_points = G.node_alert_points(node)

        assert len(alert_points) == 2

    def test_node_alert_points_2(self):
        G = map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        node = -102067

        alert_points = G.node_alert_points(node)

        assert len(alert_points) == 2

    def test_node_alert_points_2(self):
        G = map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")
        node = -102068

        alert_points = G.node_alert_points(node)

        assert len(alert_points) == 0

    def test_all_alert_points(self):
        G = map_graph("test/osm-data/graph/map_2_roads_with_lanes_att.osm")

        alert_points = G.all_alert_points()

        assert len(alert_points) == 4
