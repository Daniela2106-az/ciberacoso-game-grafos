package model.graph;

import java.util.*;

public class Graph {
    private Map<String, Node> nodes;           // id → Node
    private Map<String, List<Edge>> adjList;   // id → lista de aristas
    private boolean directed;

    public Graph(boolean directed) {
        this.directed = directed;
        this.nodes = new LinkedHashMap<>();
        this.adjList = new LinkedHashMap<>();
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(String sourceId, String targetId, int weight) {
        addEdge(sourceId, targetId, weight, "");
    }

    public void addEdge(String sourceId, String targetId, int weight, String message) {
        Node source = nodes.get(sourceId);
        Node target = nodes.get(targetId);
        if (source == null || target == null) return;

        Edge edge = new Edge(source, target, weight, directed, message);
        adjList.get(sourceId).add(edge);

        if (!directed) {
            Edge reverse = new Edge(target, source, weight, false, message);
            adjList.get(targetId).add(reverse);
        }
    }


    public Node getNode(String id) { return nodes.get(id); }
    public Collection<Node> getAllNodes() { return nodes.values(); }
    public List<Edge> getEdges(String nodeId) {
        return adjList.getOrDefault(nodeId, new ArrayList<>());
    }

    public List<Edge> getAllEdges() {
        Set<String> seen = new HashSet<>();
        List<Edge> result = new ArrayList<>();
        for (List<Edge> edges : adjList.values()) {
            for (Edge e : edges) {
                String key = e.getSource().getId() + "-" + e.getTarget().getId();
                String rev = e.getTarget().getId() + "-" + e.getSource().getId();
                if (!seen.contains(key) && !seen.contains(rev)) {
                    result.add(e);
                    seen.add(key);
                    seen.add(rev);
                }
            }
        }
        return result;
    }

    public void resetVisited() {
        for (Node n : nodes.values()) n.setVisited(false);
    }

    public boolean isDirected() { return directed; }
    public Map<String, List<Edge>> getAdjList() { return adjList; }
    public Map<String, Node> getNodes() { return nodes; }
}