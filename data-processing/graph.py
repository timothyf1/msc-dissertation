import osmnx as ox
from networkx import MultiDiGraph
from progressbar import progressbar

from alert_types import alerts_types

class Graph(MultiDiGraph):
    """
    Class inherits MultiDiGraph and adds additional methods
    """

    @staticmethod
    def create_map_graph(filename):
        """
        Import osm data into a networkx graph

        Arguments:
            filename: String the name of the file containing the OSM data

        Returns:
            Graph
        """
        G = ox.graph_from_xml(f"{filename}", simplify=False, retain_all=True)
        G = ox.bearing.add_edge_bearings(G, precision=5)
        G.__class__ = Graph
        return G


    def node_alert_points(self, node):
        """
        Calculate the alert points for a given node

        Arguments:
            node: The node we wish to calculate the alert point for

        Returns:
            list: A list contain alert objects for each alert point
        """

        alert_points = []

        for alert_type in alerts_types:
            alert_points.extend(alert_type.alert_locations(self, node))

        return alert_points

    def all_alert_points(self):
        """
        Calculate the alert points for all nodes

        Returns:
            list: A list contain alert objects for each alert point
        """

        alert_points = []

        for node in progressbar(self.nodes):
            alert_points.extend(self.node_alert_points(node))

        return alert_points

    def node_num_of_roads(self, node):
        """
        Method to calculate the number of roads connected to a node

        Arguments
            node: The node we wish to check

        Returns
            int: The number of connected roads
        """
        edges = list(self.out_edges(node))

        adjacent_nodes = [edge[1] for edge in edges]
        for edge in self.in_edges(node):
            if edge[0] not in adjacent_nodes:
                adjacent_nodes.append(edge)

        return len(adjacent_nodes)

    def node_part_of_roundabout(self, node):
        """
        Check to see if the node is part of a roundabout

        Arguments
            node: The node we wish to check

        Returns
            boolean: True if the node is part of a roundabout
        """
        roads = self.edges(node, data=True)

        for road in roads:
            if road[2].get("junction") in ["roundabout", "circular"]:
                return True
        return False
