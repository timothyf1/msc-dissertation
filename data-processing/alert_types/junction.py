from alert_types.base import AlertType

class Junction(AlertType):

    alert_type = 20

    def alert_locations(self, G, node):

        # Check num of roads connected to node
        if G.node_num_of_roads(node) <= 2:
            return []

        # Check if node is part of a roundabout
        if G.node_part_of_roundabout(node):
            return []

        in_road_lanes = G.sort_roads(node, incomming=True)

        out_road_lanes = G.sort_roads(node, incomming=False)

        # Check for any incomming single-track roads
        if len(in_road_lanes["single"]) == 0:
            return []

        # Check for any outgoing roads which
        if len(out_road_lanes["multi"]) == 0:
            return []

        alerts_points = []
        for road in in_road_lanes["single"]:
            location = self.find_alert_location(G, node, road)
            alerts_points.append(self.create_alert(node, location))
        return alerts_points
