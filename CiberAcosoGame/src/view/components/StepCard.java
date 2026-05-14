package view.components;

import util.Constants;
import javax.swing.*;
import java.awt.*;

public class StepCard extends JPanel {

    public enum Type { INFO, SUCCESS, WARNING, ERROR, ACTIVE }

    public StepCard(String emoji, String title, String body, Type type) {
        setLayout(new BorderLayout(8, 2));
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor(type), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        setBackground(bgColor(type));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        icon.setVerticalAlignment(SwingConstants.TOP);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Constants.FONT_HEADING);
        titleLbl.setForeground(fgColor(type));

        JLabel bodyLbl = new JLabel("<html>" + body + "</html>");
        bodyLbl.setFont(Constants.FONT_BODY);
        bodyLbl.setForeground(Constants.TEXT_DARK);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 1));
        text.setOpaque(false);
        text.add(titleLbl);
        text.add(bodyLbl);

        add(icon, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
    }

    private Color bgColor(Type t) {
        return switch (t) {
            case SUCCESS -> new Color(232, 255, 242);
            case WARNING -> new Color(255, 251, 222);
            case ERROR   -> new Color(255, 236, 236);
            case ACTIVE  -> new Color(230, 242, 255);
            default      -> new Color(245, 247, 250);
        };
    }

    private Color borderColor(Type t) {
        return switch (t) {
            case SUCCESS -> new Color(100, 200, 130);
            case WARNING -> new Color(220, 180, 60);
            case ERROR   -> new Color(210, 90, 90);
            case ACTIVE  -> new Color(80, 140, 220);
            default      -> new Color(210, 215, 225);
        };
    }

    private Color fgColor(Type t) {
        return switch (t) {
            case SUCCESS -> new Color(25, 130, 65);
            case WARNING -> new Color(150, 110, 0);
            case ERROR   -> new Color(170, 35, 35);
            case ACTIVE  -> new Color(25, 85, 175);
            default      -> Constants.TEXT_DARK;
        };
    }
}