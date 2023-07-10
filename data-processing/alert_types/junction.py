from alert_types.base import AlertType

class Junction(AlertType):

    @staticmethod
    def alert_locations(G, node):

        # Check num of roads connected to node
        if G.node_num_of_roads(node) <= 2:
            return []

        # Check if node is part of a roundabout
        if G.node_part_of_roundabout(node):
            return []

        in_roads = G.in_edges(node, data=True)
        in_road_lanes = AlertType.sort_roads(in_roads)

        out_roads = G.in_edges(node, data=True)
        out_road_lanes = AlertType.sort_roads(out_roads)

        # Check for any incomming single-track roads
        if len(in_road_lanes["single"]) == 0:
            return []

        # Check for any outgoing roads which
        if len(out_road_lanes["multi"]) == 0:
            return []

        alerts_points = []
        for road in in_road_lanes["single"]:
            location = AlertType.find_alert_location(G, node, road, 50)
            alerts_points.append(AlertType.create_alert(20, node, location))
        return alerts_points
