from abc import ABC, abstractmethod
import geopy.distance


class Alert():

    def __init__(self, id, alert_type, node, latitude, longitude, bearing):
        self.id = id
        self.type = alert_type
        self.node = node
        self.latitude = latitude
        self.longitude = longitude
        self.bearing = bearing


class AlertType(ABC):

    @staticmethod
    @abstractmethod
    def alert_locations(G, node):
        pass

    @staticmethod
    def create_alert(alert_type, node, location):
        return Alert(
            id = f"{node}{int(location['bearing'])}",
            alert_type = alert_type,
            node = node,
            latitude = location["latitude"],
            longitude = location["longitude"],
            bearing = location["bearing"]
        )

    @staticmethod
    def sort_roads(roads):
        # Empty lists to store the roads connected to the node in question
        roads_single_lane = []
        roads_multi_lanes = []

        # Checking each road

        for road in roads:

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


    @staticmethod
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
                return {
                    "latitude" : alert_location.latitude,
                    "longitude" : alert_location.longitude,
                    "bearing" : (current_road[2]["bearing"] - 180) % 360
                }

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
                    return {
                        "latitude" : alert_location.latitude,
                        "longitude" : alert_location.longitude,
                        "bearing" : (current_road[2]["bearing"] - 180) % 360
                    }
