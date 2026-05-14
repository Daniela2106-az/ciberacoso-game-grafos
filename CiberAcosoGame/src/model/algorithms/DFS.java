package model.algorithms;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;

import java.util.*;

public class DFS {
    private Graph graph;
    private List<Node> visitOrder;
    private List<String> stepLog;

    public DFS(Graph graph) {
        this.graph = graph;
        this.visitOrder = new ArrayList<>();
        this.stepLog = new ArrayList<>();
    }

    public void execute(String startId) {
        graph.resetVisited();
        visitOrder.clear();
        stepLog.clear();

        stepLog.add("🔍 Inicio DFS desde nodo: " + graph.getNode(startId).getName());
        dfsRecursive(graph.getNode(startId), 0);
        stepLog.add("✅ DFS completado. Nodos analizados: " + visitOrder.size());
    }

    private void dfsRecursive(Node node, int depth) {
        if (node == null || node.isVisited()) return;

        node.setVisited(true);
        visitOrder.add(node);

        String indent = "  ".repeat(depth);
        stepLog.add(indent + "👤 " + node.getName() + "  (profundidad " + depth + ")  —  rol: " + rolLabel(node.getBehavior()));

        for (Edge edge : graph.getEdges(node.getId())) {
            Node neighbor = edge.getTarget();
            if (!neighbor.isVisited()) {
                String msg = edge.getMessage().isEmpty() ? "" : "\n" + indent + "      💬 " + edge.getMessage();
                stepLog.add(indent + "   ↳ Mensaje de " + node.getName() + " a " + neighbor.getName()
                        + "  [impacto: " + edge.getWeight() + "]" + msg);
                dfsRecursive(neighbor, depth + 1);
            }
        }
    }

    public List<Node> getVisitOrder() { return visitOrder; }
    public List<String> getStepLog() { return stepLog; }

    private String rolLabel(String behavior) {
        return switch (behavior) {
            case "bully"     -> "🔴 Acosador";
            case "victim"    -> "🟡 Víctima";
            case "supporter" -> "🟢 Defensor";
            default          -> "🔵 Espectador";
        };
    }
}