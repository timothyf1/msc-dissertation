from alert_types.base import AlertType

class RoadWidens(AlertType):

    @staticmethod
    def alert_locations(G, node):
        roads = G.edges(node, data=True)

        if len(roads) == 2:
            road_lanes = AlertType.sort_roads(roads)

            if len(road_lanes["single"]) == 1 and len(road_lanes["multi"]) == 1:
                location = AlertType.find_alert_location(G, node, road_lanes["single"][0], 50)
                return [AlertType.create_alert(1, node, location)]

        return []
