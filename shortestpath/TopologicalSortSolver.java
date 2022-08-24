package graphs.shortestpaths;

import graphs.Edge;
import graphs.Graph;

import java.util.*;

/**
 *
 * @param <V> the type of vertices.
 * 
 */
public class ToposortDAGSolver<V> {
    private final Map<V, Edge<V>> edgeTo;
    private final Map<V, Double> distTo;

    /**
     * Constructs a new instance by executing the toposort-DAG-shortest-paths algorithm on the graph from the start.
     *
     * @param graph the input graph.
     * @param start the start vertex.
     */
    public ToposortSolver(Graph<V> graph, V start) {
        this.edgeTo = new HashMap<>();
        this.distTo = new HashMap<>();
        List<V> result = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        dfsPostOrder(graph, start, visited, result);

        Collections.reverse(result);
        this.edgeTo.put(start, null);
        this.distTo.put(start, 0.0);

        while(!result.isEmpty()){
            V from = result.remove(0);
            for(Edge<V> edge: graph.neighbors(from)) {
                V to = edge.to;
                double oldDist = distTo.getOrDefault(to, Double.POSITIVE_INFINITY);
                double newDist = distTo.get(from) + edge.weight;
                if (newDist < oldDist){
                    edgeTo.put(to, edge);
                    distTo.put(to, newDist);
                }

            }
        }
        
    }

    /**
     * Recursively adds nodes from the graph to the result in DFS postorder from the start vertex.
     *
     * @param graph   the input graph.
     * @param start   the start vertex.
     * @param visited the set of visited vertices.
     * @param result  the destination for adding nodes.
     */
    private void dfsPostOrder(Graph<V> graph, V start, Set<V> visited, List<V> result) {
        visited.add(start);
        for (Edge<V> neighbor : graph.neighbors(start)) {
            if (!visited.contains(neighbor.to)) {
                dfsPostOrder(graph, neighbor.to, visited, result);
            }
        }
        result.add(start);
    }

    @Override
    public List<V> solution(V goal) {
        List<V> path = new ArrayList<>();
        V curr = goal;
        path.add(curr);
        while (edgeTo.get(curr) != null) {
            curr = edgeTo.get(curr).from;
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
