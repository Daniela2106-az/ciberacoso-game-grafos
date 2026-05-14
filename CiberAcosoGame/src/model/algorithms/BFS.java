package model.algorithms;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;

import java.util.*;

public class BFS {
    private Graph graph;
    private List<Node> visitOrder;      // Orden en que se visitaron nodos
    private List<String> stepLog;       // Explicaciones paso a paso

    public BFS(Graph graph) {
        this.graph = graph;
        this.visitOrder = new ArrayList<>();
        this.stepLog = new ArrayList<>();
    }

    public void execute(String startId) {
        graph.resetVisited();
        visitOrder.clear();
        stepLog.clear();

        Queue<Node> queue = new LinkedList<>();
        Node start = graph.getNode(startId);
        if (start == null) return;

        start.setVisited(true);
        queue.add(start);
        stepLog.add("🔍 Inicio BFS desde: " + start.getName());

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            visitOrder.add(current);
            stepLog.add("👤 " + current.getName() + "  —  rol: " + rolLabel(current.getBehavior()));

            for (Edge edge : graph.getEdges(current.getId())) {
                Node neighbor = edge.getTarget();
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    queue.add(neighbor);
                    String msg = edge.getMessage().isEmpty() ? "" : "\n      💬 \"" + edge.getMessage() + "\"";
                    stepLog.add("   ↳ " + current.getName() + " → " + neighbor.getName()
                            + "  [impacto: " + edge.getWeight() + "]" + msg);
                }
            }
        }
        stepLog.add("✅ BFS completado. Nodos analizados: " + visitOrder.size());
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











