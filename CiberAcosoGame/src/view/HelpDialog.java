package view;

import util.Constants;
import javax.swing.*;
import java.awt.*;

public class HelpDialog extends JDialog {

    public HelpDialog(JFrame parent, int missionIndex) {
        super(parent, "❓ Ayuda – " + Constants.MISSION_NAMES[missionIndex], true);
        setSize(620, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerColor(missionIndex));
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel("❓ " + Constants.MISSION_NAMES[missionIndex]);
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // Contenido con tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Constants.FONT_BODY);
        tabs.addTab("🎯 Objetivo",    makeTab(getObjective(missionIndex)));
        tabs.addTab("⚙️ Algoritmo",   makeTab(getAlgorithm(missionIndex)));
        tabs.addTab("🖱️ Pasos",       makeTab(getSteps(missionIndex)));
        tabs.addTab("🎨 Colores",     makeTab(getColors(missionIndex)));

        // Footer
        JButton closeBtn = new JButton("✅ Entendido");
        closeBtn.setFont(Constants.FONT_BODY);
        closeBtn.setBackground(headerColor(missionIndex));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        footer.setBackground(Constants.BG_MAIN);
        footer.add(closeBtn);

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JScrollPane makeTab(String html) {
        JEditorPane pane = new JEditorPane("text/html",
                "<html><body style='font-family:Segoe UI;font-size:13px;"
                        + "padding:16px;line-height:1.6'>" + html + "</body></html>");
        pane.setEditable(false);
        pane.setBackground(Constants.BG_PANEL);
        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(null);
        return scroll;
    }

    private Color headerColor(int i) {
        return switch (i) {
            case 0 -> new Color(229, 80,  80);
            case 1 -> new Color(52,  120, 210);
            case 2 -> new Color(46,  160, 100);
            case 3 -> new Color(160, 50,  180);
            case 4 -> new Color(80,  50,  160);
            default -> Constants.ACCENT;
        };
    }

    private String getObjective(int i) {
        return switch (i) {
            case 0 -> """
                <h3>🔍 Misión 1 – Rastros del Acoso</h3>
                <p>Se ha detectado un caso de <b>ciberacoso</b> en la red social.
                Los mensajes negativos se propagaron entre usuarios y no es claro quién lo inició.</p>
                <p><b>Tu objetivo:</b> usar BFS o DFS para rastrear la red y descubrir
                al <b>Paciente Cero</b> — el usuario que originó el acoso.</p>
                <p>Al terminar el recorrido, haz clic en el nodo que crees que inició el problema.</p>
                """;
            case 1 -> """
                <h3>🛡️ Misión 2 – Ruta Segura</h3>
                <p>El acosador fue identificado, pero el daño sigue propagándose.
                Necesitas <b>intervenir</b> para proteger al usuario afectado.</p>
                <p><b>Tu objetivo:</b> encontrar el camino de <b>menor riesgo emocional</b>
                desde un nodo de apoyo hasta la víctima, usando el algoritmo de Dijkstra.</p>
                <p>Al ver la ruta resaltada, haz clic en el nodo destino para confirmar la intervención.</p>
                """;
            case 2 -> """
                <h3>🔗 Misión 3 – Reconstruir la Red</h3>
                <p>El acoso deterioró la confianza entre los usuarios.
                La red social está fragmentada y necesita ser reconstruida.</p>
                <p><b>Tu objetivo:</b> usar el algoritmo de Kruskal para reconstruir
                las conexiones con el <b>menor esfuerzo social posible</b>.</p>
                <p>Al terminar, haz clic en una arista roja (rechazada) para demostrar
                que entiendes por qué fue descartada.</p>
                """;
            case 3 -> """
                <h3>🌊 Misión 4 – Control del Impacto</h3>
                <p>El acosador intenta lanzar una oleada masiva de mensajes dañinos.
                Como ingeniero de redes, debes medir y controlar el daño.</p>
                <p><b>Tu objetivo:</b> calcular el <b>flujo máximo</b> de mensajes
                que puede circular entre dos nodos, y bloquear la conexión crítica.</p>
                """;
            default -> """
                <h3>⭐ Misión Final – Red Segura</h3>
                <p>La red ha sido afectada por múltiples eventos de acoso simultáneos.
                Debes aplicar <b>todos los algoritmos</b> aprendidos para restaurarla completamente.</p>
                <p>Cuatro fases secuenciales, cada una con su propio algoritmo y acción del jugador.</p>
                """;
        };
    }

    private String getAlgorithm(int i) {
        return switch (i) {
            case 0 -> """
                <h3>BFS – Búsqueda en Anchura</h3>
                <p>Explora la red <b>nivel por nivel</b>, como ondas en el agua.
                Primero visita todos los vecinos directos, luego los vecinos de los vecinos.</p>
                <p><b>Útil para:</b> encontrar el camino más corto en número de saltos,
                analizar la propagación por capas.</p>
                <h3>DFS – Búsqueda en Profundidad</h3>
                <p>Sigue una cadena de conexiones <b>hasta el fondo</b> antes de retroceder.
                Como seguir un hilo de conversación hasta su origen.</p>
                <p><b>Útil para:</b> detectar cadenas largas de reenvío de mensajes.</p>
                """;
            case 1 -> """
                <h3>Dijkstra – Camino Mínimo</h3>
                <p>Encuentra el camino de <b>menor costo acumulado</b> entre dos nodos.</p>
                <p><b>Cómo funciona:</b></p>
                <ol>
                  <li>Asigna distancia ∞ a todos los nodos, 0 al origen.</li>
                  <li>Visita el nodo con menor distancia conocida.</li>
                  <li>Actualiza las distancias de sus vecinos si encuentra un camino más corto.</li>
                  <li>Repite hasta llegar al destino.</li>
                </ol>
                <p>Los <b>números azules</b> sobre los nodos muestran el riesgo acumulado en tiempo real.</p>
                """;
            case 2 -> """
                <h3>Kruskal – Árbol de Expansión Mínima</h3>
                <p>Construye una red que conecta <b>todos los nodos</b> con el menor costo total.</p>
                <p><b>Cómo funciona:</b></p>
                <ol>
                  <li>Ordena todas las aristas de menor a mayor costo.</li>
                  <li>Agrega la arista más barata que <b>no forme un ciclo</b>.</li>
                  <li>Repite hasta conectar todos los nodos.</li>
                </ol>
                <p>Usa <b>Union-Find</b> para detectar ciclos eficientemente.</p>
                <p>Una arista forma ciclo cuando sus dos extremos ya están en el mismo componente.</p>
                """;
            case 3 -> """
                <h3>Ford-Fulkerson – Flujo Máximo</h3>
                <p>Calcula la <b>máxima cantidad de flujo</b> que puede viajar de un nodo origen a uno destino.</p>
                <p><b>Cómo funciona:</b></p>
                <ol>
                  <li>Busca un camino de aumento (ruta del origen al destino con capacidad disponible).</li>
                  <li>Envía el máximo flujo posible por ese camino.</li>
                  <li>Actualiza las capacidades residuales.</li>
                  <li>Repite hasta que no haya más caminos de aumento.</li>
                </ol>
                <p>Las aristas muestran <b>flujo/capacidad</b> (ej: 5/10).</p>
                """;
            default -> """
                <h3>Integración de todos los algoritmos</h3>
                <ul>
                  <li><b>Fase 1 – BFS/DFS:</b> localizar al Paciente Cero</li>
                  <li><b>Fase 2 – Dijkstra:</b> encontrar la ruta de intervención más segura</li>
                  <li><b>Fase 3 – Ford-Fulkerson:</b> medir y controlar el flujo de acoso</li>
                  <li><b>Fase 4 – Kruskal:</b> reconstruir la red con el menor esfuerzo</li>
                </ul>
                """;
        };
    }

    private String getSteps(int i) {
        return switch (i) {
            case 0 -> """
                <ol>
                  <li>Selecciona el <b>nodo de inicio</b> en el desplegable.</li>
                  <li>Presiona <b>▶ BFS</b> o <b>▶ DFS</b> para iniciar el recorrido.</li>
                  <li>Observa cómo los nodos se iluminan en el orden que el algoritmo los visita.</li>
                  <li>Lee el panel lateral para ver el rol de cada usuario.</li>
                  <li>Cuando termine, <b>haz clic en el nodo</b> que crees que inició el acoso.</li>
                  <li>Si aciertas, la misión se completa. Si fallas, analiza mejor el recorrido.</li>
                </ol>
                <p><b>Pista:</b> el acosador principal suele ser el que tiene más conexiones
                con otros bullies y apareció primero en el recorrido.</p>
                """;
            case 1 -> """
                <ol>
                  <li>Haz clic en el <b>nodo origen</b> (punto de partida de la intervención).</li>
                  <li>Haz clic en el <b>nodo destino</b> (usuario a proteger).</li>
                  <li>Presiona <b>🔍 Calcular Ruta Segura</b>.</li>
                  <li>Observa los números azules actualizarse — son las distancias calculadas.</li>
                  <li>La ruta óptima se resaltará en <b>verde</b>.</li>
                  <li>Haz clic en el <b>nodo destino</b> para confirmar la intervención.</li>
                </ol>
                """;
            case 2 -> """
                <ol>
                  <li>Presiona <b>▶ Ejecutar Kruskal</b> para ver todo el proceso,
                      o <b>⏭ Paso a paso</b> para ir decisión por decisión.</li>
                  <li>Observa cómo las aristas grises se vuelven <b>verdes</b> (aceptadas)
                      o <b>rojas punteadas</b> (rechazadas).</li>
                  <li>Cuando Kruskal termine, <b>haz clic en una arista roja</b>
                      para explicar por qué fue rechazada.</li>
                  <li>Si la arista seleccionada efectivamente habría formado un ciclo, completas la misión.</li>
                </ol>
                """;
            case 3 -> """
                <ol>
                  <li>Haz clic en cualquier nodo como <b>fuente</b> (origen del acoso).</li>
                  <li>Haz clic en otro nodo como <b>sumidero</b> (destino del acoso).</li>
                  <li>Presiona <b>🌊 Simular Flujo</b>.</li>
                  <li>Observa los caminos de aumento animados en <b>naranja</b>.</li>
                  <li>Las aristas saturadas quedan en <b>rojo</b> — son el cuello de botella.</li>
                  <li>Selecciona qué conexión bloquear para detener el flujo.</li>
                </ol>
                """;
            default -> """
                <ol>
                  <li><b>Fase 1:</b> Ejecuta BFS o DFS y acusa al Paciente Cero haciendo clic en él.</li>
                  <li><b>Fase 2:</b> Selecciona origen y destino, calcula la ruta, confirma haciendo clic en el destino.</li>
                  <li><b>Fase 3:</b> Selecciona fuente y sumidero, simula el flujo, elige qué conexión bloquear.</li>
                  <li><b>Fase 4:</b> Ejecuta Kruskal y haz clic en una arista roja para completar.</li>
                </ol>
                <p>Cada fase se desbloquea al completar la anterior.</p>
                """;
        };
    }

    private String getColors(int i) {
        String common = """
                <h3>Colores de los nodos</h3>
                <table cellpadding='6'>
                  <tr><td>🔴 <b style='color:#e55050'>Rojo</b></td><td>Acosador (Bully)</td></tr>
                  <tr><td>🟡 <b style='color:#cc8800'>Amarillo</b></td><td>Víctima</td></tr>
                  <tr><td>🟢 <b style='color:#27ae60'>Verde</b></td><td>Defensor / Apoyo</td></tr>
                  <tr><td>🔵 <b style='color:#4169e1'>Azul</b></td><td>Espectador (Bystander)</td></tr>
                  <tr><td>🩵 <b style='color:#00d2dc'>Cian</b></td><td>Nodo visitado por el algoritmo</td></tr>
                  <tr><td>🟩 <b style='color:#3cc878'>Verde brillante</b></td><td>Nodo en la ruta final</td></tr>
                </table>
                """;
        String edges = switch (i) {
            case 0 -> """
                <h3>Colores de las aristas</h3>
                <table cellpadding='6'>
                  <tr><td>⬜ Gris</td><td>Conexión no visitada</td></tr>
                  <tr><td>🟠 Naranja</td><td>Conexión entre nodos ya visitados</td></tr>
                </table>
                """;
            case 1 -> """
                <h3>Colores de las aristas</h3>
                <table cellpadding='6'>
                  <tr><td>⬜ Gris</td><td>Conexión no evaluada</td></tr>
                  <tr><td>🟢 Verde grueso</td><td>Ruta más segura encontrada</td></tr>
                </table>
                <p>Los <b>números azules</b> sobre los nodos = riesgo acumulado desde el origen.</p>
                """;
            case 2 -> """
                <h3>Colores de las aristas</h3>
                <table cellpadding='6'>
                  <tr><td>⬜ Gris</td><td>Conexión aún no evaluada</td></tr>
                  <tr><td>🟢 Verde grueso</td><td>Aceptada en el MST</td></tr>
                  <tr><td>🔴 Rojo punteado</td><td>Rechazada (formaría ciclo)</td></tr>
                </table>
                """;
            case 3 -> """
                <h3>Colores de las aristas</h3>
                <table cellpadding='6'>
                  <tr><td>⬜ Gris</td><td>Capacidad disponible</td></tr>
                  <tr><td>🟠 Naranja</td><td>Camino de aumento activo</td></tr>
                  <tr><td>🔴 Rojo punteado</td><td>Arista saturada (corte mínimo)</td></tr>
                </table>
                <p>Las etiquetas muestran <b>flujo/capacidad</b> (ej: 7/10).</p>
                """;
            default -> """
                <p>Cada fase usa los colores de su misión correspondiente.
                Consulta la ayuda de cada misión individual para más detalles.</p>
                """;
        };
        return common + edges;
    }
}