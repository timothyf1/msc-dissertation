from alert_types.base import AlertType

class RoadWidens(AlertType):

    @staticmethod
    def alert_locations(G, node):
        if G.node_num_of_roads(node) == 2:
            in_roads = G.in_edges(node, data=True)
            in_road_lanes = AlertType.sort_roads(in_roads)

            out_roads = G.out_edges(node, data=True)
            out_road_lanes = AlertType.sort_roads(out_roads)

            if len(in_road_lanes["single"]) > 0 and len(out_road_lanes["multi"]) > 0:
                location = AlertType.find_alert_location(G, node, in_road_lanes["single"][0], 50)
                return [AlertType.create_alert(1, node, location)]

        return []
