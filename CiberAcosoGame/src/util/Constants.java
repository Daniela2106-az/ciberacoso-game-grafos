package util;

import java.awt.Color;
import java.awt.Font;

public class Constants {
    // Colores del juego (paleta moderna, clara, legible)
    public static final Color BG_MAIN        = new Color(245, 247, 250);   // Fondo general
    public static final Color BG_PANEL       = new Color(255, 255, 255);   // Paneles blancos
    public static final Color COLOR_BULLY    = new Color(229, 80,  80);    // Rojo suave
    public static final Color COLOR_VICTIM   = new Color(255, 180, 50);    // Amarillo
    public static final Color COLOR_SUPPORT  = new Color(72,  199, 142);   // Verde
    public static final Color COLOR_NEUTRAL  = new Color(100, 149, 237);   // Azul aciano

    public static final Color COLOR_VISITED  = new Color(0, 210, 220);     // Cian brillante — solo para nodos visitados
    public static final Color COLOR_FINAL_PATH = new Color(255, 160, 0);   // Naranja dorado — ruta final confirmada
    public static final Color COLOR_PATH     = new Color(255, 140, 0);     // Naranja camino

    public static final Color ACCENT         = new Color(79,  140, 255);   // Azul acento UI
    public static final Color TEXT_DARK      = new Color(30,  30,  50);    // Texto principal
    public static final Color TEXT_LIGHT     = new Color(120, 120, 140);   // Texto secundario

    // Fuentes
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_NODE    = new Font("Segoe UI", Font.BOLD,  13);

    // Tamaños de nodos
    public static final int NODE_RADIUS = 32;

    // Ventana
    public static final int WINDOW_WIDTH  = 1280;
    public static final int WINDOW_HEIGHT = 820;

    // Misiones
    public static final String[] MISSION_NAMES = {
            "Misión 1 – Rastros del Acoso",
            "Misión 2 – Ruta Segura",
            "Misión 3 – Reconstruir la Red",
            "Misión 4 – Control del Impacto",
            "Misión Final – Red Segura"
    };
}