package task5;

import java.util.*;

/**
 * Task 5 – Dijkstra's Shortest-Path Algorithm
 * Scottish Road Network: Edinburgh → Dundee
 *
 * Road distances (miles, from the textbook map):
 *   Edinburgh  — Kirkcaldy  : 13
 *   Edinburgh  — Stirling   : 36
 *   Edinburgh  — Perth      : 44
 *   Stirling   — Perth      : 35
 *   Stirling   — Glasgow    : 28
 *   Perth      — Dundee     : 22
 *   Kirkcaldy  — Dundee     : 24
 */
public class DijkstraScotland {

    // ----------------------------------------------------------------
    // Weighted edge
    // ----------------------------------------------------------------
    static class Edge {
        final String from, to;
        final int weight;
        Edge(String from, String to, int weight) {
            this.from = from; this.to = to; this.weight = weight;
        }
        @Override public String toString() {
            return from + " --" + weight + "--> " + to;
        }
    }

    // ----------------------------------------------------------------
    // Weighted undirected graph
    // ----------------------------------------------------------------
    static class WeightedGraph {
        private final Map<String, List<Edge>> adj = new LinkedHashMap<>();

        void addVertex(String v) {
            adj.putIfAbsent(v, new ArrayList<>());
        }

        void addEdge(String u, String v, int w) {
            adj.get(u).add(new Edge(u, v, w));
            adj.get(v).add(new Edge(v, u, w));   // undirected
        }

        List<Edge> adj(String v) { return adj.get(v); }
        Set<String> vertices()   { return adj.keySet(); }
    }

    // ----------------------------------------------------------------
    // Dijkstra's algorithm
    // ----------------------------------------------------------------
    static class Dijkstra {
        private final Map<String, Integer> distTo = new LinkedHashMap<>();
        private final Map<String, String>  edgeTo = new LinkedHashMap<>();
        private final Set<String> settled = new LinkedHashSet<>();
        private final String source;

        Dijkstra(WeightedGraph g, String source) {
            this.source = source;

            // Initialise all distances to infinity
            for (String v : g.vertices())
                distTo.put(v, Integer.MAX_VALUE);
            distTo.put(source, 0);

            // Min-priority queue: (vertex, dist)
            PriorityQueue<String> pq = new PriorityQueue<>(
                    Comparator.comparingInt(distTo::get));
            pq.offer(source);

            System.out.println("=== Dijkstra Trace: source = " + source + " ===\n");
            System.out.printf("%-15s %-10s %-20s%n", "Settled Node", "dist", "Updated neighbours");
            System.out.println("-".repeat(55));

            while (!pq.isEmpty()) {
                String v = pq.poll();
                if (settled.contains(v)) continue;   // stale entry
                settled.add(v);

                StringBuilder updates = new StringBuilder();

                for (Edge e : g.adj(v)) {
                    String w = e.to;
                    if (settled.contains(w)) continue;

                    int newDist = distTo.get(v) + e.weight;
                    if (newDist < distTo.get(w)) {
                        distTo.put(w, newDist);
                        edgeTo.put(w, v);
                        pq.offer(w);   // lazy deletion: re-add with updated priority
                        updates.append(w).append("=").append(newDist).append("  ");
                    }
                }

                System.out.printf("%-15s %-10d %-20s%n",
                        v, distTo.get(v),
                        updates.length() == 0 ? "(none)" : updates.toString().trim());
            }
            System.out.println();
        }

        int distTo(String v)  { return distTo.getOrDefault(v, Integer.MAX_VALUE); }
        boolean hasPathTo(String v) { return distTo.get(v) < Integer.MAX_VALUE; }

        List<String> pathTo(String v) {
            if (!hasPathTo(v)) return null;
            Deque<String> path = new ArrayDeque<>();
            for (String x = v; !x.equals(source); x = edgeTo.get(x))
                path.push(x);
            path.push(source);
            return new ArrayList<>(path);
        }

        void printAllDistances() {
            System.out.println("=== Final shortest distances from " + source + " ===");
            System.out.printf("%-15s %s%n", "City", "Distance (miles)");
            System.out.println("-".repeat(35));
            for (Map.Entry<String, Integer> e : distTo.entrySet()) {
                int d = e.getValue();
                System.out.printf("%-15s %s%n", e.getKey(),
                        d == Integer.MAX_VALUE ? "unreachable" : d);
            }
            System.out.println();
        }
    }

    // ----------------------------------------------------------------
    // Build the Scottish road network
    // ----------------------------------------------------------------
    static WeightedGraph buildScottishRoadNetwork() {
        WeightedGraph g = new WeightedGraph();
        for (String city : new String[]{
                "Edinburgh","Kirkcaldy","Stirling","Perth","Glasgow","Dundee"})
            g.addVertex(city);

        g.addEdge("Edinburgh", "Kirkcaldy", 13);
        g.addEdge("Edinburgh", "Stirling",  36);
        g.addEdge("Edinburgh", "Perth",     44);
        g.addEdge("Stirling",  "Perth",     35);
        g.addEdge("Stirling",  "Glasgow",   28);
        g.addEdge("Perth",     "Dundee",    22);
        g.addEdge("Kirkcaldy", "Dundee",    24);

        return g;
    }

    // ----------------------------------------------------------------
    // Main
    // ----------------------------------------------------------------
    public static void main(String[] args) {
        WeightedGraph g = buildScottishRoadNetwork();
        String source      = "Edinburgh";
        String destination = "Dundee";

        Dijkstra dijkstra = new Dijkstra(g, source);
        dijkstra.printAllDistances();

        // Print shortest path to Dundee
        List<String> path = dijkstra.pathTo(destination);
        System.out.println("=== Shortest path: " + source + " → " + destination + " ===");
        if (path == null) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path   : " + String.join(" → ", path));
            System.out.println("Distance: " + dijkstra.distTo(destination) + " miles");
        }

        // Compare alternative route via Perth for illustration
        System.out.println();
        System.out.println("--- Alternative route comparison ---");
        System.out.println("Via Kirkcaldy : Edinburgh(0) → Kirkcaldy(13) → Dundee(37) = 37 miles ✓ SHORTEST");
        System.out.println("Via Perth     : Edinburgh(0) → Perth(44)     → Dundee(66) = 66 miles");
    }
}