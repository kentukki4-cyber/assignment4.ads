package task3;

import java.util.*;

/**
 * Task 3 – DFS and BFS implementation for the graph in Task 1.
 *
 * Graph (from Task 1):
 *   A: C B D
 *   B: A C E G
 *   C: A B D
 *   D: C A
 *   E: G F B
 *   F: G E
 *   G: F B
 *
 * The adjacency lists are built in the EXACT order given, so the
 * traversal order will match the hand-traced results in Task 1 and Task 2.
 */
public class GraphTraversal {

    // ----------------------------------------------------------------
    // Simple undirected graph using adjacency lists (LinkedList preserves
    // insertion order, which is critical for matching the trace).
    // ----------------------------------------------------------------
    static class Graph {
        private final Map<String, LinkedList<String>> adj = new LinkedHashMap<>();

        void addVertex(String v) {
            adj.putIfAbsent(v, new LinkedList<>());
        }

        // Add a directed edge v → w (we call this twice for undirected)
        void addEdge(String v, String w) {
            adj.get(v).add(w);
        }

        Iterable<String> adj(String v) {
            return adj.get(v);
        }

        Set<String> vertices() {
            return adj.keySet();
        }
    }

    // ----------------------------------------------------------------
    // Depth-First Search (Sedgewick & Wayne, p. 537)
    // ----------------------------------------------------------------
    static class DepthFirstSearch {
        private final Set<String> marked = new LinkedHashSet<>();
        private final Map<String, String> edgeTo = new LinkedHashMap<>();
        private final List<String> visitOrder = new ArrayList<>();
        private final String source;

        DepthFirstSearch(Graph g, String s) {
            this.source = s;
            dfs(g, s);
        }

        private void dfs(Graph g, String v) {
            marked.add(v);
            visitOrder.add(v);
            for (String w : g.adj(v)) {
                if (!marked.contains(w)) {
                    edgeTo.put(w, v);
                    dfs(g, w);
                }
            }
        }

        boolean hasPathTo(String v) { return marked.contains(v); }

        List<String> pathTo(String v) {
            if (!hasPathTo(v)) return null;
            Deque<String> path = new ArrayDeque<>();
            for (String x = v; !x.equals(source); x = edgeTo.get(x))
                path.push(x);
            path.push(source);
            return new ArrayList<>(path);
        }

        List<String> visitOrder() { return visitOrder; }
        Map<String, String> edgeTo()  { return edgeTo; }
    }

    // ----------------------------------------------------------------
    // Breadth-First Search (Sedgewick & Wayne, p. 539)
    // ----------------------------------------------------------------
    static class BreadthFirstSearch {
        private final Set<String> marked = new LinkedHashSet<>();
        private final Map<String, String> edgeTo = new LinkedHashMap<>();
        private final Map<String, Integer> distTo = new LinkedHashMap<>();
        private final List<String> visitOrder = new ArrayList<>();
        private final String source;

        BreadthFirstSearch(Graph g, String s) {
            this.source = s;
            bfs(g, s);
        }

        private void bfs(Graph g, String s) {
            Queue<String> queue = new LinkedList<>();
            marked.add(s);
            distTo.put(s, 0);
            visitOrder.add(s);
            queue.offer(s);

            while (!queue.isEmpty()) {
                String v = queue.poll();
                for (String w : g.adj(v)) {
                    if (!marked.contains(w)) {
                        marked.add(w);
                        edgeTo.put(w, v);
                        distTo.put(w, distTo.get(v) + 1);
                        visitOrder.add(w);
                        queue.offer(w);
                    }
                }
            }
        }

        boolean hasPathTo(String v) { return marked.contains(v); }
        int distTo(String v) { return distTo.getOrDefault(v, Integer.MAX_VALUE); }

        List<String> pathTo(String v) {
            if (!hasPathTo(v)) return null;
            Deque<String> path = new ArrayDeque<>();
            for (String x = v; !x.equals(source); x = edgeTo.get(x))
                path.push(x);
            path.push(source);
            return new ArrayList<>(path);
        }

        List<String> visitOrder() { return visitOrder; }
        Map<String, String> edgeTo()  { return edgeTo; }
        Map<String, Integer> distTo() { return distTo; }
    }

    // ----------------------------------------------------------------
    // Build the graph from Task 1
    // ----------------------------------------------------------------
    static Graph buildTask1Graph() {
        Graph g = new Graph();
        // Add vertices first so adjacency lists are in correct order
        for (String v : new String[]{"A","B","C","D","E","F","G"})
            g.addVertex(v);

        // Add edges in the exact adjacency-list order from the assignment:
        // A: C B D
        g.addEdge("A","C"); g.addEdge("A","B"); g.addEdge("A","D");
        // B: A C E G
        g.addEdge("B","A"); g.addEdge("B","C"); g.addEdge("B","E"); g.addEdge("B","G");
        // C: A B D
        g.addEdge("C","A"); g.addEdge("C","B"); g.addEdge("C","D");
        // D: C A
        g.addEdge("D","C"); g.addEdge("D","A");
        // E: G F B
        g.addEdge("E","G"); g.addEdge("E","F"); g.addEdge("E","B");
        // F: G E
        g.addEdge("F","G"); g.addEdge("F","E");
        // G: F B
        g.addEdge("G","F"); g.addEdge("G","B");

        return g;
    }

    // ----------------------------------------------------------------
    // Main
    // ----------------------------------------------------------------
    public static void main(String[] args) {
        Graph g = buildTask1Graph();
        String source = "A";

        // ---- DFS ----
        System.out.println("=== DEPTH-FIRST SEARCH from " + source + " ===");
        DepthFirstSearch dfs = new DepthFirstSearch(g, source);
        System.out.println("Visit order : " + String.join(" → ", dfs.visitOrder()));
        System.out.println("edgeTo map  : " + dfs.edgeTo());
        System.out.println();
        System.out.println("Paths from " + source + ":");
        for (String v : new String[]{"A","C","B","E","G","F","D"}) {
            List<String> path = dfs.pathTo(v);
            System.out.println("  " + source + " to " + v + ": " +
                    (path == null ? "no path" : String.join(" → ", path)));
        }

        System.out.println();

        // ---- BFS ----
        System.out.println("=== BREADTH-FIRST SEARCH from " + source + " ===");
        BreadthFirstSearch bfs = new BreadthFirstSearch(g, source);
        System.out.println("Visit order : " + String.join(" → ", bfs.visitOrder()));
        System.out.println("edgeTo map  : " + bfs.edgeTo());
        System.out.println();
        System.out.println("Shortest distances and paths from " + source + ":");
        for (String v : new String[]{"A","C","B","D","E","G","F"}) {
            List<String> path = bfs.pathTo(v);
            System.out.println("  " + source + " to " + v +
                    " (dist=" + bfs.distTo(v) + "): " +
                    (path == null ? "no path" : String.join(" → ", path)));
        }

        System.out.println();
        System.out.println("--- Comparison with hand-traced results ---");
        System.out.println("Expected DFS order: A → C → B → E → G → F → D");
        System.out.println("Actual   DFS order: " + String.join(" → ", dfs.visitOrder()));
        System.out.println();
        System.out.println("Expected BFS order: A → C → B → D → E → G → F");
        System.out.println("Actual   BFS order: " + String.join(" → ", bfs.visitOrder()));
        System.out.println();
        boolean dfsMatch = dfs.visitOrder().equals(Arrays.asList("A","C","B","E","G","F","D"));
        boolean bfsMatch = bfs.visitOrder().equals(Arrays.asList("A","C","B","D","E","G","F"));
        System.out.println("DFS match: " + (dfsMatch ? "✓ MATCH" : "✗ MISMATCH"));
        System.out.println("BFS match: " + (bfsMatch ? "✓ MATCH" : "✗ MISMATCH"));
    }
}