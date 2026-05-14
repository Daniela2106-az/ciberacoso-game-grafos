package model.algorithms;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;

import java.util.*;

public class Kruskal {
    private Graph graph;
    private List<Edge> mstEdges;       // aristas del árbol resultante
    private List<Edge> rejectedEdges;  // aristas descartadas (formarían ciclo)
    private List<String> stepLog;
    private int totalCost;

    public Kruskal(Graph graph) {
        this.graph         = graph;
        this.mstEdges      = new ArrayList<>();
        this.rejectedEdges = new ArrayList<>();
        this.stepLog       = new ArrayList<>();
        this.totalCost     = 0;
    }

    public void execute() {
        mstEdges.clear();
        rejectedEdges.clear();
        stepLog.clear();
        totalCost = 0;

        List<Edge> allEdges = new ArrayList<>(graph.getAllEdges());
        allEdges.sort(Comparator.comparingInt(Edge::getWeight));

        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> rank  = new HashMap<>();
        for (String id : graph.getNodes().keySet()) {
            parent.put(id, id);
            rank.put(id, 0);
        }

        stepLog.add("🏗️ Iniciando reconstrucción de confianza...");
        stepLog.add("📋 Orden de análisis (de menor a mayor esfuerzo):");
        for (Edge e : allEdges) {
            stepLog.add("   " + e.getSource().getName() + " ↔ "
                    + e.getTarget().getName() + "  [esfuerzo: " + e.getWeight() + "]");
        }
        stepLog.add("─────────────────────────────────");

        int nodesCount = graph.getNodes().size();

        for (Edge edge : allEdges) {
            String srcId = edge.getSource().getId();
            String tgtId = edge.getTarget().getId();
            String rootA = find(parent, srcId);
            String rootB = find(parent, tgtId);

            // Si el MST ya está completo, todo lo demás es rechazado
            if (mstEdges.size() == nodesCount - 1) {
                rejectedEdges.add(edge);
                stepLog.add("• Analizando conexión '" + edge.getSource().getName()
                        + "' y '" + edge.getTarget().getName()
                        + "'  (Costo: " + edge.getWeight() + ")  →  ❌ Rechazada:");
                stepLog.add("  MST ya completo — conexión innecesaria.");
                stepLog.add("─────────────────────────────────");
                continue;
            }

            stepLog.add("• Analizando conexión '" + edge.getSource().getName()
                    + "' y '" + edge.getTarget().getName()
                    + "'  (Costo: " + edge.getWeight() + ")");

            if (!rootA.equals(rootB)) {
                union(parent, rank, rootA, rootB);
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                stepLog.add("  → ¡Éxito! Costo acumulado: " + totalCost);
            } else {
                rejectedEdges.add(edge);
                stepLog.add("  → ❌ Rechazada:");
                stepLog.add("  Ya existe una ruta de confianza entre ellos,");
                stepLog.add("  sería un gasto redundante.");
            }
            stepLog.add("─────────────────────────────────");
        }

        stepLog.add("✅ Red restablecida con esfuerzo mínimo de " + totalCost + " puntos.");
        stepLog.add("🌐 Conexiones establecidas: " + mstEdges.size());
        stepLog.add("❌ Conexiones rechazadas: " + rejectedEdges.size());
    }

    // ── Union-Find ───────────────────────────────────────────────
    private String find(Map<String, String> parent, String id) {
        if (!parent.get(id).equals(id))
            parent.put(id, find(parent, parent.get(id)));
        return parent.get(id);
    }

    private void union(Map<String, String> parent, Map<String, Integer> rank,
                       String a, String b) {
        if (rank.get(a) < rank.get(b))  { parent.put(a, b); }
        else if (rank.get(a) > rank.get(b)) { parent.put(b, a); }
        else { parent.put(b, a); rank.put(a, rank.get(a) + 1); }
    }

    public List<Edge> getMstEdges()      { return mstEdges; }
    public List<Edge> getRejectedEdges() { return rejectedEdges; }
    public List<String> getStepLog()     { return stepLog; }
    public int getTotalCost()            { return totalCost; }
}