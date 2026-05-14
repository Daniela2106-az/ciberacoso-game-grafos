package model.algorithms;

import model.graph.Graph;
import model.graph.Node;

import java.util.*;

public class FordFulkerson {

    private Graph graph;
    private List<String> stepLog;
    private List<List<String>> augmentingPaths;   // cada camino encontrado (ids)
    private List<Integer>      pathFlows;          // flujo enviado por cada camino
    private Map<String, Map<String, Integer>> capacity;   // capacidad original
    private Map<String, Map<String, Integer>> residual;   // grafo residual
    private Set<String> saturatedEdgeKeys;         // aristas saturadas al final
    private int maxFlow;

    public FordFulkerson(Graph graph) {
        this.graph             = graph;
        this.stepLog           = new ArrayList<>();
        this.augmentingPaths   = new ArrayList<>();
        this.pathFlows         = new ArrayList<>();
        this.saturatedEdgeKeys = new HashSet<>();
        this.capacity          = new HashMap<>();
        this.residual          = new HashMap<>();
    }

    public int execute(String sourceId, String sinkId) {
        stepLog.clear();
        augmentingPaths.clear();
        pathFlows.clear();
        saturatedEdgeKeys.clear();
        maxFlow = 0;

        // ── Construir grafo residual inicial ─────────────────────
        for (String u : graph.getNodes().keySet()) {
            capacity.put(u, new HashMap<>());
            residual.put(u, new HashMap<>());
        }
        for (var entry : graph.getAdjList().entrySet()) {
            String u = entry.getKey();
            for (var edge : entry.getValue()) {
                String v = edge.getTarget().getId();
                int cap  = edge.getWeight();
                capacity.get(u).put(v, cap);
                residual.get(u).merge(v,  cap, Integer::sum);
                residual.get(v).merge(u,    0, Integer::sum); // arista inversa
            }
        }

        stepLog.add("🌊 Analizando capacidad de carga de la red...");
        stepLog.add("📍 Fuente: "   + graph.getNode(sourceId).getName());
        stepLog.add("🎯 Sumidero: " + graph.getNode(sinkId).getName());
        stepLog.add("─────────────────────────────────");

        int pathNumber = 1;

        // ── Buscar caminos de aumento (BFS) ──────────────────────
        List<String> path;
        while (!(path = bfsPath(sourceId, sinkId)).isEmpty()) {

            // Capacidad mínima del camino (cuello de botella)
            int bottleneck = Integer.MAX_VALUE;
            for (int i = 0; i < path.size() - 1; i++) {
                String u = path.get(i);
                String v = path.get(i + 1);
                bottleneck = Math.min(bottleneck,
                        residual.get(u).getOrDefault(v, 0));
            }

            // Actualizar grafo residual
            for (int i = 0; i < path.size() - 1; i++) {
                String u = path.get(i);
                String v = path.get(i + 1);
                residual.get(u).merge(v, -bottleneck, Integer::sum);
                residual.get(v).merge(u,  bottleneck, Integer::sum);
            }

            maxFlow += bottleneck;
            augmentingPaths.add(new ArrayList<>(path));
            pathFlows.add(bottleneck);

            // ── Log narrativo de este camino ──────────────────────
            StringBuilder routeStr = new StringBuilder("• Ruta " + pathNumber + ": ");
            for (int i = 0; i < path.size(); i++) {
                routeStr.append(graph.getNode(path.get(i)).getName());
                if (i < path.size() - 1) routeStr.append(" → ");
            }
            stepLog.add(routeStr.toString());
            stepLog.add("  Flujo enviado: " + bottleneck
                    + (bottleneck == getOriginalCap(path) ? " (Saturado ⚠️)" : ""));

            // Capacidad residual de cada arista del camino
            for (int i = 0; i < path.size() - 1; i++) {
                String u = path.get(i);
                String v = path.get(i + 1);
                int origCap  = capacity.get(u).getOrDefault(v, 0);
                int flowUsed = origCap - residual.get(u).getOrDefault(v, 0);
                int remaining = residual.get(u).getOrDefault(v, 0);
                stepLog.add("  📊 " + graph.getNode(u).getName()
                        + " → " + graph.getNode(v).getName()
                        + "  [" + flowUsed + "/" + origCap + "]"
                        + "  Capacidad restante: " + remaining);
            }
            stepLog.add("  Flujo acumulado: " + maxFlow);
            stepLog.add("─────────────────────────────────");
            pathNumber++;
        }

        // ── Detectar aristas saturadas (corte mínimo) ────────────
        for (String u : capacity.keySet()) {
            for (String v : capacity.get(u).keySet()) {
                int origCap = capacity.get(u).getOrDefault(v, 0);
                int resid   = residual.get(u).getOrDefault(v, 0);
                if (origCap > 0 && resid == 0) {
                    saturatedEdgeKeys.add(u + "-" + v);
                }
            }
        }

        // ── Resumen final ─────────────────────────────────────────
        stepLog.add("⚠️  Flujo Máximo de Acoso Detectado: " + maxFlow + " unidades/seg");
        stepLog.add("─────────────────────────────────");
        stepLog.add("🔴 Aristas saturadas (corte mínimo):");
        for (String key : saturatedEdgeKeys) {
            String[] parts = key.split("-");
            Node u = graph.getNode(parts[0]);
            Node v = graph.getNode(parts[1]);
            if (u != null && v != null) {
                stepLog.add("   ✂️  " + u.getName() + " → " + v.getName()
                        + "  [cap: " + capacity.get(parts[0]).get(parts[1]) + "]");
            }
        }
        stepLog.add("─────────────────────────────────");
        stepLog.add("⚠️  Diagnóstico: Para detener el ataque, debes");
        stepLog.add("   intervenir las conexiones saturadas inmediatamente.");

        return maxFlow;
    }

    // ── BFS para encontrar camino de aumento ─────────────────────
    private List<String> bfsPath(String source, String sink) {
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            String u = queue.poll();
            if (u.equals(sink)) break;
            for (String v : residual.get(u).keySet()) {
                if (!visited.contains(v) && residual.get(u).get(v) > 0) {
                    visited.add(v);
                    parent.put(v, u);
                    queue.add(v);
                }
            }
        }

        if (!visited.contains(sink)) return new ArrayList<>();

        // Reconstruir camino
        List<String> path = new ArrayList<>();
        String curr = sink;
        while (curr != null) {
            path.add(0, curr);
            curr = parent.get(curr);
        }
        return path;
    }

    private int getOriginalCap(List<String> path) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < path.size() - 1; i++) {
            min = Math.min(min, capacity.get(path.get(i))
                    .getOrDefault(path.get(i + 1), 0));
        }
        return min;
    }

    public int getMaxFlow()                       { return maxFlow; }
    public List<String> getStepLog()              { return stepLog; }
    public List<List<String>> getAugmentingPaths(){ return augmentingPaths; }
    public List<Integer> getPathFlows()           { return pathFlows; }
    public Set<String> getSaturatedEdgeKeys()     { return saturatedEdgeKeys; }
    public Map<String, Map<String, Integer>> getResidual()  { return residual; }
    public Map<String, Map<String, Integer>> getCapacity()  { return capacity; }
}