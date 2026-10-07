# 🌐 CiberAcoso Game

Juego educativo de escritorio, desarrollado en **Java (Swing)**, que enseña algoritmos de grafos mientras el jugador combate el ciberacoso en una red social. Fue creado para la asignatura *Estructura de Datos II* de la Universidad del Norte.

## ¿Qué hace el proyecto?

El juego cuenta la historia de Valeria, una estudiante víctima de acoso en una red social. El jugador debe usar algoritmos clásicos de grafos para rastrear el origen del acoso, encontrar rutas seguras de intervención, reconstruir la red y controlar la propagación del daño, hasta convertir una red tóxica en una **Comunidad Segura**.

Cada misión visualiza paso a paso la ejecución del algoritmo sobre un grafo donde los nodos representan personas (acosadores, víctimas, apoyo y neutrales) y las aristas las conexiones entre ellas.

## Características

- 5 misiones, cada una asociada a un algoritmo de grafos:

  | Misión | Algoritmo |
  |---|---|
  | 1 – Rastros del Acoso | BFS y DFS |
  | 2 – Ruta Segura | Dijkstra |
  | 3 – Reconstruir la Red | Kruskal |
  | 4 – Control del Impacto | Ford-Fulkerson |
  | Final – Red Segura | Todos integrados |

- Interfaz gráfica con Swing: introducción narrativa con efecto máquina de escribir, menú de misiones, ayuda y paneles por misión.
- Visualización interactiva del grafo (nodos visitados, ruta final, colores por rol).
- Registro visual paso a paso de cada algoritmo (`StepCard`, `VisualLogPanel`).
- Sistema de progreso con estrellas (hasta 3 por misión, 15 en total).
- Generación de grafos de juego (`GraphGenerator`).
- Sin dependencias externas: solo la biblioteca estándar de Java.

## Estructura de carpetas

```
ciberacoso-game-grafos/
├── README.md
├── .gitignore
└── src/
    ├── Main.java             # Punto de entrada
    ├── controller/           # GameController (progreso), MissionController (ejecuta algoritmos)
    ├── model/
    │   ├── graph/            # Graph, Node, Edge
    │   └── algorithms/       # BFS, DFS, Dijkstra, Kruskal, FordFulkerson
    ├── view/                 # MainWindow, IntroPanel, MenuPanel, MissionNPanel, FinalMissionPanel, GraphPanel, HelpDialog
    │   └── components/       # StepCard, VisualLogPanel
    └── util/                 # Constants (colores, fuentes, nombres), GraphGenerator
```

Arquitectura: patrón MVC (modelo – vista – controlador).

## Requisitos

- **JDK 14 o superior** (se usan `switch` con flechas y `String.repeat`).
- Un entorno gráfico (Swing no funciona en servidores sin pantalla).
- Fuentes *Segoe UI* / *Segoe UI Emoji* recomendadas (si no están, Java usa una fuente por defecto).
- No requiere librerías de terceros.

## Cómo usarlo

### Desde la terminal

```bash
git clone https://github.com/Daniela2106-az/ciberacoso-game-grafos.git
cd ciberacoso-game-grafos
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

### Desde IntelliJ IDEA

1. Abre la carpeta del repositorio (*File > Open*).
2. En *Project Structure* configura un JDK 14+ y marca `src` como *Sources Root*.
3. Ejecuta la clase `Main`.

### Jugando

1. Lee la introducción y pulsa para continuar al menú.
2. Elige una misión (las 5 están disponibles desde el inicio).
3. Ejecuta el algoritmo y sigue el registro paso a paso para resolver el reto.
4. Gana estrellas; tu progreso se muestra en el pie del menú (`X/5 misiones`, `⭐ X/15`). El progreso vive en memoria y se reinicia al cerrar el juego.
