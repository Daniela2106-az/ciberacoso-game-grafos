package view;

import util.Constants;
import javax.swing.*;
import java.awt.*;

public class IntroPanel extends JPanel {
    private MainWindow window;
    private javax.swing.Timer typeTimer;
    private int charIndex = 0;

    private static final String STORY =
            "En una red social, un grupo de estudiantes comenzó a enviar\n" +
                    "mensajes hirientes sobre una compañera llamada Valeria.\n\n" +
                    "Los mensajes se propagaron rápidamente... comentarios crueles,\n" +
                    "exclusión del grupo, rumores falsos.\n\n" +
                    "Valeria empezó a sentirse sola. Nadie parecía querer ayudar.\n\n" +
                    "Pero tú sí puedes hacer algo.\n\n" +
                    "Usando algoritmos de grafos, rastrearás el origen del acoso,\n" +
                    "encontrarás rutas seguras para intervenir, controlarás\n" +
                    "la propagación y reconstruirás la confianza en la red.\n\n" +
                    "¿Estás listo para convertir una red tóxica\n" +
                    "en una Comunidad Segura?";

    public IntroPanel(MainWindow window) {
        this.window = window;
        setBackground(new Color(18, 22, 38));
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Header
        JLabel title = new JLabel("🌐 CiberAcoso Game", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(255, 220, 80));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        JLabel sub = new JLabel("Una historia real que sucede todos los días", SwingConstants.CENTER);
        sub.setFont(Constants.FONT_BODY);
        sub.setForeground(new Color(180, 160, 220));
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(title);
        top.add(sub);

        // Área de historia con efecto typewriter
        JTextArea storyArea = new JTextArea();
        storyArea.setEditable(false);
        storyArea.setOpaque(false);
        storyArea.setForeground(new Color(220, 215, 240));
        storyArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        storyArea.setFocusable(false);

        JPanel storyWrapper = new JPanel(new BorderLayout());
        storyWrapper.setOpaque(false);
        storyWrapper.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        storyWrapper.add(storyArea, BorderLayout.CENTER);

        // Botones
        JButton skipBtn  = makeBtn("⏭ Saltar intro",   new Color(60, 60, 90));
        JButton startBtn = makeBtn("▶ Comenzar",        new Color(46, 160, 100));
        startBtn.setVisible(false);

        skipBtn.addActionListener(e -> {
            if (typeTimer != null) typeTimer.stop();
            window.showPanel("MENU");
        });
        startBtn.addActionListener(e -> window.showPanel("MENU"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 20));
        btnPanel.setOpaque(false);
        btnPanel.add(skipBtn);
        btnPanel.add(startBtn);

        add(top,          BorderLayout.NORTH);
        add(storyWrapper, BorderLayout.CENTER);
        add(btnPanel,     BorderLayout.SOUTH);

        // Efecto typewriter
        typeTimer = new javax.swing.Timer(28, e -> {
            if (charIndex < STORY.length()) {
                storyArea.append(String.valueOf(STORY.charAt(charIndex)));
                charIndex++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                startBtn.setVisible(true);
                skipBtn.setText("🏠 Menú");
            }
        });
        typeTimer.start();
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(Constants.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}