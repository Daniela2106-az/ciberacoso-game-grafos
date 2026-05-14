package view;

import util.Constants;
import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MainWindow() {
        setTitle("CiberAcoso Game – Estructura de Datos II");
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Pantallas
        MenuPanel menuPanel = new MenuPanel(this);
        mainContainer.add(menuPanel, "MENU");

        IntroPanel introPanel = new IntroPanel(this);
        mainContainer.add(introPanel, "INTRO");

        add(mainContainer);

        // Solo dejamos esta línea para que arranque en la INTRO
        cardLayout.show(mainContainer, "INTRO");
    }

    public void showPanel(String name) {
        cardLayout.show(mainContainer, name);
        // Si volvemos al menú, forzar reconstrucción para mostrar progreso actualizado
        if (name.equals("MENU")) {
            for (Component c : mainContainer.getComponents()) {
                if (c instanceof MenuPanel mp) {
                    mp.refresh();
                    break;
                }
            }
        }
    }
    public void addPanel(JPanel panel, String name) {
        mainContainer.add(panel, name);
    }

    public CardLayout getCardLayout() { return cardLayout; }
    public JPanel getMainContainer() { return mainContainer; }
}