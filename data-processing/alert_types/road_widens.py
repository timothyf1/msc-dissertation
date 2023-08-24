from alert_types.base import AlertType

class RoadWidens(AlertType):

    alert_type = 10

    def alert_locations(self, G, node):
        if G.node_num_of_roads(node) == 2:
            in_roads = G.in_edges(node, data=True)
            in_road_lanes = self.sort_roads(in_roads)

            out_roads = G.out_edges(node, data=True)
            out_road_lanes = self.sort_roads(out_roads)

            if len(in_road_lanes["single"]) > 0 and len(out_road_lanes["multi"]) > 0:
                location = self.find_alert_location(G, node, in_road_lanes["single"][0], 50)
                return [self.create_alert(node, location)]

        return []
