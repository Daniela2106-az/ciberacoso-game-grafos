package view;

import controller.GameController;
import controller.MissionController;
import model.algorithms.Dijkstra;
import model.graph.Graph;
import model.graph.Node;
import util.Constants;
import util.GraphGenerator;
import view.components.StepCard;
import view.components.VisualLogPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Mission2Panel extends JPanel {
    private MainWindow window;
    private Graph graph;
    private MissionController controller;
    private GraphPanel graphPanel;
    private VisualLogPanel visualLog;
    private JLabel statusLabel;

    private List<Node> currentPath;
    private Node selectedSource = null;
    private Node selectedTarget = null;
    private JLabel sourceLabel, targetLabel;
    private JButton runBtn, resetBtn;
    private javax.swing.Timer animTimer;

    public Mission2Panel(MainWindow window) {
        this.window     = window;
        this.graph      = GraphGenerator.generateMission2Graph();
        this.controller = new MissionController(graph);
        setBackground(Constants.BG_MAIN);
        setLayout(new BorderLayout(10, 10));
        buildUI();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 120, 210));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("🛡️ Misión 2 – Ruta Segura");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Selecciona origen y destino — Dijkstra encontrará el camino de menor riesgo");
        desc.setFont(Constants.FONT_BODY);
        desc.setForeground(new Color(200, 220, 255));

        JButton backBtn = new JButton("← Menú");
        backBtn.setFont(Constants.FONT_BODY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(40, 100, 185));
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
        helpBtn.setBackground(new Color(35, 90, 170));
        helpBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        helpBtn.setFocusPainted(false);
        helpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpBtn.addActionListener(e ->
                new HelpDialog((JFrame) SwingUtilities.getWindowAncestor(this), 1).setVisible(true));

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
                "<html>Dijkstra calculará la ruta de <b>menor riesgo</b>.<br>" +
                        "Cuando veas el camino resaltado, <b>haz clic en el nodo destino</b><br>" +
                        "para confirmar la intervención.</html>",
                new Color(230, 243, 255)));

        infoPanel.add(makeInfoCard("⚖️ Nivel de riesgo",
                "<html><b style='color:#27ae60'>1–3</b> → Canal seguro<br>" +
                        "<b style='color:#e67e22'>4–6</b> → Riesgo medio<br>" +
                        "<b style='color:#e55050'>7–10</b> → Canal peligroso<br>" +
                        "Dijkstra evita los caminos de mayor riesgo.</html>",
                new Color(255, 243, 230)));

        infoPanel.add(makeInfoCard("🖱️ ¿Cómo jugar?",
                "<html>1. Haz clic en el <b>nodo origen</b>.<br>" +
                        "2. Haz clic en el <b>nodo destino</b>.<br>" +
                        "3. Presiona <b>Calcular Ruta Segura</b>.<br>" +
                        "4. Cuando veas la ruta, <b>haz clic en el destino</b> para confirmar.</html>",
                new Color(240, 255, 245)));

        // ── Grafo + Log visual ───────────────────────────────────
        graphPanel = new GraphPanel(graph);
        graphPanel.enableClickMode(this::handleNodeClick);

        visualLog = new VisualLogPanel("¿Qué hace Dijkstra?");
        visualLog.setPreferredSize(new Dimension(380, 400));

        statusLabel = new JLabel("Haz clic en un nodo para comenzar");
        statusLabel.setFont(Constants.FONT_SMALL);
        statusLabel.setForeground(Constants.TEXT_LIGHT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel logWrapper = new JPanel(new BorderLayout(0, 4));
        logWrapper.setBackground(Constants.BG_MAIN);
        logWrapper.setPreferredSize(new Dimension(380, 440));
        logWrapper.add(visualLog, BorderLayout.CENTER);
        logWrapper.add(statusLabel, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.setBackground(Constants.BG_MAIN);
        center.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        center.add(graphPanel, BorderLayout.CENTER);
        center.add(logWrapper, BorderLayout.EAST);

        JPanel controls = buildControls();

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

        sourceLabel = makeStatusBadge("🟢 Origen: (sin seleccionar)", new Color(230, 255, 240));
        targetLabel = makeStatusBadge("🟡 Destino: (sin seleccionar)", new Color(255, 250, 220));

        runBtn   = makeButton("🔍 Calcular Ruta Segura", new Color(52, 120, 210));
        resetBtn = makeButton("↺ Reiniciar",              new Color(160, 160, 170));
        runBtn.setEnabled(false);

        runBtn.addActionListener(e -> runDijkstra());
        resetBtn.addActionListener(e -> resetAll());

        panel.add(sourceLabel);
        panel.add(targetLabel);
        panel.add(runBtn);
        panel.add(resetBtn);
        return panel;
    }

    // ── Fase 1: selección de nodos ───────────────────────────────
    private void handleNodeClick(Node node) {
        if (selectedSource == null) {
            selectedSource = node;
            sourceLabel.setText("🟢 Origen: " + node.getName());
            graphPanel.highlightNode(node);
            statusLabel.setText("Ahora haz clic en el nodo destino");
            visualLog.clear();
            visualLog.addCard("🟢", "Origen seleccionado", node.getName()
                    + "  [" + rolLabel(node.getBehavior()) + "]", StepCard.Type.SUCCESS);
            visualLog.addCard("👆", "Siguiente paso",
                    "Haz clic en el nodo destino.", StepCard.Type.INFO);
        } else if (selectedTarget == null && !node.getId().equals(selectedSource.getId())) {
            selectedTarget = node;
            targetLabel.setText("🟡 Destino: " + node.getName());
            graphPanel.highlightNode(node);
            statusLabel.setText("¡Listo! Presiona 'Calcular Ruta Segura'");
            visualLog.addCard("🎯", "Destino seleccionado", node.getName()
                    + "  [" + rolLabel(node.getBehavior()) + "]", StepCard.Type.WARNING);
            visualLog.addCard("▶️", "Listo",
                    "Presiona 'Calcular Ruta Segura'.", StepCard.Type.ACTIVE);
            runBtn.setEnabled(true);
        }
    }

    // ── Fase 2: ejecutar Dijkstra ────────────────────────────────
    private void runDijkstra() {
        if (selectedSource == null || selectedTarget == null) return;
        graphPanel.disableClickMode();
        runBtn.setEnabled(false);
        visualLog.clear();
        visualLog.addCard("⚙️", "Dijkstra ejecutándose",
                "Calculando distancias desde " + selectedSource.getName() + "...",
                StepCard.Type.ACTIVE);

        Dijkstra dijkstra = controller.getDijkstra();
        dijkstra.setOnDistUpdate((nodeId, dist) ->
                SwingUtilities.invokeLater(() -> graphPanel.updateDistLabel(nodeId, dist)));

        currentPath = controller.runDijkstra(selectedSource.getId(), selectedTarget.getId());

        if (currentPath.isEmpty()) {
            statusLabel.setText("❌ No existe ruta segura entre esos nodos");
            visualLog.addCard("❌", "Sin ruta",
                    "No hay camino posible entre esos nodos.", StepCard.Type.ERROR);
            return;
        }

        statusLabel.setText("Animando ruta óptima...");
        animateFinalPath();
    }

    private void animateFinalPath() {
        if (animTimer != null) animTimer.stop();
        final int[] step = {0};
        graphPanel.reset();

        Dijkstra dijkstra = controller.getDijkstra();
        for (Node n : graph.getAllNodes())
            graphPanel.updateDistLabel(n.getId(), dijkstra.getDistance(n.getId()));

        animTimer = new javax.swing.Timer(700, e -> {
            if (step[0] < currentPath.size()) {
                Node n = currentPath.get(step[0]);
                graphPanel.highlightNode(n);
                int dist = controller.getDijkstra().getDistance(n.getId());
                visualLog.addCard("➡️", n.getName(),
                        "Riesgo acumulado: " + (dist == Integer.MAX_VALUE ? "∞" : dist),
                        StepCard.Type.ACTIVE);
                statusLabel.setText("➡️ " + n.getName()
                        + "  (" + (step[0] + 1) + "/" + currentPath.size() + ")");
                step[0]++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                graphPanel.showFinalPath(currentPath);
                int risk = controller.getDijkstraDistance(selectedTarget.getId());
                visualLog.addCard("✅", "Ruta más segura encontrada",
                        "Riesgo total: " + risk + " unidades.", StepCard.Type.SUCCESS);
                visualLog.addCard("🎯", "¡Tu turno!",
                        "Haz clic en el nodo <b>" + selectedTarget.getName()
                                + "</b> para confirmar la intervención.",
                        StepCard.Type.WARNING);
                statusLabel.setText("🎯 Haz clic en '"
                        + selectedTarget.getName() + "' para confirmar la intervención");
                // Activar clic en el destino para confirmar
                graphPanel.enableClickMode(this::confirmIntervention);
            }
        });
        animTimer.start();
    }

    // ── Fase 3: el jugador confirma haciendo clic en el destino ──
    private void confirmIntervention(Node node) {
        if (!node.getId().equals(selectedTarget.getId())) {
            visualLog.addCard("❌", "Nodo incorrecto",
                    "Ese no es el destino. Haz clic en <b>"
                            + selectedTarget.getName() + "</b>.", StepCard.Type.ERROR);
            return;
        }
        graphPanel.disableClickMode();
        int risk = controller.getDijkstraDistance(selectedTarget.getId());

        StringBuilder routeStr = new StringBuilder();
        for (int i = 0; i < currentPath.size(); i++) {
            routeStr.append(currentPath.get(i).getName());
            if (i < currentPath.size() - 1) routeStr.append(" → ");
        }

        visualLog.addCard("🏆", "¡Intervención confirmada!",
                "Ruta: " + routeStr + " — Riesgo: " + risk, StepCard.Type.SUCCESS);
        statusLabel.setText("✅ ¡Intervención exitosa!");

        String message = "<html><b>🛡️ ¡Intervención Exitosa!</b><br><br>" +
                "<b>Ruta segura:</b> " + routeStr + "<br>" +
                "<b>Riesgo total:</b> " + risk + " unidades<br><br>" +
                "<i>Dijkstra garantiza que no existe ninguna ruta con menor riesgo.<br>" +
                "Has logrado llegar al usuario afectado por el canal más seguro.</i></html>";

        int opt = JOptionPane.showOptionDialog(this, message, "✅ Misión 2 Completada",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"✅ Volver al menú", "↺ Intentar de nuevo"}, null);
        GameController.getInstance().completeMission(1, 3);
        if (opt == 0) {
            GameController.getInstance().completeMission(1, 3);
            window.showPanel("MENU");
        }
        else resetAll();
    }

    private void resetAll() {
        if (animTimer != null) animTimer.stop();
        selectedSource = null;
        selectedTarget = null;
        currentPath    = null;
        sourceLabel.setText("🟢 Origen: (sin seleccionar)");
        targetLabel.setText("🟡 Destino: (sin seleccionar)");
        runBtn.setEnabled(false);
        visualLog.clear();
        graphPanel.reset();
        graphPanel.enableClickMode(this::handleNodeClick);
        statusLabel.setText("Haz clic en un nodo para comenzar");
    }

    private String rolLabel(String behavior) {
        return switch (behavior) {
            case "bully"     -> "🔴 Acosador";
            case "victim"    -> "🟡 Víctima";
            case "supporter" -> "🟢 Defensor";
            default          -> "🔵 Espectador";
        };
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