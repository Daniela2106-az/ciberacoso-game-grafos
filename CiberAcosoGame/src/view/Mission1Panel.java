package view;

import controller.MissionController;
import model.graph.Graph;
import model.graph.Node;
import util.Constants;
import util.GraphGenerator;
import view.components.StepCard;
import view.components.VisualLogPanel;
import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Mission1Panel extends JPanel {
    private MainWindow window;
    private Graph graph;
    private MissionController controller;
    private GraphPanel graphPanel;
    private VisualLogPanel visualLog;
    private JLabel statusLabel;
    private List<Node> visitOrder;
    private int currentStep = 0;
    private javax.swing.Timer animTimer;

    public Mission1Panel(MainWindow window) {
        this.window = window;
        this.graph = GraphGenerator.generateMission1Graph();
        this.controller = new MissionController(graph);
        setBackground(Constants.BG_MAIN);
        setLayout(new BorderLayout(10, 10));
        buildUI();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(229, 80, 80));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("🔍 Misión 1 – Rastros del Acoso");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Analiza las conexiones entre usuarios y descubre quién inició el ciberacoso");
        desc.setFont(Constants.FONT_BODY);
        desc.setForeground(new Color(255, 220, 220));

        JButton backBtn = new JButton("← Menú");
        backBtn.setFont(Constants.FONT_BODY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(200, 60, 60));
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
        helpBtn.setBackground(new Color(180, 50, 50));
        helpBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        helpBtn.setFocusPainted(false);
        helpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpBtn.addActionListener(e ->
                new HelpDialog((JFrame) SwingUtilities.getWindowAncestor(this), 0).setVisible(true));

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
                "<html>Ejecuta BFS o DFS desde cualquier nodo. Cuando termine,<br>" +
                        "haz clic en el nodo que crees que <b>inició el acoso</b>.</html>",
                new Color(255, 243, 230)));

        infoPanel.add(makeInfoCard("👤 Roles en el grafo",
                "<html><b style='color:#e55050'>🔴 Bully</b> – Inicia o amplifica el acoso<br>" +
                        "<b style='color:#cc8800'>🟡 Víctima</b> – Receptor del acoso<br>" +
                        "<b style='color:#27ae60'>🟢 Apoyo</b> – Defiende a la víctima<br>" +
                        "<b style='color:#4169e1'>🔵 Bystander</b> – Observa sin actuar</html>",
                new Color(240, 248, 255)));

        infoPanel.add(makeInfoCard("🔗 Pesos en las aristas",
                "<html>El número indica la <b>intensidad de la interacción</b>.<br>" +
                        "Mayor número = mayor impacto emocional del mensaje.</html>",
                new Color(240, 255, 245)));

        // ── Centro: grafo + log visual ───────────────────────────
        graphPanel = new GraphPanel(graph);

        visualLog = new VisualLogPanel("¿Qué está pasando?");
        visualLog.setPreferredSize(new Dimension(500, 400));

        statusLabel = new JLabel("Selecciona un algoritmo para comenzar");
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

        JLabel startLabel = new JLabel("Nodo inicio:");
        startLabel.setFont(Constants.FONT_BODY);
        startLabel.setForeground(Constants.TEXT_DARK);

        JComboBox<String> nodeSelector = new JComboBox<>();
        for (Node n : graph.getAllNodes())
            nodeSelector.addItem(n.getId() + " – " + n.getName());
        nodeSelector.setFont(Constants.FONT_BODY);
        nodeSelector.setPreferredSize(new Dimension(180, 36));

        JButton bfsBtn  = makeButton("▶ Ejecutar BFS", Constants.ACCENT);
        JButton dfsBtn  = makeButton("▶ Ejecutar DFS", new Color(120, 90, 200));
        JButton stepBtn = makeButton("⏭ Paso a paso",  new Color(60, 170, 120));
        JButton resetBtn= makeButton("↺ Reiniciar",    new Color(160, 160, 170));

        stepBtn.setEnabled(false);

        bfsBtn.addActionListener(e -> {
            String selected = (String) nodeSelector.getSelectedItem();
            String nodeId   = selected.split(" – ")[0].trim();
            visitOrder      = controller.runBFS(nodeId);
            currentStep     = 0;
            graphPanel.reset();
            stepBtn.setEnabled(true);
            animateVisit();
        });

        dfsBtn.addActionListener(e -> {
            String selected = (String) nodeSelector.getSelectedItem();
            String nodeId   = selected.split(" – ")[0].trim();
            visitOrder      = controller.runDFS(nodeId);
            currentStep     = 0;
            graphPanel.reset();
            stepBtn.setEnabled(true);
            animateVisit();
        });

        stepBtn.addActionListener(e -> {
            if (visitOrder != null && currentStep < visitOrder.size()) {
                Node n = visitOrder.get(currentStep);
                graphPanel.highlightNode(n);
                addNodeCard(n);
                currentStep++;
                statusLabel.setText("Paso " + currentStep + " de " + visitOrder.size());
            } else {
                statusLabel.setText("✅ Recorrido completado");
                stepBtn.setEnabled(false);
                visualLog.addCard("🎯", "¡Listo!", "Haz clic en el acosador principal.", StepCard.Type.SUCCESS);
                graphPanel.enableClickMode(this::evaluateSuspect);
            }
        });

        resetBtn.addActionListener(e -> {
            if (animTimer != null) animTimer.stop();
            graphPanel.reset();
            visualLog.clear();
            currentStep = 0;
            stepBtn.setEnabled(false);
            statusLabel.setText("Selecciona un algoritmo para comenzar");
        });

        panel.add(startLabel);
        panel.add(nodeSelector);
        panel.add(bfsBtn);
        panel.add(dfsBtn);
        panel.add(stepBtn);
        panel.add(resetBtn);
        return panel;
    }

    private void animateVisit() {
        if (animTimer != null) animTimer.stop();
        currentStep = 0;
        graphPanel.disableClickMode();
        visualLog.clear();
        visualLog.addCard("🚀", "Algoritmo iniciado",
                "Rastreando conexiones desde el nodo seleccionado...",
                StepCard.Type.ACTIVE);

        animTimer = new javax.swing.Timer(700, e -> {
            if (visitOrder != null && currentStep < visitOrder.size()) {
                Node n = visitOrder.get(currentStep);
                graphPanel.highlightNode(n);
                addNodeCard(n);
                statusLabel.setText("Visitando: " + n.getName()
                        + "  (" + (currentStep + 1) + "/" + visitOrder.size() + ")");
                currentStep++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                visualLog.addCard("🎯", "Recorrido completado",
                        "Haz clic en el nodo que inició el acoso.",
                        StepCard.Type.SUCCESS);
                statusLabel.setText("🎯 Haz clic en el acosador principal");
                graphPanel.enableClickMode(this::evaluateSuspect);
            }
        });
        animTimer.start();
    }

    private void addNodeCard(Node n) {
        String rol = switch (n.getBehavior()) {
            case "bully"     -> "🔴 Acosador";
            case "victim"    -> "🟡 Víctima";
            case "supporter" -> "🟢 Defensor";
            default          -> "🔵 Espectador";
        };
        StepCard.Type tipo = switch (n.getBehavior()) {
            case "bully"     -> StepCard.Type.ERROR;
            case "victim"    -> StepCard.Type.WARNING;
            case "supporter" -> StepCard.Type.SUCCESS;
            default          -> StepCard.Type.INFO;
        };
        visualLog.addCard(rol.substring(0, 2), n.getName(), rol, tipo);
    }

    private void evaluateSuspect(model.graph.Node node) {
        graphPanel.disableClickMode();
        String correctId = "A";
        boolean correct  = node.getId().equals(correctId);

        String title = correct ? "✅ ¡Correcto!" : "❌ Incorrecto";
        String message;
        if (correct) {
            message = "<html><b>" + node.getName() + "</b> es el acosador principal.<br><br>" +
                    "El algoritmo reveló que fue el primer nodo en conectar<br>" +
                    "con múltiples usuarios propagando mensajes negativos.<br><br>" +
                    "<i>Misión completada.</i></html>";
        } else {
            message = "<html><b>" + node.getName() + "</b> no inició el acoso.<br><br>" +
                    "Rol: <b>" + node.getBehavior() + "</b><br>" +
                    "Este usuario solo replicaba mensajes o era espectador.<br><br>" +
                    "<i>Analiza mejor el recorrido e inténtalo de nuevo.</i></html>";
        }

        int opt = JOptionPane.showOptionDialog(this, message, title,
                JOptionPane.DEFAULT_OPTION,
                correct ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE,
                null,
                correct ? new String[]{"🏠 Menú principal"} : new String[]{"↺ Intentar de nuevo"},
                null);

        if (correct && opt== 0) {
            // 1. Guardar progreso (Índice 0 = Misión 1, 3 estrellas)
            GameController.getInstance().completeMission(0, 3);

            // 3. Volver al menú (el addNotify del MenuPanel hará el resto)
            window.showPanel("MENU");
        } else {
            graphPanel.reset();
            visualLog.clear();
            statusLabel.setText("Selecciona un algoritmo para comenzar");
        }
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(Constants.FONT_BODY);
        btn.setBackground(bg);
        int lum = (bg.getRed() * 299 + bg.getGreen() * 587 + bg.getBlue() * 114) / 1000;
        btn.setForeground(lum > 160 ? Color.BLACK : Color.WHITE);
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