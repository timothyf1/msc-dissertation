from alert_types.base import AlertType

class RoadWidens(AlertType):

    alert_type = 10

    def alert_locations(self, G, node):
        if G.node_num_of_roads(node) == 2:
            in_road_lanes = G.sort_roads(node, incomming=True)

            out_road_lanes = G.sort_roads(node, incomming=False)

            if len(in_road_lanes["single"]) > 0 and len(out_road_lanes["multi"]) > 0:
                location = self.find_alert_location(G, node, in_road_lanes["single"][0])
                return [self.create_alert(node, location)]

        return []
