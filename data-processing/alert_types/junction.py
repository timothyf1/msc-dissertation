from alert_types.base import AlertType

class Junction(AlertType):

    @staticmethod
    def alert_locations(G, node):
        roads = G.edges(node, data=True)

        if len(roads) <= 2:
            return []

        road_lanes = AlertType.sort_roads(roads)

        if len(road_lanes["single"]) == 0 or len(road_lanes["multi"]) == 0:
            return []

        alerts_points = []
        for road in road_lanes["single"]:
            location = AlertType.find_alert_location(G, node, road, 50)
            alerts_points.append(AlertType.create_alert(2, node, location))
        return alerts_points
