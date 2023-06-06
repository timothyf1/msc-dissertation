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


if __name__ == "__main__":
    G = map_graph("silchester_filtered.osm")

    print(f"Total Nodes: {len(list(G.nodes))}")
