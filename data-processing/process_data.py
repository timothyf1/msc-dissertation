import json
import sys
import os
import pickle

from graph import Graph


def find_alert_points(filename, dump=False, debug_file=False):
    """
    Main function

    Arguments:
        filename: The filename of the osm file which alert points are calculated for
        dump (optional): Dump the Graph data to a pickle file to speed up load time
        dump_file (optional): Option to save the alert points to an osm file with map data.
    """

    if os.path.isfile(f"dump/{input_file[13:-4]}.pckl"):
        print("Using existing pickle dump")
        with open(f"dump/{input_file[13:-4]}.pckl", "rb") as f:
            G = pickle.load(f)
    else:
        print("Importing osm data to graph")
    G = Graph.create_map_graph(input_file)

    print(f"Total Nodes: {len(list(G.nodes()))}")

    alertpoints = G.all_alert_points()

    print(f"Number of alert locations: {len(alertpoints)}")

    os.mkdir("alerts")

    with open(f"alerts/alerts_{input_file[13:-4]}.json", "w") as f:
        json.dump(
            {
                "area" : input_file[13:-4],
                "driving_left" : True,
                "alerts" : alertpoints
            },
            f,
            default=vars
        )
    print(f"Alert locations saved to alerts/alerts_{input_file[13:-4]}.json")

    if debug_file:
        for alert in alertpoints:
            G.add_node(alert.id, x=alert.longitude, y=alert.latitude, bearing=alert.bearing)

        ox.save_graph_xml(G, filepath=f"osm-with-alerts/{input_file[13:-4]}-alerts.osm")

    if dump:
        with open(f"dump/{input_file[13:-4]}.pckl", "wb") as f:
            pickle.dump(G, f)


if __name__ == "__main__":

    input_file = sys.argv[1]

    if input_file[:2] == ".\\":
        input_file = input_file[2:]

    dump = True if "dump" in sys.argv else False
    debug_file = True if "debug-file" in sys.argv else False

    find_alert_points(input_file, dump=dump, debug_file=debug_file)
