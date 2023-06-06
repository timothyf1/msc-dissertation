import osmnx as ox


def map_graph(filename):
    """
    Import osm data into a networkx graph

    Arguments:
        filename: String the name of the file containing the OSM data

    Returns:
        networkx.MultiDiGraph graph
    """

    G = ox.graph_from_xml(f"{filename}", simplify=False)
    G = ox.bearing.add_edge_bearings(G)
    return G


def check_node_danger(node, G):
    """
    Check to see if a node is a danger point

    Arguments:
        node: The node which we want to check
        G: The graph which we using to check

    Returns:
        boolean: True if the node is a danger point
    """

    # Empty lists to store the roads connected to the node in question
    roads_with_1_lane = []
    roads_with_more_than_1_lane = []

    # Checking each node
    for edge in G.edges(node, data=True):

        # If the road has a lane attribute we will use this number
        if lanes := edge[2].get("lanes"):
            if lanes == "1":
                roads_with_1_lane.append(edge)
            else:
                roads_with_more_than_1_lane.append(edge)

        # Otherwise we will use the type of road
        else:
            if edge[2].get("highway") in ["unclassified"]:
                roads_with_1_lane.append(edge)
            else:
                roads_with_more_than_1_lane.append(edge)

    # If we have roads with 1 lane and other roads we more than 1 lane then this node is a danger node
    if len(roads_with_1_lane) > 0 and len(roads_with_more_than_1_lane) > 0:
        return True

    return False


if __name__ == "__main__":
    G = map_graph("silchester_filtered.osm")
    print(f"Total Nodes: {len(list(G.nodes))}")

    danger_nodes = [node for node in G.nodes if check_node_danger(node, G)]
    print(f"Number of danger nodes: {len(danger_nodes)}")
