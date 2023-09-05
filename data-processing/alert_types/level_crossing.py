from alert_types.base import AlertType

class LevelCrossing(AlertType):

    alert_type = 30

    def alert_locations(self, G, node):

        alerts = []

        if G.node_level_crossing(node):
            for road in G.in_edges(node, data=True):

                # Check if next node is a level crossing incase of crossing
                # muiltple tracks only 1 warning is needed
                if G.node_level_crossing(road[0]):
                    continue

                location = self.find_alert_location(G, node, road)
                alerts.append(self.create_alert(node, location))

        return alerts
