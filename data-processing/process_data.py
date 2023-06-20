import osmnx as ox
import json
import sys
from networkx import MultiDiGraph
from progressbar import progressbar

from alert_types import alerts_types

class Graph(MultiDiGraph):
    """
    Class inherits MultiDiGraph and adds additional methods
    """

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


def map_graph(filename):
    """
    Import osm data into a networkx graph

    Arguments:
        filename: String the name of the file containing the OSM data

    Returns:
        networkx.MultiDiGraph graph
    """

    G = ox.graph_from_xml(f"{filename}", simplify=False, retain_all=True)
    G = ox.bearing.add_edge_bearings(G, precision=5)
    G.__class__ = Graph
    return G


if __name__ == "__main__":

    input_file = sys.argv[1]

    print("Importing osm data to graph")
    G = map_graph(input_file)

    print(f"Total Nodes: {len(list(G.nodes()))}")

    alertpoints = G.all_alert_points()

    print(f"Number of alert locations: {len(alertpoints)}")

    if input_file[:2] == ".\\":
        input_file = input_file[2:]

    with open(f"alerts/alerts_{input_file[13:-4]}.json", "w") as f:
        json.dump(
            {
                "area" : input_file[14:-4],
                "alerts" : alertpoints
            },
            f,
            default=vars
        )
    print(f"Alert locations saved to alerts/alerts_{input_file[13:-4]}.json")

    if "debug-file" in sys.argv:
        for alert in alertpoints:
            G.add_node(alert.id, x=alert.longitude, y=alert.latitude, bearing=alert.bearing)

        ox.save_graph_xml(G, filepath=f"osm-with-alerts/{input_file[13:-4]}-alerts.osm")
