package view;

import controller.GameController;
import controller.MissionController;
import model.algorithms.Dijkstra;
import model.algorithms.FordFulkerson;
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
import java.util.*;
import java.util.List;

public class FinalMissionPanel extends JPanel {

    private MainWindow window;
    private Graph graph;
    private MissionController controller;
    private GraphPanel graphPanel;
    private VisualLogPanel visualLog;
    private JLabel statusLabel;
    private javax.swing.Timer animTimer;
    private javax.swing.Timer blinkTimer;

    private int currentPhase = 0;

    // Estadísticas
    private int bfsStepsToOrigin = 0;
    private int dijkstraRisk     = 0;
    private int maxFlowDetected  = 0;
    private int kruskalCost      = 0;

    // Fase 1
    private List<Node> visitOrder;
    private Node detectedOrigin;
    private int currentStep = 0;

    // Fase 2
    private Node dijkstraSource = null;
    private Node dijkstraTarget = null;
    private List<Node> dijkstraPath;
    private JLabel phase2SrcLabel, phase2TgtLabel;
    private JButton phase2RunBtn;

    // Fase 3
    private Node ffSource = null;
    private Node ffSink   = null;
    private JLabel phase3SrcLabel, phase3SinkLabel;
    private JButton phase3RunBtn;
    private FordFulkerson lastFF = null;

    // Fase 4
    private JButton phase4RunBtn;
    private List<Edge> kruskalRejected;
    private List<Edge> kruskalMST;
    private boolean phase4Done = false;

    // CardLayout fases
    private JPanel phaseContainer;
    private CardLayout phaseCard;
    private JLabel[] phaseIndicators = new JLabel[4];

    public FinalMissionPanel(MainWindow window) {
        this.window     = window;
        this.graph      = GraphGenerator.generateFinalMissionGraph();
        this.controller = new MissionController(graph);
        setBackground(new Color(18, 22, 38));
        setLayout(new BorderLayout(8, 8));
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ───────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 10, 50));
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("⭐ Misión Final – Red Segura");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(new Color(255, 220, 80));

        JLabel desc = new JLabel("La red está en crisis. Aplica todos los algoritmos para restaurarla.");
        desc.setFont(Constants.FONT_BODY);
        desc.setForeground(new Color(180, 160, 220));

        JButton backBtn = new JButton("← Menú");
        backBtn.setFont(Constants.FONT_BODY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(60, 20, 90));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { stopTimers(); window.showPanel("MENU"); });

        JButton helpBtn = new JButton("❓ Ayuda");
        helpBtn.setFont(Constants.FONT_BODY);
        helpBtn.setForeground(Color.WHITE);
        helpBtn.setBackground(new Color(60, 20, 90));
        helpBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        helpBtn.setFocusPainted(false);
        helpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpBtn.addActionListener(e ->
                new HelpDialog((JFrame) SwingUtilities.getWindowAncestor(this), 4).setVisible(true));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(desc);

        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerBtns.setOpaque(false);
        headerBtns.add(helpBtn);
        headerBtns.add(backBtn);

        header.add(titleBox,    BorderLayout.CENTER);
        header.add(headerBtns, BorderLayout.EAST);
        return header;
    }

    // ── Centro ───────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(8, 0));
        center.setBackground(new Color(18, 22, 38));
        center.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));

        graphPanel = new GraphPanel(graph);
        graphPanel.setBackground(new Color(24, 28, 46));
        graphPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 50, 120), 2));

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(new Color(18, 22, 38));
        rightPanel.setPreferredSize(new Dimension(370, 0));
        rightPanel.add(buildProgressPanel(), BorderLayout.NORTH);
        rightPanel.add(buildPhasePanel(),    BorderLayout.CENTER);
        rightPanel.add(buildLogPanel(),      BorderLayout.SOUTH);

        center.add(graphPanel, BorderLayout.CENTER);
        center.add(rightPanel, BorderLayout.EAST);
        return center;
    }

    // ── Barra de progreso ────────────────────────────────────────
    private JPanel buildProgressPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 4));
        panel.setBackground(new Color(28, 32, 52));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 50, 120), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        String[] labels = {
                "🔍 Fase 1: Localizar origen",
                "🛡️ Fase 2: Intervenir",
                "🌊 Fase 3: Contener flujo",
                "🔗 Fase 4: Reconstruir"
        };
        for (int i = 0; i < 4; i++) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(new Color(28, 32, 52));
            JLabel num = new JLabel(labels[i]);
            num.setFont(Constants.FONT_BODY);
            num.setForeground(i == 0 ? new Color(255, 220, 80) : new Color(80, 80, 110));
            JLabel status = new JLabel(i == 0 ? "▶ En curso" : "🔒");
            status.setFont(Constants.FONT_SMALL);
            status.setForeground(i == 0 ? new Color(255, 220, 80) : new Color(60, 60, 90));
            status.setHorizontalAlignment(SwingConstants.RIGHT);
            row.add(num, BorderLayout.CENTER);
            row.add(status, BorderLayout.EAST);
            panel.add(row);
            phaseIndicators[i] = status;
        }
        return panel;
    }

    // ── CardLayout fases ─────────────────────────────────────────
    private JPanel buildPhasePanel() {
        phaseCard      = new CardLayout();
        phaseContainer = new JPanel(phaseCard);
        phaseContainer.setBackground(new Color(28, 32, 52));
        phaseContainer.add(buildPhase1Panel(), "PHASE1");
        phaseContainer.add(buildPhase2Panel(), "PHASE2");
        phaseContainer.add(buildPhase3Panel(), "PHASE3");
        phaseContainer.add(buildPhase4Panel(), "PHASE4");
        phaseCard.show(phaseContainer, "PHASE1");
        return phaseContainer;
    }

    // ── Log ──────────────────────────────────────────────────────
    private JPanel buildLogPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(18, 22, 38));
        wrapper.setPreferredSize(new Dimension(370, 190));
        visualLog = new VisualLogPanel("Registro de operaciones");
        visualLog.setBackground(new Color(20, 24, 40));
        wrapper.add(visualLog, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        bar.setBackground(new Color(12, 14, 26));
        statusLabel = new JLabel("⚠️  Red en crisis — Fase 1: Localiza el origen del acoso");
        statusLabel.setFont(Constants.FONT_BODY);
        statusLabel.setForeground(new Color(255, 180, 80));
        bar.add(statusLabel);
        return bar;
    }

    // ═══════════════════════════════════════════════════════════════
    //  FASE 1 — BFS/DFS → acusar al Paciente Cero
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildPhase1Panel() {
        JPanel panel = darkCard();
        JLabel title = darkTitle("🔍 Fase 1 – Localizar el origen", new Color(255, 220, 80));
        JLabel info  = darkInfo("<html>Ejecuta BFS o DFS. Cuando termine,<br>" +
                "haz clic en el nodo que crees que<br><b>inició el acoso</b>.</html>");

        JLabel startLbl = darkInfo("Nodo inicio:");
        JComboBox<String> nodeSelector = new JComboBox<>();
        for (Node n : graph.getAllNodes())
            nodeSelector.addItem(n.getId() + " – " + n.getName());
        nodeSelector.setFont(Constants.FONT_SMALL);
        nodeSelector.setBackground(new Color(40, 44, 70));
        nodeSelector.setForeground(Color.WHITE);
        nodeSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nodeSelector.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton bfsBtn = accentBtn("▶ BFS", new Color(52, 120, 210));
        JButton dfsBtn = accentBtn("▶ DFS", new Color(120, 60, 200));

        bfsBtn.addActionListener(e -> {
            String sel = (String) nodeSelector.getSelectedItem();
            visitOrder = controller.runBFS(sel.split(" – ")[0].trim());
            currentStep = 0; graphPanel.reset(); animatePhase1(true);
        });
        dfsBtn.addActionListener(e -> {
            String sel = (String) nodeSelector.getSelectedItem();
            visitOrder = controller.runDFS(sel.split(" – ")[0].trim());
            currentStep = 0; graphPanel.reset(); animatePhase1(false);
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(bfsBtn); btnRow.add(dfsBtn);

        panel.add(title); panel.add(info);
        panel.add(Box.createVerticalStrut(8));
        panel.add(startLbl); panel.add(nodeSelector);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnRow);
        return panel;
    }

    private void animatePhase1(boolean isBFS) {
        stopTimers(); currentStep = 0;
        detectedOrigin = graph.getNode("A");
        visualLog.clear();
        visualLog.addCard("🚀", "Rastreo iniciado",
                (isBFS ? "BFS" : "DFS") + " recorriendo la red...", StepCard.Type.ACTIVE);

        animTimer = new javax.swing.Timer(600, e -> {
            if (currentStep < visitOrder.size()) {
                Node n = visitOrder.get(currentStep);
                graphPanel.highlightNode(n);
                visualLog.addCard(rolLabel(n.getBehavior()).substring(0, 2),
                        n.getName(), rolLabel(n.getBehavior()), cardType(n.getBehavior()));
                statusLabel.setText("🔍 Rastreando: " + n.getName()
                        + "  (" + (currentStep + 1) + "/" + visitOrder.size() + ")");
                currentStep++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                bfsStepsToOrigin = visitOrder.indexOf(detectedOrigin) + 1;
                visualLog.addCard("🎯", "Recorrido completado",
                        "Haz clic en el nodo que inició el acoso.", StepCard.Type.WARNING);
                statusLabel.setText("🎯 Haz clic en el acosador principal para acusarlo");
                startBlinking(detectedOrigin);
                graphPanel.enableClickMode(this::evaluateSuspectPhase1);
            }
        });
        animTimer.start();
    }

    private void evaluateSuspectPhase1(Node node) {
        if (!node.getId().equals(detectedOrigin.getId())) {
            visualLog.addCard("❌", node.getName() + " no es el origen",
                    rolLabel(node.getBehavior()) + " — sigue analizando.", StepCard.Type.ERROR);
            return;
        }
        graphPanel.disableClickMode(); stopTimers();
        visualLog.addCard("✅", "¡" + detectedOrigin.getName() + " acusado!",
                "Detectado en el paso " + bfsStepsToOrigin + ".", StepCard.Type.SUCCESS);
        statusLabel.setText("✅ Paciente Cero identificado");
        phaseIndicators[0].setText("✅");
        phaseIndicators[0].setForeground(new Color(80, 220, 140));

        JOptionPane.showMessageDialog(this,
                "<html><b>✅ ¡Paciente Cero identificado!</b><br><br>" +
                        "<b>" + detectedOrigin.getName() + "</b> fue el origen del acoso.<br>" +
                        "Detectado en el paso <b>" + bfsStepsToOrigin + "</b>.<br><br>" +
                        "<i>Ahora debes intervenir para proteger a las víctimas.</i></html>",
                "Fase 1 Completada", JOptionPane.INFORMATION_MESSAGE);
        doAdvancePhase(0);
    }

    private void startBlinking(Node node) {
        final boolean[] on = {true};
        blinkTimer = new javax.swing.Timer(400, e -> {
            if (on[0]) graphPanel.highlightNode(node);
            else {
                // Solo limpiar highlighted, no tocar modo clic
                graphPanel.clearHighlights();
            }
            on[0] = !on[0];
        });
        blinkTimer.start();
    }

    // ═══════════════════════════════════════════════════════════════
    //  FASE 2 — DIJKSTRA → clic en destino para confirmar
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildPhase2Panel() {
        JPanel panel = darkCard();
        JLabel title = darkTitle("🛡️ Fase 2 – Intervención", new Color(100, 180, 255));
        JLabel info  = darkInfo("<html>Selecciona <b>origen</b> y <b>destino</b>.<br>" +
                "Dijkstra calculará la ruta más segura.<br>" +
                "Luego <b>haz clic en el destino</b> para confirmar.</html>");

        phase2SrcLabel = dimLabel("🟢 Origen: (clic en el grafo)");
        phase2TgtLabel = dimLabel("🎯 Destino: (clic en el grafo)");
        phase2RunBtn   = accentBtn("🛡️ Calcular Ruta", new Color(52, 120, 210));
        phase2RunBtn.setEnabled(false);
        phase2RunBtn.addActionListener(e -> runPhase2());

        panel.add(title); panel.add(info);
        panel.add(Box.createVerticalStrut(10));
        panel.add(phase2SrcLabel); panel.add(Box.createVerticalStrut(4));
        panel.add(phase2TgtLabel); panel.add(Box.createVerticalStrut(10));
        panel.add(phase2RunBtn);
        return panel;
    }

    private void setupPhase2() {
        stopTimers(); graphPanel.reset();
        dijkstraSource = null; dijkstraTarget = null;
        phase2RunBtn.setEnabled(false);
        visualLog.clear();
        visualLog.addCard("👆", "Selecciona el nodo origen",
                "Haz clic en cualquier nodo.", StepCard.Type.INFO);

        graphPanel.enableClickMode(node -> {
            if (dijkstraSource == null) {
                dijkstraSource = node;
                phase2SrcLabel.setText("🟢 Origen: " + node.getName());
                graphPanel.highlightNode(node);
                visualLog.addCard("🟢", "Origen: " + node.getName(),
                        rolLabel(node.getBehavior()), StepCard.Type.SUCCESS);
                statusLabel.setText("Selecciona el nodo destino");
            } else if (dijkstraTarget == null && !node.getId().equals(dijkstraSource.getId())) {
                dijkstraTarget = node;
                phase2TgtLabel.setText("🎯 Destino: " + node.getName());
                graphPanel.highlightNode(node);
                visualLog.addCard("🎯", "Destino: " + node.getName(),
                        rolLabel(node.getBehavior()), StepCard.Type.WARNING);
                phase2RunBtn.setEnabled(true);
                statusLabel.setText("Presiona 'Calcular Ruta'");
            }
        });
        statusLabel.setText("🛡️ Fase 2: Selecciona el nodo origen");
    }

    private void runPhase2() {
        graphPanel.disableClickMode(); phase2RunBtn.setEnabled(false);
        visualLog.clear();
        visualLog.addCard("⚙️", "Dijkstra ejecutándose",
                "Calculando ruta de menor riesgo...", StepCard.Type.ACTIVE);

        Dijkstra dij = controller.getDijkstra();
        dij.setOnDistUpdate((id, dist) ->
                SwingUtilities.invokeLater(() -> graphPanel.updateDistLabel(id, dist)));

        dijkstraPath = controller.runDijkstra(dijkstraSource.getId(), dijkstraTarget.getId());
        dijkstraRisk = controller.getDijkstraDistance(dijkstraTarget.getId());

        if (dijkstraPath.isEmpty()) {
            visualLog.addCard("❌", "Sin ruta", "No hay camino posible.", StepCard.Type.ERROR);
            statusLabel.setText("❌ No hay ruta posible");
            return;
        }

        stopTimers();
        final int[] step = {0};
        graphPanel.reset();
        for (Node n : graph.getAllNodes())
            graphPanel.updateDistLabel(n.getId(), dij.getDistance(n.getId()));

        animTimer = new javax.swing.Timer(700, e -> {
            if (step[0] < dijkstraPath.size()) {
                Node n = dijkstraPath.get(step[0]);
                graphPanel.highlightNode(n);
                int dist = dij.getDistance(n.getId());
                visualLog.addCard("➡️", n.getName(),
                        "Riesgo acumulado: " + (dist == Integer.MAX_VALUE ? "∞" : dist),
                        StepCard.Type.ACTIVE);
                statusLabel.setText("➡️ " + n.getName()
                        + "  (" + (step[0] + 1) + "/" + dijkstraPath.size() + ")");
                step[0]++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                graphPanel.showFinalPath(dijkstraPath);
                visualLog.addCard("✅", "Ruta encontrada",
                        "Riesgo: " + dijkstraRisk + " — haz clic en <b>"
                                + dijkstraTarget.getName() + "</b> para confirmar.",
                        StepCard.Type.SUCCESS);
                statusLabel.setText("🎯 Haz clic en '"
                        + dijkstraTarget.getName() + "' para confirmar");
                graphPanel.enableClickMode(this::confirmPhase2);
            }
        });
        animTimer.start();
    }

    private void confirmPhase2(Node node) {
        if (!node.getId().equals(dijkstraTarget.getId())) {
            visualLog.addCard("❌", "Nodo incorrecto",
                    "Haz clic en <b>" + dijkstraTarget.getName() + "</b>.",
                    StepCard.Type.ERROR);
            return;
        }
        graphPanel.disableClickMode();
        visualLog.addCard("🏆", "¡Intervención confirmada!",
                "Riesgo: " + dijkstraRisk + " — Fase 2 completada.", StepCard.Type.SUCCESS);
        statusLabel.setText("✅ Intervención confirmada — Fase 2 completada");
        phaseIndicators[1].setText("✅");
        phaseIndicators[1].setForeground(new Color(80, 220, 140));

        JOptionPane.showMessageDialog(this,
                "<html><b>🛡️ ¡Intervención exitosa!</b><br><br>" +
                        "Ruta: <b>" + buildPathStr(dijkstraPath) + "</b><br>" +
                        "Riesgo total: <b>" + dijkstraRisk + " unidades</b><br><br>" +
                        "<i>Has llegado al usuario afectado por el canal más seguro.</i></html>",
                "Fase 2 Completada", JOptionPane.INFORMATION_MESSAGE);
        doAdvancePhase(1);
    }

    // ═══════════════════════════════════════════════════════════════
    //  FASE 3 — FORD-FULKERSON → elegir arista a bloquear
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildPhase3Panel() {
        JPanel panel = darkCard();
        JLabel title = darkTitle("🌊 Fase 3 – Contener el Flujo", new Color(200, 100, 255));
        JLabel info  = darkInfo("<html>Selecciona <b>fuente</b> y <b>sumidero</b>.<br>" +
                "Ford-Fulkerson calculará el flujo máximo.<br>" +
                "Luego <b>elige qué conexión bloquear</b>.</html>");

        phase3SrcLabel  = dimLabel("🔴 Fuente:   (clic en el grafo)");
        phase3SinkLabel = dimLabel("🟡 Sumidero: (clic en el grafo)");
        phase3RunBtn    = accentBtn("🌊 Simular Flujo", new Color(160, 50, 200));
        phase3RunBtn.setEnabled(false);
        phase3RunBtn.addActionListener(e -> runPhase3());

        panel.add(title); panel.add(info);
        panel.add(Box.createVerticalStrut(10));
        panel.add(phase3SrcLabel); panel.add(Box.createVerticalStrut(4));
        panel.add(phase3SinkLabel); panel.add(Box.createVerticalStrut(10));
        panel.add(phase3RunBtn);
        return panel;
    }

    private void setupPhase3() {
        stopTimers(); graphPanel.reset();
        ffSource = null; ffSink = null;
        phase3RunBtn.setEnabled(false);
        visualLog.clear();
        visualLog.addCard("👆", "Selecciona la fuente",
                "Haz clic en el nodo origen.", StepCard.Type.INFO);

        graphPanel.enableClickMode(node -> {
            if (ffSource == null) {
                ffSource = node;
                phase3SrcLabel.setText("🔴 Fuente: " + node.getName());
                graphPanel.highlightNode(node);
                visualLog.addCard("🔴", "Fuente: " + node.getName(),
                        rolLabel(node.getBehavior()), StepCard.Type.ERROR);
                statusLabel.setText("Selecciona el nodo sumidero");
            } else if (ffSink == null && !node.getId().equals(ffSource.getId())) {
                ffSink = node;
                phase3SinkLabel.setText("🟡 Sumidero: " + node.getName());
                graphPanel.highlightNode(node);
                visualLog.addCard("🟡", "Sumidero: " + node.getName(),
                        rolLabel(node.getBehavior()), StepCard.Type.WARNING);
                phase3RunBtn.setEnabled(true);
                statusLabel.setText("Presiona 'Simular Flujo'");
            }
        });
        statusLabel.setText("🌊 Fase 3: Selecciona el nodo fuente");
    }

    private void runPhase3() {
        graphPanel.disableClickMode(); phase3RunBtn.setEnabled(false);
        visualLog.clear();
        visualLog.addCard("⚙️", "Ford-Fulkerson iniciado",
                "Buscando caminos de aumento...", StepCard.Type.ACTIVE);

        maxFlowDetected = controller.runFordFulkerson(ffSource.getId(), ffSink.getId());
        lastFF = controller.getFordFulkerson();

        Map<String, int[]> flowData = new HashMap<>();
        for (String u : lastFF.getCapacity().keySet())
            for (String v : lastFF.getCapacity().get(u).keySet()) {
                int cap  = lastFF.getCapacity().get(u).get(v);
                int flow = cap - lastFF.getResidual().get(u).getOrDefault(v, 0);
                flowData.put(u + "-" + v, new int[]{flow, cap});
            }
        graphPanel.setFlowData(flowData);

        List<List<String>> paths = lastFF.getAugmentingPaths();
        List<Integer>      flows = lastFF.getPathFlows();
        stopTimers();
        final int[] idx = {0};

        animTimer = new javax.swing.Timer(1000, e -> {
            graphPanel.clearActivePath();
            if (idx[0] < paths.size()) {
                List<String> path = paths.get(idx[0]);
                int flow = flows.get(idx[0]);
                graphPanel.setActivePath(path);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    sb.append(graph.getNode(path.get(i)).getName());
                    if (i < path.size() - 1) sb.append(" → ");
                }
                visualLog.addCard("🌊", "Ruta " + (idx[0] + 1),
                        sb + " — flujo: " + flow, StepCard.Type.ACTIVE);
                statusLabel.setText("Ruta " + (idx[0] + 1) + " — flujo: " + flow);
                idx[0]++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                graphPanel.clearActivePath();
                graphPanel.setSaturated(lastFF.getSaturatedEdgeKeys());
                for (String key : lastFF.getSaturatedEdgeKeys()) {
                    String[] parts = key.split("-");
                    Node u = graph.getNode(parts[0]), v = graph.getNode(parts[1]);
                    if (u != null && v != null)
                        visualLog.addCard("⚠️", u.getName() + " → " + v.getName(),
                                "Saturada — corte mínimo", StepCard.Type.WARNING);
                }
                visualLog.addCard("🎯", "¡Tu turno!",
                        "Elige qué conexión bloquear.", StepCard.Type.WARNING);
                statusLabel.setText("⚠️ Flujo máximo: " + maxFlowDetected
                        + " — elige una conexión para bloquear");
                showBlockDialogPhase3();
            }
        });
        animTimer.start();
    }

    private void showBlockDialogPhase3() {
        Set<String> saturated = lastFF.getSaturatedEdgeKeys();
        if (saturated.isEmpty()) { completePhase3("ninguna"); return; }

        List<String> options = new ArrayList<>(), optKeys = new ArrayList<>();
        for (String key : saturated) {
            String[] parts = key.split("-");
            Node u = graph.getNode(parts[0]), v = graph.getNode(parts[1]);
            if (u != null && v != null) {
                int cap = lastFF.getCapacity().get(parts[0]).get(parts[1]);
                options.add("✂️  " + u.getName() + " → " + v.getName() + "  [cap: " + cap + "]");
                optKeys.add(key);
            }
        }
        String[] optArr = options.toArray(new String[0]);
        String choice = (String) JOptionPane.showInputDialog(this,
                "<html><b>⚠️ Flujo máximo: " + maxFlowDetected + " unidades/seg</b><br><br>" +
                        "Las aristas <b style='color:red'>rojas</b> están saturadas.<br>" +
                        "Son el <b>corte mínimo</b> — bloquea una para detener el acoso.<br><br>" +
                        "¿Qué conexión vas a bloquear?</html>",
                "🌊 Elegir Corte Mínimo", JOptionPane.QUESTION_MESSAGE,
                null, optArr, optArr[0]);

        if (choice != null) {
            int i = Arrays.asList(optArr).indexOf(choice);
            String[] parts = optKeys.get(i).split("-");
            Node u = graph.getNode(parts[0]), v = graph.getNode(parts[1]);
            completePhase3(u.getName() + " → " + v.getName());
        }
    }

    private void completePhase3(String blocked) {
        visualLog.addCard("✂️", "Bloqueada: " + blocked,
                "Flujo detenido. Fase 3 completada.", StepCard.Type.SUCCESS);
        statusLabel.setText("✅ Fase 3 completada — flujo controlado");
        phaseIndicators[2].setText("✅");
        phaseIndicators[2].setForeground(new Color(80, 220, 140));

        JOptionPane.showMessageDialog(this,
                "<html><b>🌊 ¡Flujo contenido!</b><br><br>" +
                        "Flujo máximo: <b>" + maxFlowDetected + " unidades/seg</b><br>" +
                        "Conexión bloqueada: <b>" + blocked + "</b><br><br>" +
                        "<i>Has identificado y bloqueado el cuello de botella.</i></html>",
                "Fase 3 Completada", JOptionPane.INFORMATION_MESSAGE);
        doAdvancePhase(2);
    }

    // ═══════════════════════════════════════════════════════════════
    //  FASE 4 — KRUSKAL → clic en arista rechazada para completar
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildPhase4Panel() {
        JPanel panel = darkCard();
        JLabel title = darkTitle("🔗 Fase 4 – Reconstrucción Total", new Color(80, 220, 140));
        JLabel info  = darkInfo("<html>Kruskal reconectará a todos.<br>" +
                "Cuando termine, <b>haz clic en una arista roja</b><br>" +
                "(descartada) para completar la restauración.</html>");

        phase4RunBtn = accentBtn("🔗 Reconstruir Red", new Color(46, 160, 100));
        phase4RunBtn.addActionListener(e -> runPhase4());

        panel.add(title); panel.add(info);
        panel.add(Box.createVerticalStrut(16));
        panel.add(phase4RunBtn);
        return panel;
    }

    private void setupPhase4() {
        stopTimers(); graphPanel.reset();
        graphPanel.disableClickMode(); phase4Done = false;
        visualLog.clear();
        visualLog.addCard("🔗", "Fase 4 desbloqueada",
                "Presiona 'Reconstruir Red'.", StepCard.Type.INFO);
        statusLabel.setText("🔗 Fase 4: Presiona 'Reconstruir Red'");
    }

    private void runPhase4() {
        phase4RunBtn.setEnabled(false);
        controller.runKruskal();
        Kruskal k   = controller.getKruskal();
        kruskalCost     = k.getTotalCost();
        kruskalMST      = k.getMstEdges();
        kruskalRejected = k.getRejectedEdges();

        List<Edge> sorted = new ArrayList<>(graph.getAllEdges());
        sorted.sort(java.util.Comparator.comparingInt(Edge::getWeight));

        visualLog.clear();
        visualLog.addCard("📋", "Aristas ordenadas",
                sorted.size() + " conexiones a evaluar", StepCard.Type.INFO);

        stopTimers();
        final int[] step = {0};
        animTimer = new javax.swing.Timer(450, e -> {
            if (step[0] < sorted.size()) {
                Edge edge = sorted.get(step[0]);
                boolean acc = kruskalMST.stream().anyMatch(x ->
                        x.getSource().getId().equals(edge.getSource().getId()) &&
                                x.getTarget().getId().equals(edge.getTarget().getId()));
                if (acc) {
                    graphPanel.addMSTEdge(edge);
                    visualLog.addCard("✅",
                            edge.getSource().getName() + " ↔ " + edge.getTarget().getName(),
                            "Costo " + edge.getWeight() + " — aceptada", StepCard.Type.SUCCESS);
                } else {
                    graphPanel.addRejectedEdge(edge);
                    visualLog.addCard("❌",
                            edge.getSource().getName() + " ↔ " + edge.getTarget().getName(),
                            "Costo " + edge.getWeight() + " — ciclo, descartada",
                            StepCard.Type.ERROR);
                }
                statusLabel.setText((acc ? "✅ " : "❌ ")
                        + edge.getSource().getName() + " ↔ " + edge.getTarget().getName()
                        + "  [" + edge.getWeight() + "]");
                step[0]++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                phase4Done = true;
                visualLog.addCard("🌐", "Red reconstruida — costo: " + kruskalCost,
                        "Haz clic en una arista <b>roja punteada</b> para completar.",
                        StepCard.Type.SUCCESS);
                statusLabel.setText("🎯 Haz clic en una arista roja para completar");
                graphPanel.enableEdgeClickMode(this::confirmPhase4);
            }
        });
        animTimer.start();
    }

    private void confirmPhase4(Edge edge) {
        if (!phase4Done) return;

        String srcId = edge.getSource().getId();
        String tgtId = edge.getTarget().getId();

        boolean isRejected = kruskalRejected.stream().anyMatch(e -> {
            String es = e.getSource().getId(), et = e.getTarget().getId();
            return (es.equals(srcId) && et.equals(tgtId))
                    || (es.equals(tgtId) && et.equals(srcId));
        });

        if (!isRejected) {
            visualLog.addCard("ℹ️", "Esa arista está en el MST",
                    "Busca una arista <b>roja punteada</b>.", StepCard.Type.INFO);
            return;
        }

        graphPanel.disableEdgeClickMode();
        visualLog.addCard("🏆", "¡Red completamente restaurada!",
                "Costo total: " + kruskalCost + " puntos", StepCard.Type.SUCCESS);
        phaseIndicators[3].setText("✅");
        phaseIndicators[3].setForeground(new Color(80, 220, 140));
        statusLabel.setText("🌐 ¡Misión Final completada!");

        // Registrar victoria — un solo timer, una sola llamada
        new javax.swing.Timer(800, ev -> {
            ((javax.swing.Timer) ev.getSource()).stop();
            GameController.getInstance().completeMission(4, 3);
            showVictoryDialog();
        }).start();
    }

    // ═══════════════════════════════════════════════════════════════
    //  AVANCE MANUAL DE FASES
    // ═══════════════════════════════════════════════════════════════
    private void doAdvancePhase(int completed) {
        int next = completed + 1;
        if (next >= 4) return;
        phaseIndicators[next].setText("▶ En curso");
        phaseIndicators[next].setForeground(new Color(255, 220, 80));
        currentPhase = next;
        phaseCard.show(phaseContainer, "PHASE" + (next + 1));
        switch (next) {
            case 1 -> setupPhase2();
            case 2 -> setupPhase3();
            case 3 -> setupPhase4();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  VICTORIA
    // ═══════════════════════════════════════════════════════════════
    private void showVictoryDialog() {
        GameController gc = GameController.getInstance();

        String msg = "<html><body style='width:300px;padding:10px;font-family:sans-serif'>" +
                "<h2 style='color:#FFD700;text-align:center'>🏆 ¡Misión Final Completada!</h2>" +
                "<p style='text-align:center'>Has restaurado la red social y detenido el ciberacoso.</p><hr>" +
                "<b>📊 Resumen de operaciones:</b><br><br>" +
                "🔍 <b>Fase 1:</b> Origen detectado en paso " + bfsStepsToOrigin + "<br>" +
                "🛡️ <b>Fase 2:</b> Riesgo de intervención: " + dijkstraRisk + "<br>" +
                "🌊 <b>Fase 3:</b> Flujo máximo contenido: " + maxFlowDetected + "<br>" +
                "🔗 <b>Fase 4:</b> Costo de reconstrucción: " + kruskalCost + "<br><hr>" +
                "⭐ <b>Estrellas totales:</b> " + gc.getTotalStars() + " / 15<br>" +
                "✅ <b>Misiones completadas:</b> " + gc.getMissionsWon() + " / 5<br><br>" +
                "<center><i style='color:#888'>El ciberacoso afecta a millones de personas.<br>" +
                "Tu intervención marca la diferencia.</i></center>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, msg,
                "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
        stopTimers();
        window.showPanel("MENU");
    }

    // ═══════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════
    private String buildPathStr(List<Node> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }

    private void stopTimers() {
        if (animTimer  != null && animTimer.isRunning())  animTimer.stop();
        if (blinkTimer != null && blinkTimer.isRunning()) blinkTimer.stop();
    }

    private String rolLabel(String b) {
        return switch (b) {
            case "bully"     -> "🔴 Acosador";
            case "victim"    -> "🟡 Víctima";
            case "supporter" -> "🟢 Defensor";
            default          -> "🔵 Espectador";
        };
    }

    private StepCard.Type cardType(String b) {
        return switch (b) {
            case "bully"     -> StepCard.Type.ERROR;
            case "victim"    -> StepCard.Type.WARNING;
            case "supporter" -> StepCard.Type.SUCCESS;
            default          -> StepCard.Type.INFO;
        };
    }

    private JPanel darkCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(28, 32, 52));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 50, 120), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        return p;
    }

    private JLabel darkTitle(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Constants.FONT_HEADING);
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel darkInfo(String text) {
        JLabel l = new JLabel("<html>" + text + "</html>");
        l.setFont(Constants.FONT_BODY);
        l.setForeground(new Color(180, 180, 210));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel dimLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Constants.FONT_BODY);
        l.setForeground(new Color(160, 160, 200));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton accentBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(Constants.FONT_BODY);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }
}