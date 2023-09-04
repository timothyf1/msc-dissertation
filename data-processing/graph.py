import osmnx as ox
from networkx import MultiDiGraph
from progressbar import progressbar

from alert_types import alerts_types

class Graph(MultiDiGraph):
    """
    Class inherits MultiDiGraph and adds additional methods
    """

    ignore_highways = ["living_street", "pedestrian", "service"]

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
        edges = list(self.out_roads(node))

        adjacent_nodes = [edge[1] for edge in edges]
        for edge in self.in_roads(node):
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

    def in_roads(self, node):
        """
        Method to find incomming roads to a node that will be processed

        Arguments
            node: The node we are checking for incomming roads

        Returns
            list: Containing truples for the incomming roads
        """
        roads = self.in_edges(node, data=True)
        roads = [road for road in roads if road[2].get("highway") not in self.ignore_highways]

        return roads

    def out_roads(self, node):
        """
        Method to find outgoining roads to a node that will be processed

        Arguments
            node: The node we are checking for outgoining roads

        Returns
            list: Containing truples for the outgoining roads
        """
        roads = self.out_edges(node, data=True)
        roads = [road for road in roads if road[2].get("highway") not in self.ignore_highways]

        return roads

    def sort_roads(self, node, incomming):
        """
        This method will find and sort the roads at a given node into 2 categories - single lane or muilt lane

        Argurments:
            node: The node we are sorting the roads for
            incomming: True if we are checking incomming roads, false for checking outgoing roads

        Returns:
            dictionay: Containing two keys as follows
                single: For the roads with a single lane
                muilt:: For the roads with 2 or more lanes
        """

        roads = self.in_roads(node) if incomming else self.out_roads(node)

        # Empty lists to store the roads connected to the node in question
        roads_single_lane = []
        roads_multi_lanes = []

        # Checking each road

        for road in roads:
            if road[2].get("highway") in ["living_street", "pedestrian", "service"]:
                continue

            # If the road has a lane attribute we will use this number
            if lanes := road[2].get("lanes"):
                if lanes == "1":
                    roads_single_lane.append(road)
                else:
                    roads_multi_lanes.append(road)

            # Otherwise we will use the type of road
            else:
                if road[2].get("highway") in ["unclassified"]:
                    roads_single_lane.append(road)
                else:
                    roads_multi_lanes.append(road)

        return {
            "single" : roads_single_lane,
            "multi" : roads_multi_lanes
        }
