package view;

import controller.GameController;
import util.Constants;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private MainWindow window;

    public MenuPanel(MainWindow window) {
        this.window = window;
        setBackground(Constants.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        this.removeAll();

        GameController gc = GameController.getInstance();

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Constants.ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("🌐 CiberAcoso Game", SwingConstants.CENTER);
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "Aprende algoritmos de grafos protegiendo la red social",
                SwingConstants.CENTER);
        subtitle.setFont(Constants.FONT_BODY);
        subtitle.setForeground(new Color(220, 230, 255));

        header.add(title,    BorderLayout.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);

        // ── Grid de misiones ─────────────────────────────────────
        JPanel missionGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        missionGrid.setBackground(Constants.BG_MAIN);
        missionGrid.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        String[] icons     = {"🔍", "🛡️", "🔗", "🌊", "⭐"};
        String[] algos     = {"BFS y DFS", "Dijkstra", "Kruskal", "Ford-Fulkerson", "Todo integrado"};

        for (int i = 0; i < Constants.MISSION_NAMES.length; i++) {
            boolean unlocked  = gc.isMissionUnlocked(i);
            boolean completed = gc.isMissionCompleted(i);
            int     starCount = gc.getStars(i);
            missionGrid.add(buildMissionCard(i, icons[i], algos[i], unlocked, completed, starCount));
        }
        missionGrid.add(new JLabel()); // celda vacía 6ta

        // ── Footer con progreso ──────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Constants.BG_MAIN);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 40, 16, 40));

        JLabel credit = new JLabel(
                "Universidad del Norte – Estructura de Datos II   |   "
                        + gc.getMissionsWon() + "/5 misiones completadas   ⭐ "
                        + gc.getTotalStars() + "/15",
                SwingConstants.CENTER);
        credit.setFont(Constants.FONT_SMALL);
        credit.setForeground(Constants.TEXT_LIGHT);
        footer.add(credit, BorderLayout.CENTER);

        add(header,      BorderLayout.NORTH);
        add(missionGrid, BorderLayout.CENTER);
        add(footer,      BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel buildMissionCard(int index, String icon, String algo,
                                    boolean unlocked, boolean completed, int starCount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Constants.BG_PANEL);

        Color borderColor = completed ? new Color(60, 200, 120)
                : unlocked  ? Constants.ACCENT
                :             new Color(220, 225, 235);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        if (unlocked) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    launchMission(index);
                }
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(new Color(235, 242, 255)); card.repaint();
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(Constants.BG_PANEL); card.repaint();
                }
            });
        }

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(
                "<html><center>" + Constants.MISSION_NAMES[index] + "</center></html>",
                SwingConstants.CENTER);
        nameLabel.setFont(Constants.FONT_HEADING);
        nameLabel.setForeground(Constants.TEXT_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel algoLabel = new JLabel(algo, SwingConstants.CENTER);
        algoLabel.setFont(Constants.FONT_SMALL);
        algoLabel.setForeground(Constants.TEXT_LIGHT);
        algoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Estado: completada con estrellas / jugable / bloqueada
        String statusText;
        Color  statusColor;
        if (completed) {
            statusText  = "⭐".repeat(starCount);
            statusColor = new Color(220, 160, 0);
        } else if (unlocked) {
            statusText  = "▶ Click para jugar";
            statusColor = Constants.ACCENT;
        } else {
            statusText  = "🔒 Bloqueada";
            statusColor = Constants.TEXT_LIGHT;
        }

        JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);
        statusLabel.setFont(Constants.FONT_BODY);
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(algoLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(statusLabel);

        return card;
    }

    private void launchMission(int index) {
        switch (index) {
            case 0 -> { Mission1Panel m = new Mission1Panel(window); window.addPanel(m, "MISSION1"); window.showPanel("MISSION1"); }
            case 1 -> { Mission2Panel m = new Mission2Panel(window); window.addPanel(m, "MISSION2"); window.showPanel("MISSION2"); }
            case 2 -> { Mission3Panel m = new Mission3Panel(window); window.addPanel(m, "MISSION3"); window.showPanel("MISSION3"); }
            case 3 -> { Mission4Panel m = new Mission4Panel(window); window.addPanel(m, "MISSION4"); window.showPanel("MISSION4"); }
            case 4 -> { FinalMissionPanel m = new FinalMissionPanel(window); window.addPanel(m, "FINAL"); window.showPanel("FINAL"); }
        }
    }

    /** Llamado por MainWindow cada vez que el menú se hace visible */
    public void refresh() {
        buildUI();
    }
}