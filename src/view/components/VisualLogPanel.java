package view.components;

import util.Constants;
import javax.swing.*;
import java.awt.*;

public class VisualLogPanel extends JPanel {

    private JPanel cardsContainer;
    private JLabel titleLabel;
    private JScrollPane scrollPane;

    public VisualLogPanel(String title) {
        setLayout(new BorderLayout(0, 6));
        setBackground(Constants.BG_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.FONT_HEADING);
        titleLabel.setForeground(Constants.TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(Constants.BG_PANEL);

        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Constants.BG_PANEL);
        scrollPane.getViewport().setBackground(Constants.BG_PANEL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** Agrega una tarjeta visual al log */
    public void addCard(String emoji, String title, String body, StepCard.Type type) {
        StepCard card = new StepCard(emoji, title, body, type);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsContainer.add(card);
        cardsContainer.add(Box.createVerticalStrut(4));
        cardsContainer.revalidate();
        cardsContainer.repaint();

        // Auto-scroll al último card
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    /** Limpia todas las tarjetas */
    public void clear() {
        cardsContainer.removeAll();
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /** Cambia el título del panel */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /** Permite cambiar el color de fondo (útil para tema oscuro en Misión Final) */
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (cardsContainer != null) cardsContainer.setBackground(bg);
        if (scrollPane != null) {
            scrollPane.setBackground(bg);
            scrollPane.getViewport().setBackground(bg);
        }
    }
}