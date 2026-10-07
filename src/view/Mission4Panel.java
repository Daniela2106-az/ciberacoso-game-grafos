package view;

import controller.GameController;
import controller.MissionController;
import model.algorithms.FordFulkerson;
import model.graph.Graph;
import model.graph.Node;
import util.Constants;
import util.GraphGenerator;
import view.components.StepCard;
import view.components.VisualLogPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Mission4Panel extends JPanel {
    private MainWindow window;
    private Graph graph;
    private MissionController controller;
    private GraphPanel graphPanel;
    private VisualLogPanel visualLog;
    private JLabel statusLabel;
    private JButton runBtn, resetBtn;
    private javax.swing.Timer animTimer;

    private Node selectedSource = null;
    private Node selectedSink   = null;
    private JLabel sourceLabel, sinkLabel;
    private int maxFlow = 0;

    public Mission4Panel(MainWindow window) {
        this.window     = window;
        this.graph      = GraphGenerator.generateMission4Graph();
        this.controller = new MissionController(graph);
        setBackground(Constants.BG_MAIN);
        setLayout(new BorderLayout(10, 10));
        buildUI();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(160, 50, 180));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("🌊 Misión 4 – Control del Impacto");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Calcula el flujo máximo de acoso entre dos nodos y bloquea las conexiones saturadas");
        desc.setFont(Constants.FONT_BODY);
        desc.setForeground(new Color(235, 200, 255));

        JButton backBtn = new JButton("← Menú");
        backBtn.setFont(Constants.FONT_BODY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(130, 30, 150));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> window.showPanel("MENU"));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(desc);
        JButton helpBtn = new JButton("❓ Ayuda");
        helpBtn.setFont(Constants.FONT_BODY);
        helpBtn.setForeground(Color.WHITE);
        helpBtn.setBackground(new Color(120, 30, 150));
        helpBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        helpBtn.setFocusPainted(false);
        helpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpBtn.addActionListener(e ->
                new HelpDialog((JFrame) SwingUtilities.getWindowAncestor(this), 3).setVisible(true));

        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerBtns.setOpaque(false);
        headerBtns.add(helpBtn);
        headerBtns.add(backBtn);

        header.add(titleBox, BorderLayout.CENTER);
        header.add(headerBtns, BorderLayout.EAST);

        // ── Info cards ───────────────────────────────────────────
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        infoPanel.setBackground(Constants.BG_MAIN);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 16));

        infoPanel.add(makeInfoCard("🎯 Objetivo",
                "<html>Eres un <b>Ingeniero de Redes</b>.<br>" +
                        "Selecciona cualquier <b>nodo origen</b> y <b>destino</b>.<br>" +
                        "Ford-Fulkerson calculará el flujo máximo posible.</html>",
                new Color(248, 230, 255)));

        infoPanel.add(makeInfoCard("📡 ¿Qué es la capacidad?",
                "<html>Cada arista es una <b>tubería</b>.<br>" +
                        "El número = mensajes por segundo que pueden pasar.<br>" +
                        "Las tuberías <b>llenas (rojo)</b> son el cuello de botella.</html>",
                new Color(255, 243, 230)));

        infoPanel.add(makeInfoCard("🖱️ ¿Cómo jugar?",
                "<html>1. Haz clic en el <b>nodo origen</b>.<br>" +
                        "2. Haz clic en el <b>nodo destino</b>.<br>" +
                        "3. Presiona <b>'Simular Flujo'</b>.<br>" +
                        "4. <b>Bloquea</b> la conexión saturada.</html>",
                new Color(240, 240, 255)));

        // ── Grafo + Log visual ───────────────────────────────────
        graphPanel = new GraphPanel(graph);
        graphPanel.setNeutralMode(true);
        graphPanel.enableClickMode(this::handleNodeClick);

        visualLog = new VisualLogPanel("Análisis de Flujo");
        visualLog.setPreferredSize(new Dimension(500, 400));

        statusLabel = new JLabel("Haz clic en el nodo fuente para comenzar");
        statusLabel.setFont(Constants.FONT_SMALL);
        statusLabel.setForeground(Constants.TEXT_LIGHT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel logWrapper = new JPanel(new BorderLayout(0, 4));
        logWrapper.setBackground(Constants.BG_MAIN);
        logWrapper.setPreferredSize(new Dimension(500, 440));
        logWrapper.add(visualLog, BorderLayout.CENTER);
        logWrapper.add(statusLabel, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.setBackground(Constants.BG_MAIN);
        center.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        center.add(graphPanel, BorderLayout.CENTER);
        center.add(logWrapper, BorderLayout.EAST);

        // ── Controles ────────────────────────────────────────────
        JPanel controls = buildControls();

        // ── Ensamblar ────────────────────────────────────────────
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Constants.BG_MAIN);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(infoPanel, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
    }

    private JPanel buildControls() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        panel.setBackground(Constants.BG_MAIN);

        sourceLabel = makeStatusBadge("🔴 Origen: (sin seleccionar)",   new Color(255, 230, 230));
        sinkLabel   = makeStatusBadge("🟡 Destino: (sin seleccionar)", new Color(255, 250, 220));

        runBtn   = makeButton("🌊 Simular Flujo", new Color(160, 50, 180));
        resetBtn = makeButton("↺ Reiniciar",       new Color(160, 160, 170));
        runBtn.setEnabled(false);

        runBtn.addActionListener(e -> runFordFulkerson());
        resetBtn.addActionListener(e -> resetAll());

        panel.add(sourceLabel);
        panel.add(sinkLabel);
        panel.add(runBtn);
        panel.add(resetBtn);
        return panel;
    }

    private void handleNodeClick(Node node) {
        if (selectedSource == null) {
            selectedSource = node;
            sourceLabel.setText("🔴 Origen: " + node.getName());
            graphPanel.highlightNode(node);
            statusLabel.setText("Ahora haz clic en el nodo destino");
            visualLog.clear();
            visualLog.addCard("🔴", "Origen seleccionado", node.getName(), StepCard.Type.ERROR);
            visualLog.addCard("👆", "Siguiente", "Haz clic en el nodo destino.", StepCard.Type.INFO);
        } else if (selectedSink == null && !node.getId().equals(selectedSource.getId())) {
            selectedSink = node;
            sinkLabel.setText("🟡 Destino: " + node.getName());
            graphPanel.highlightNode(node);
            statusLabel.setText("¡Listo! Presiona 'Simular Flujo'");
            visualLog.addCard("🟡", "Destino seleccionado", node.getName(), StepCard.Type.WARNING);
            visualLog.addCard("▶️", "Listo", "Presiona 'Simular Flujo'.", StepCard.Type.ACTIVE);
            runBtn.setEnabled(true);
        }
    }

    private void runFordFulkerson() {
        graphPanel.disableClickMode();
        runBtn.setEnabled(false);
        visualLog.clear();
        visualLog.addCard("⚙️", "Ford-Fulkerson iniciado",
                "Buscando caminos de aumento...", StepCard.Type.ACTIVE);

        maxFlow = controller.runFordFulkerson(selectedSource.getId(), selectedSink.getId());

        FordFulkerson ff = controller.getFordFulkerson();

        Map<String, int[]> flowData = new HashMap<>();
        for (String u : ff.getCapacity().keySet())
            for (String v : ff.getCapacity().get(u).keySet()) {
                int cap  = ff.getCapacity().get(u).get(v);
                int flow = cap - ff.getResidual().get(u).getOrDefault(v, 0);
                flowData.put(u + "-" + v, new int[]{flow, cap});
            }
        graphPanel.setFlowData(flowData);

        animateAugmentingPaths(ff);
    }

    private void animateAugmentingPaths(FordFulkerson ff) {
        List<List<String>> paths = ff.getAugmentingPaths();
        List<Integer> flows      = ff.getPathFlows();

        if (animTimer != null) animTimer.stop();
        final int[] pathIdx = {0};

        animTimer = new javax.swing.Timer(1200, e -> {
            graphPanel.clearActivePath();
            if (pathIdx[0] < paths.size()) {
                List<String> path = paths.get(pathIdx[0]);
                int flow          = flows.get(pathIdx[0]);
                graphPanel.setActivePath(path);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    sb.append(graph.getNode(path.get(i)).getName());
                    if (i < path.size() - 1) sb.append(" → ");
                }
                visualLog.addCard("🌊", "Ruta " + (pathIdx[0] + 1),
                        sb + " — flujo: " + flow, StepCard.Type.ACTIVE);
                statusLabel.setText("Ruta " + (pathIdx[0] + 1) + ": " + sb + "  [flujo: " + flow + "]");
                pathIdx[0]++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                graphPanel.clearActivePath();
                graphPanel.setSaturated(ff.getSaturatedEdgeKeys());

                // Mostrar aristas saturadas en el log
                for (String key : ff.getSaturatedEdgeKeys()) {
                    String[] parts = key.split("-");
                    Node u = graph.getNode(parts[0]);
                    Node v = graph.getNode(parts[1]);
                    if (u != null && v != null) {
                        int cap = ff.getCapacity().get(parts[0]).get(parts[1]);
                        visualLog.addCard("⚠️", "Saturada: " + u.getName() + " → " + v.getName(),
                                "Capacidad: " + cap + " — es parte del corte mínimo", StepCard.Type.WARNING);
                    }
                }
                visualLog.addCard("🔴", "Flujo máximo: " + maxFlow + " unidades/seg",
                        "Bloquea una arista saturada para detener el acoso.", StepCard.Type.ERROR);
                statusLabel.setText("⚠️ Flujo máximo: " + maxFlow + " — elige una arista para bloquear");
                showBlockDialog(ff);
            }
        });
        animTimer.start();
    }

    private void showBlockDialog(FordFulkerson ff) {
        Set<String> saturated = ff.getSaturatedEdgeKeys();
        if (saturated.isEmpty()) { showFinalResult(null); return; }

        List<String> options    = new ArrayList<>();
        List<String> optionKeys = new ArrayList<>();
        for (String key : saturated) {
            String[] parts = key.split("-");
            Node u = graph.getNode(parts[0]);
            Node v = graph.getNode(parts[1]);
            if (u != null && v != null) {
                int cap = ff.getCapacity().get(parts[0]).get(parts[1]);
                options.add("✂️  " + u.getName() + " → " + v.getName() + "  [cap: " + cap + "]");
                optionKeys.add(key);
            }
        }

        String[] optArr = options.toArray(new String[0]);
        String choice = (String) JOptionPane.showInputDialog(this,
                "<html><b>⚠️ Flujo máximo: " + maxFlow + " unidades/seg</b><br><br>" +
                        "Las aristas en <b style='color:red'>rojo</b> están saturadas.<br>" +
                        "Son el <b>corte mínimo</b> — bloquear una detiene el flujo.<br><br>" +
                        "¿Qué conexión vas a bloquear?</html>",
                "🌊 Elegir Corte Mínimo",
                JOptionPane.QUESTION_MESSAGE, null, optArr, optArr[0]);

        if (choice != null) {
            int idx = Arrays.asList(optArr).indexOf(choice);
            String[] parts = optionKeys.get(idx).split("-");
            Node u = graph.getNode(parts[0]);
            Node v = graph.getNode(parts[1]);
            showFinalResult(u.getName() + " → " + v.getName());
        }
    }

    private void showFinalResult(String blockedEdge) {
        String blocked = blockedEdge != null
                ? "<br><b>Conexión bloqueada:</b> " + blockedEdge + "<br>¡El flujo de acoso fue interrumpido! ✅"
                : "";
        String message = "<html><b>🌊 Análisis Completado</b><br><br>" +
                "<b>Flujo máximo:</b> " + maxFlow + " unidades/seg<br>" +
                "<b>Origen:</b> " + selectedSource.getName() + "<br>" +
                "<b>Destino:</b> " + selectedSink.getName() + blocked + "<br><br>" +
                "<i>Ford-Fulkerson garantiza que este es el flujo máximo posible.</i></html>";

        int opt = JOptionPane.showOptionDialog(this, message, "✅ Misión 4 Completada",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"✅ Volver al menú", "↺ Intentar de nuevo"}, null);
        GameController.getInstance().completeMission(3, 3);
        if (opt == 0) {
            GameController.getInstance().completeMission(3, 3);
            window.showPanel("MENU");
        }
        else resetAll();
    }

    private void resetAll() {
        if (animTimer != null) animTimer.stop();
        selectedSource = null;
        selectedSink   = null;
        sourceLabel.setText("🔴 Origen: (sin seleccionar)");
        sinkLabel.setText("🟡 Destino: (sin seleccionar)");
        runBtn.setEnabled(false);
        visualLog.clear();
        graphPanel.reset();
        graphPanel.enableClickMode(this::handleNodeClick);
        statusLabel.setText("Haz clic en el nodo fuente para comenzar");
    }

    private JLabel makeStatusBadge(String text, Color bg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.FONT_BODY);
        lbl.setForeground(Constants.TEXT_DARK);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return lbl;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(Constants.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel makeInfoCard(String title, String body, Color bg) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel t = new JLabel(title);
        t.setFont(Constants.FONT_HEADING);
        t.setForeground(Constants.TEXT_DARK);
        JLabel b = new JLabel(body);
        b.setFont(Constants.FONT_BODY);
        b.setForeground(Constants.TEXT_DARK);
        card.add(t, BorderLayout.NORTH);
        card.add(b, BorderLayout.CENTER);
        return card;
    }
}