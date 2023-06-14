import osmnx as ox
import geopy.distance

class Alert():

    def __init__(self, id, alert_type, node, latitude, longitude, bearing):
        self.id = id
        self.type = alert_type
        self.node = node
        self.latitude = latitude
        self.longitude = longitude
        self.bearing = bearing


def map_graph(filename):
    """
    Import osm data into a networkx graph

    Arguments:
        filename: String the name of the file containing the OSM data

    Returns:
        networkx.MultiDiGraph graph
    """

    G = ox.graph_from_xml(f"{filename}", simplify=False)
    G = ox.bearing.add_edge_bearings(G, precision=5)
    return G


def sort_node_roads(node, G):
    # Empty lists to store the roads connected to the node in question
    roads_single_lane = []
    roads_multi_lanes = []

    # Checking each road

    for road in G.edges(node, data=True):

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


def check_node_danger(node, G):
    """
    Check to see if a node is a danger point

    Arguments:
        node: The node which we want to check
        G: The graph which we using to check

    Returns:
        boolean: True if the node is a danger point
    """

    road_lanes = sort_node_roads(node, G)

    # If we have roads with 1 lane and other roads we more than 1 lane then this node is a danger node
    if len(road_lanes["single"]) > 0 and len(road_lanes["multi"]) > 0:
        return True

    return False


def find_alert_location(G, node, road, distance):
    """
    Calculates the alert location for a node along a given road and distance

    Arguments:
        G: The graph which we using
        node: The node which we want to calcutale the alert points for
        road: The road which we will be looking for the alert location
        distance: The distance along the road we are going from the node

    Returns:
        A dictionary item for the alert location with the following keys:
            id: A ID for the alert point
            node: The node ID which the alert point is for
            x: longitude of the alert point
            y: latitude of the alert point
            bearing: The direction of travel which the alert point is should activate
    """

    distance_remaining = distance
    current_road = road
    current_node = node

    while distance_remaining > 0:

        # Check to see if the length of the road piece is long enough for the remaining distance
        if current_road[2]["length"] >= distance_remaining:
            node_obj = G.nodes[current_node]
            start = geopy.Point(node_obj["y"], node_obj["x"])
            alert_location = geopy.distance.distance(meters=distance_remaining).destination(
                point=start,
                bearing=float(current_road[2]["bearing"])
            )

            return Alert(
                id = f"{node}{int(road[2]['bearing'])}",
                alert_type = 1,
                node = node,
                latitude = alert_location.longitude,
                longitude = alert_location.latitude,
                bearing = (current_road[2]["bearing"] - 180) % 360
            )

        else:
            distance_remaining -= current_road[2]["length"]
            if current_node == current_road[0]:
                next_node = current_road[1]
            else:
                next_node = current_road[0]

            next_node_roads = G.edges(next_node, data=True)

            # Check to see if the next node is continuation of the road or a junction
            if len(next_node_roads) == 2:
                for potential_road in next_node_roads:
                    if current_node not in potential_road:
                        current_road = potential_road
                current_node = next_node

            else:
                # If the next node is a junction set the alert location halfway along the current road section
                node_obj = G.nodes[current_node]
                start = geopy.Point(node_obj["y"], node_obj["x"])
                alert_location = geopy.distance.distance(meters=current_road[2]["length"]/2).destination(
                    point=start,
                    bearing=float(current_road[2]["bearing"])
                )

                return Alert(
                    id = f"{node}{int(road[2]['bearing'])}",
                    alert_type = 1,
                    node = node,
                    latitude = alert_location.longitude,
                    longitude = alert_location.latitude,
                    bearing = (current_road[2]["bearing"] - 180) % 360
                )


def node_alert_points(node, G):
    """
    Calculates the location for the alert points for a danger node

    Arguments:
        node: The node which we want to calcutale the alert points for
        G: The graph which we using

    Returns:
        A list containing a dictonary with the following keys:
            id: A ID for the alert point
            node: The node ID which the alert point is for
            x: longitude of the alert point
            y: latitude of the alert point
            bearing: The direction of travel which the alert point is should activate
    """

    # Find roads we need to look for alert points on
    roads = sort_node_roads(node, G)["single"]

    alert_points = []

    for road in roads:
        if alert := find_alert_location(G, node, road, 50):
            alert_points.append(alert)

    return alert_points


if __name__ == "__main__":
    G = map_graph("silchester_filtered.osm")
    print(f"Total Nodes: {len(list(G.nodes))}")

    danger_nodes = [node for node in G.nodes if check_node_danger(node, G)]
    print(f"Number of danger nodes: {len(danger_nodes)}")

    alert_locations = []
    for node in danger_nodes:
        alert_locations.extend(node_alert_points(node, G))
    print(f"Number of alert locations: {len(alert_locations)}")
