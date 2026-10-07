package view;

import controller.GameController;
import controller.MissionController;
import model.algorithms.Kruskal;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;
import util.Constants;
import util.GraphGenerator;
import view.components.StepCard;
import view.components.VisualLogPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Mission3Panel extends JPanel {
    private MainWindow window;
    private Graph graph;
    private MissionController controller;
    private GraphPanel graphPanel;
    private VisualLogPanel visualLog;
    private JLabel statusLabel;
    private JButton runBtn, stepBtn, resetBtn;
    private javax.swing.Timer animTimer;

    private List<Edge> mstEdges;
    private List<Edge> rejectedEdges;
    private List<Edge> allSortedEdges;
    private int currentStep = 0;
    private boolean kruskalDone = false;

    public Mission3Panel(MainWindow window) {
        this.window     = window;
        this.graph      = GraphGenerator.generateMission3Graph();
        this.controller = new MissionController(graph);
        setBackground(Constants.BG_MAIN);
        setLayout(new BorderLayout(10, 10));
        buildUI();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(46, 160, 100));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("🔗 Misión 3 – Reconstruir la Red");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("El acoso destruyó la confianza. Usa Kruskal para reconstruir la red con el menor esfuerzo");
        desc.setFont(Constants.FONT_BODY);
        desc.setForeground(new Color(200, 245, 220));

        JButton backBtn = new JButton("← Menú");
        backBtn.setFont(Constants.FONT_BODY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(34, 130, 78));
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
        helpBtn.setBackground(new Color(30, 120, 70));
        helpBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        helpBtn.setFocusPainted(false);
        helpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpBtn.addActionListener(e ->
                new HelpDialog((JFrame) SwingUtilities.getWindowAncestor(this), 2).setVisible(true));

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
                "<html>Kruskal elige la conexión de <b>menor costo</b> que no forme ciclo.<br>" +
                        "Al terminar, <b>haz clic en una arista roja</b> (descartada)<br>" +
                        "para confirmar que entiendes por qué fue rechazada.</html>",
                new Color(230, 255, 240)));

        infoPanel.add(makeInfoCard("💰 ¿Qué es el costo?",
                "<html><b style='color:#27ae60'>1–3</b> → Alta confianza, fácil reconectar<br>" +
                        "<b style='color:#e67e22'>4–6</b> → Esfuerzo moderado<br>" +
                        "<b style='color:#e55050'>7–9</b> → Herida profunda, difícil<br>" +
                        "Kruskal prioriza el menor costo.</html>",
                new Color(255, 250, 225)));

        infoPanel.add(makeInfoCard("🖥️ ¿Qué verás?",
                "<html><b style='color:#27ae60'>━━━ Verde</b> → Conexión aceptada (MST)<br>" +
                        "<b style='color:#e55050'>- - - Rojo</b> → Descartada (formaría ciclo)<br>" +
                        "Al terminar haz clic en una arista <b>roja</b><br>" +
                        "para completar la misión.</html>",
                new Color(240, 240, 255)));

        // ── Grafo + Log visual ───────────────────────────────────
        graphPanel = new GraphPanel(graph);

        visualLog = new VisualLogPanel("Decisiones de Kruskal");
        visualLog.setPreferredSize(new Dimension(380, 400));

        statusLabel = new JLabel("Presiona 'Ejecutar Kruskal' o 'Paso a paso'");
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

        runBtn   = makeButton("▶ Ejecutar Kruskal", new Color(46, 160, 100));
        stepBtn  = makeButton("⏭ Paso a paso",       new Color(52, 120, 210));
        resetBtn = makeButton("↺ Reiniciar",          new Color(160, 160, 170));
        stepBtn.setEnabled(false);

        runBtn.addActionListener(e -> runKruskalFull());
        stepBtn.addActionListener(e -> stepKruskal());
        resetBtn.addActionListener(e -> resetAll());

        panel.add(runBtn);
        panel.add(stepBtn);
        panel.add(resetBtn);
        return panel;
    }

    private void runKruskalFull() {
        prepareKruskal();
        runBtn.setEnabled(false);
        stepBtn.setEnabled(false);
        if (animTimer != null) animTimer.stop();
        currentStep = 0;

        animTimer = new javax.swing.Timer(600, e -> {
            if (currentStep < allSortedEdges.size()) {
                applyStep(currentStep);
                currentStep++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                onKruskalComplete();
            }
        });
        animTimer.start();
    }

    private void stepKruskal() {
        if (currentStep == 0) prepareKruskal();
        if (currentStep < allSortedEdges.size()) {
            applyStep(currentStep);
            currentStep++;
            if (currentStep >= allSortedEdges.size()) onKruskalComplete();
        }
    }

    private void prepareKruskal() {
        graphPanel.reset();
        visualLog.clear();
        kruskalDone = false;
        controller.runKruskal();

        Kruskal k     = controller.getKruskal();
        // DEBUG TEMPORAL
        System.out.println("Total aristas grafo: " + graph.getAllEdges().size());
        System.out.println("MST size: " + k.getMstEdges().size());
        System.out.println("Rejected size: " + k.getRejectedEdges().size());
        System.out.println("Nodos: " + graph.getNodes().size());
        mstEdges      = k.getMstEdges();
        rejectedEdges = k.getRejectedEdges();

        allSortedEdges = new ArrayList<>(graph.getAllEdges());
        allSortedEdges.sort(java.util.Comparator.comparingInt(Edge::getWeight));

        visualLog.addCard("📋", "Aristas ordenadas por costo",
                allSortedEdges.size() + " conexiones a evaluar (menor → mayor costo)",
                StepCard.Type.INFO);
        stepBtn.setEnabled(true);
        statusLabel.setText("Kruskal listo — observa cada decisión en el grafo");
    }

    private void applyStep(int idx) {
        Edge edge = allSortedEdges.get(idx);
        boolean accepted = mstEdges.stream().anyMatch(e ->
                e.getSource().getId().equals(edge.getSource().getId()) &&
                        e.getTarget().getId().equals(edge.getTarget().getId()));

        if (accepted) {
            graphPanel.addMSTEdge(edge);
            visualLog.addCard("✅", edge.getSource().getName() + " ↔ " + edge.getTarget().getName(),
                    "Costo " + edge.getWeight() + " — conexión aceptada", StepCard.Type.SUCCESS);
            statusLabel.setText("✅ Aceptada: " + edge.getSource().getName()
                    + " ↔ " + edge.getTarget().getName() + "  [costo: " + edge.getWeight() + "]");
        } else {
            graphPanel.addRejectedEdge(edge);
            visualLog.addCard("❌", edge.getSource().getName() + " ↔ " + edge.getTarget().getName(),
                    "Costo " + edge.getWeight() + " — formaría un ciclo, descartada", StepCard.Type.ERROR);
            statusLabel.setText("❌ Descartada: " + edge.getSource().getName()
                    + " ↔ " + edge.getTarget().getName() + " — ciclo");
        }
    }

    private void onKruskalComplete() {
        kruskalDone = true;
        Kruskal k = controller.getKruskal();
        visualLog.addCard("🌐", "Red reconstruida",
                "Costo total: " + k.getTotalCost() + " puntos de esfuerzo social",
                StepCard.Type.SUCCESS);
        visualLog.addCard("🎯", "¡Tu turno!",
                "Haz clic en una arista <b>roja</b> (descartada) para demostrar que entiendes por qué fue rechazada.",
                StepCard.Type.WARNING);
        statusLabel.setText("🎯 Haz clic en una arista roja (descartada) para completar la misión");

        // DEBUG TEMPORAL - borrar después
        System.out.println("=== ARISTAS RECHAZADAS ===");
        for (Edge e : rejectedEdges) {
            System.out.println("  " + e.getSource().getId() + "-" + e.getTarget().getId()
                    + "  " + e.getSource().getName() + " ↔ " + e.getTarget().getName());
        }
        System.out.println("=== MST ===");
        for (Edge e : mstEdges) {
            System.out.println("  " + e.getSource().getId() + "-" + e.getTarget().getId()
                    + "  " + e.getSource().getName() + " ↔ " + e.getTarget().getName());
        }

        // Activar clic en el grafo para detectar arista rechazada
        graphPanel.enableEdgeClickMode(this::handleRejectedEdgeClick);    }

    // El jugador hace clic en un nodo que pertenece a una arista rechazada
    private void handleRejectedEdgeClick(Edge edge) {
        if (!kruskalDone) return;

        boolean isRejected = rejectedEdges.stream().anyMatch(e ->
                (e.getSource().getId().equals(edge.getSource().getId()) &&
                        e.getTarget().getId().equals(edge.getTarget().getId())) ||
                        (e.getSource().getId().equals(edge.getTarget().getId()) &&
                                e.getTarget().getId().equals(edge.getSource().getId())));

        if (!isRejected) {
            visualLog.addCard("ℹ️", "Esa arista fue aceptada",
                    "Está en el MST. Busca una arista <b>roja punteada</b>.",
                    StepCard.Type.INFO);
            return;
        }

        graphPanel.disableEdgeClickMode();
        Kruskal k = controller.getKruskal();

        String explanation = "<html>" +
                "<b>🎯 ¡Correcto! Identificaste una arista descartada.</b><br><br>" +
                "<b>Arista seleccionada:</b> " + edge.getSource().getName()
                + " ↔ " + edge.getTarget().getName()
                + "  [costo: " + edge.getWeight() + "]<br><br>" +
                "<b>¿Por qué fue rechazada?</b><br>" +
                "Porque " + edge.getSource().getName() + " y "
                + edge.getTarget().getName()
                + " ya estaban conectados indirectamente.<br>" +
                "Agregarla habría creado un <b>ciclo</b> — conexión redundante.<br><br>" +
                "<b>Costo total de la red reconstruida:</b> " + k.getTotalCost() + " puntos<br><br>" +
                "<i>Kruskal garantiza la red más económica posible.</i></html>";
        GameController.getInstance().completeMission(2, 3);
        int opt = JOptionPane.showOptionDialog(this, explanation,
                "🔗 Misión 3 Completada",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"✅ Volver al menú", "↺ Intentar de nuevo"}, null);

        if (opt == 0) {
            GameController.getInstance().completeMission(2, 3);
            window.showPanel("MENU");
        }
        else resetAll();
    }

    private void resetAll() {
        if (animTimer != null) animTimer.stop();
        currentStep = 0;
        kruskalDone = false;
        graphPanel.disableEdgeClickMode();
        graphPanel.reset();
        visualLog.clear();
        runBtn.setEnabled(true);
        stepBtn.setEnabled(false);
        statusLabel.setText("Presiona 'Ejecutar Kruskal' o 'Paso a paso'");
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