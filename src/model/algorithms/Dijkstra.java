package model.algorithms;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;

import java.util.*;
import java.util.function.BiConsumer;

public class Dijkstra {
    private Graph graph;
    private Map<String, Integer> distances;
    private Map<String, String> previous;
    private List<String> stepLog;

    // Callback visual: (nodeId, distancia) → actualiza etiqueta en GraphPanel
    private BiConsumer<String, Integer> onDistUpdate;

    public Dijkstra(Graph graph) {
        this.graph     = graph;
        this.distances = new HashMap<>();
        this.previous  = new HashMap<>();
        this.stepLog   = new ArrayList<>();
    }

    public void setOnDistUpdate(BiConsumer<String, Integer> callback) {
        this.onDistUpdate = callback;
    }

    public void execute(String startId) {
        distances.clear();
        previous.clear();
        stepLog.clear();

        // Inicializar en infinito
        for (String id : graph.getNodes().keySet()) {
            distances.put(id, Integer.MAX_VALUE);
            previous.put(id, null);
            if (onDistUpdate != null) onDistUpdate.accept(id, Integer.MAX_VALUE);
        }
        distances.put(startId, 0);
        if (onDistUpdate != null) onDistUpdate.accept(startId, 0);

        PriorityQueue<String> pq = new PriorityQueue<>(
                Comparator.comparingInt(id -> distances.get(id)));
        pq.add(startId);

        Node startNode = graph.getNode(startId);
        stepLog.add("🛡️ Iniciando protocolo de Ruta Segura...");
        stepLog.add("📍 Punto de partida: " + startNode.getName());
        stepLog.add("─────────────────────────────────");

        while (!pq.isEmpty()) {
            String currentId = pq.poll();
            Node current     = graph.getNode(currentId);
            int currentDist  = distances.get(currentId);

            if (currentDist == Integer.MAX_VALUE) break;

            stepLog.add("🔍 Analizando: " + current.getName()
                    + "  [riesgo acumulado: " + currentDist + "]");

            for (Edge edge : graph.getEdges(currentId)) {
                Node neighbor   = edge.getTarget();
                String nId      = neighbor.getId();
                int newDist     = currentDist + edge.getWeight();
                int oldDist     = distances.get(nId);

                stepLog.add("   • Evaluando conexión " + current.getName()
                        + " → " + neighbor.getName()
                        + "  (Costo: " + edge.getWeight() + ")");

                if (!edge.getMessage().isEmpty()) {
                    stepLog.add("     💬 " + edge.getMessage());
                }

                if (newDist < oldDist) {
                    distances.put(nId, newDist);
                    previous.put(nId, currentId);
                    pq.add(nId);
                    if (onDistUpdate != null) onDistUpdate.accept(nId, newDist);

                    stepLog.add("     ✅ Decisión: El camino por '"
                            + current.getName() + "' es más seguro → "
                            + neighbor.getName() + " actualizado a " + newDist);
                } else {
                    stepLog.add("     ⏭ Descartado: ya existe una ruta más segura ("
                            + oldDist + " ≤ " + newDist + ")");
                }
            }
            stepLog.add("─────────────────────────────────");
        }
        stepLog.add("✅ Dijkstra completado.");
    }

    public List<Node> getPath(String targetId) {
        List<Node> path = new ArrayList<>();
        String current = targetId;
        while (current != null) {
            path.add(0, graph.getNode(current));
            current = previous.get(current);
        }
        if (path.size() == 1 && distances.get(targetId) == Integer.MAX_VALUE)
            return new ArrayList<>();
        return path;
    }

    /** Genera el log final con la ruta y el riesgo total */
    public List<String> buildFinalLog(List<Node> path, String targetId) {
        List<String> summary = new ArrayList<>();
        if (path.isEmpty()) {
            summary.add("❌ No se encontró ruta segura.");
            return summary;
        }
        StringBuilder routeStr = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            routeStr.append(path.get(i).getName());
            if (i < path.size() - 1) routeStr.append(" → ");
        }
        summary.add("─────────────────────────────────");
        summary.add("🏁 Ruta óptima encontrada:");
        summary.add("   " + routeStr);
        summary.add("⚠️  Riesgo total de la intervención: "
                + distances.get(targetId) + " unidades.");
        summary.add("✅ ¡Intervención exitosa! Has llegado a la víctima");
        summary.add("   con el menor riesgo posible.");
        return summary;
    }

    public int getDistance(String targetId) {
        return distances.getOrDefault(targetId, Integer.MAX_VALUE);
    }

    public List<String> getStepLog() { return stepLog; }
}