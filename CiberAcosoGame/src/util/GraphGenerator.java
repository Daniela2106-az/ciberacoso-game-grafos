package util;

import model.graph.Graph;
import model.graph.Node;

public class GraphGenerator {

    public static Graph generateMission1Graph() {
        Graph g = new Graph(false);
        g.addNode(new Node("A", "Camila",  "bully",     390,  60));
        g.addNode(new Node("B", "Andrés",  "bystander", 190, 170));
        g.addNode(new Node("C", "Sofía",   "bystander", 590, 170));
        g.addNode(new Node("D", "Miguel",  "bully",     140, 330));
        g.addNode(new Node("E", "Valeria", "victim",    390, 330));
        g.addNode(new Node("F", "Luis",    "supporter", 640, 330));
        g.addNode(new Node("G", "Paula",   "supporter", 280, 490));
        g.addNode(new Node("H", "Jorge",   "bystander", 510, 490));

        g.addEdge("A", "B", 3, "\"¿Viste lo que hizo Valeria? Qué vergüenza 😂\"");
        g.addEdge("A", "C", 5, "\"Comparte esto, todo el mundo debe saberlo 👀\"");
        g.addEdge("B", "D", 2, "\"Sí, yo también lo vi. Es una ridicula la verdad\"");
        g.addEdge("B", "E", 7, "\"Valeria, todos están hablando mal de ti\"");
        g.addEdge("C", "E", 4, "\"Mejor sal del grupo, ya nadie te quiere aquí\"");
        g.addEdge("C", "F", 1, "\"Luis, ¿por qué la defiendes? Ponte de nuestro lado\"");
        g.addEdge("D", "G", 6, "\"Paula, únete, esto es solo un juego\"");
        g.addEdge("E", "G", 3, "\"Paula por favor ayúdame, no sé qué hacer 😢\"");
        g.addEdge("E", "H", 2, "\"Jorge, ¿tú también crees lo que dicen de mí?\"");
        g.addEdge("F", "H", 5, "\"Jorge, defiende a Valeria, esto está mal\"");
        return g;
    }

    public static Graph generateMission2Graph() {
        Graph g = new Graph(false);
        g.addNode(new Node("A", "Camila",  "bully",     390,  60));
        g.addNode(new Node("B", "Andrés",  "bystander", 170, 180));
        g.addNode(new Node("C", "Sofía",   "bully",     610, 180));
        g.addNode(new Node("D", "Miguel",  "bully",     120, 350));
        g.addNode(new Node("E", "Valeria", "victim",    390, 390));
        g.addNode(new Node("F", "Luis",    "supporter", 660, 350));
        g.addNode(new Node("G", "Paula",   "supporter", 270, 510));
        g.addNode(new Node("H", "Jorge",   "bystander", 510, 510));

        g.addEdge("F", "C", 2,  "\"Sofía, sé que dudas. Habla conmigo antes de seguir\"");
        g.addEdge("F", "H", 3,  "\"Jorge, necesito que me ayudes a llegar a Valeria\"");
        g.addEdge("C", "A", 8,  "\"Camila está bloqueando el acceso — riesgo muy alto\"");
        g.addEdge("C", "E", 6,  "\"Sofía aún tiene contacto con Valeria — ruta posible\"");
        g.addEdge("H", "E", 4,  "\"Jorge puede pasarle un mensaje a Valeria\"");
        g.addEdge("H", "G", 2,  "\"Jorge y Paula coordinan apoyo juntos\"");
        g.addEdge("G", "E", 3,  "\"Paula llega directamente a Valeria con apoyo\"");
        g.addEdge("A", "B", 9,  "\"Camila sigue propagando mensajes negativos\"");
        g.addEdge("B", "D", 5,  "\"Andrés pasa el mensaje a Miguel\"");
        g.addEdge("D", "E", 10, "\"Miguel intenta intimidar a Valeria — máximo riesgo\"");
        return g;
    }

    public static Graph generateMission3Graph() {
        Graph g = new Graph(false);
        g.addNode(new Node("A", "Camila",  "bystander", 390,  60));
        g.addNode(new Node("B", "Andrés",  "supporter", 170, 180));
        g.addNode(new Node("C", "Sofía",   "supporter", 610, 180));
        g.addNode(new Node("D", "Miguel",  "bystander", 120, 360));
        g.addNode(new Node("E", "Valeria", "victim",    390, 390));
        g.addNode(new Node("F", "Luis",    "supporter", 660, 360));
        g.addNode(new Node("G", "Paula",   "supporter", 270, 510));
        g.addNode(new Node("H", "Jorge",   "bystander", 510, 510));

        g.addEdge("A", "B", 4,  "\"Camila y Andrés pueden volver a hablar con algo de esfuerzo\"");
        g.addEdge("A", "C", 2,  "\"Camila y Sofía aún tienen confianza — reconexión fácil\"");
        g.addEdge("A", "E", 9,  "\"Camila y Valeria — la herida es profunda, costo alto\"");
        g.addEdge("B", "D", 3,  "\"Andrés puede acercarse a Miguel con paciencia\"");
        g.addEdge("B", "E", 6,  "\"Andrés quiere ayudar a Valeria pero es difícil\"");
        g.addEdge("C", "E", 5,  "\"Sofía se disculpó — la reconexión es posible\"");
        g.addEdge("C", "F", 1,  "\"Sofía y Luis son amigos — conexión casi inmediata\"");
        g.addEdge("D", "G", 7,  "\"Miguel intenta acercarse a Paula — desconfianza alta\"");
        g.addEdge("E", "G", 2,  "\"Valeria y Paula tienen mucha confianza mutua\"");
        g.addEdge("E", "H", 4,  "\"Valeria le da una oportunidad a Jorge\"");
        g.addEdge("F", "H", 3,  "\"Luis y Jorge coordinan juntos el apoyo\"");
        g.addEdge("G", "H", 8,  "\"Paula y Jorge — poca historia en común\"");
        return g;
    }

    public static Graph generateMission4Graph() {
        Graph g = new Graph(true);
        g.addNode(new Node("S", "Carlos",  "bully",     110, 280));
        g.addNode(new Node("A", "Camila",  "bully",     290, 130));
        g.addNode(new Node("B", "Andrés",  "bystander", 290, 280));
        g.addNode(new Node("C", "Sofía",   "bystander", 290, 430));
        g.addNode(new Node("D", "Miguel",  "bully",     480, 130));
        g.addNode(new Node("E", "Jorge",   "bystander", 480, 280));
        g.addNode(new Node("F", "Paula",   "bystander", 480, 430));
        g.addNode(new Node("T", "Valeria", "victim",    660, 280));

        g.addEdge("S", "A", 10, "Carlos envía spam masivo a Camila");
        g.addEdge("S", "B",  8, "Carlos bombardea a Andrés");
        g.addEdge("S", "C",  5, "Carlos contacta a Sofía");
        g.addEdge("S", "D",  7, "Carlos activa a Miguel directamente");
        g.addEdge("A", "D",  6, "Camila reenvía a Miguel");
        g.addEdge("A", "E",  4, "Camila intenta llegar a Jorge");
        g.addEdge("A", "B",  3, "Camila y Andrés se coordinan");
        g.addEdge("B", "E",  5, "Andrés actúa como puente");
        g.addEdge("B", "F",  4, "Andrés desvía mensajes a Paula");
        g.addEdge("B", "T",  2, "Andrés llega directamente a Valeria");
        g.addEdge("C", "F",  8, "Sofía amplifica el contenido");
        g.addEdge("C", "B",  3, "Sofía redirige hacia Andrés");
        g.addEdge("D", "T",  7, "Miguel bombardea a Valeria");
        g.addEdge("D", "E",  5, "Miguel usa a Jorge de intermediario");
        g.addEdge("E", "T",  9, "Jorge permite el paso de mensajes");
        g.addEdge("E", "F",  3, "Jorge y Paula se comunican");
        g.addEdge("F", "T",  6, "Paula se convierte en canal final");
        g.addEdge("C", "T",  4, "Sofía llega directamente a Valeria");
        return g;
    }

    public static Graph generateFinalMissionGraph() {
        Graph g = new Graph(false);
        g.addNode(new Node("A", "Tomás",   "bully",     390,  50));
        g.addNode(new Node("B", "Camila",  "bully",     190, 150));
        g.addNode(new Node("C", "Rodrigo", "bystander", 590, 150));
        g.addNode(new Node("D", "Andrés",  "bystander",  90, 280));
        g.addNode(new Node("E", "Sofía",   "bully",     290, 260));
        g.addNode(new Node("F", "Miguel",  "bystander", 490, 260));
        g.addNode(new Node("G", "Paula",   "victim",    690, 280));
        g.addNode(new Node("H", "Luis",    "supporter", 140, 400));
        g.addNode(new Node("I", "Valeria", "supporter", 340, 390));
        g.addNode(new Node("J", "Jorge",   "bystander", 540, 390));
        g.addNode(new Node("K", "Felipe",  "supporter", 690, 400));
        g.addNode(new Node("L", "Natalia", "bystander", 240, 510));
        g.addNode(new Node("M", "Diego",   "bystander", 440, 510));
        g.addNode(new Node("N", "Ana",     "victim",    590, 510));

        g.addEdge("A", "B",  3, "Tomás coordina el acoso con Camila");
        g.addEdge("A", "C",  7, "Tomás presiona a Rodrigo");
        g.addEdge("A", "E",  2, "Tomás y Sofía son el núcleo del acoso");
        g.addEdge("B", "D",  4, "Camila arrastra a Andrés");
        g.addEdge("B", "E",  5, "Camila y Sofía amplifican mensajes");
        g.addEdge("C", "F",  3, "Rodrigo conecta con Miguel");
        g.addEdge("C", "G",  8, "Rodrigo acosa directamente a Paula");
        g.addEdge("D", "H",  2, "Andrés cerca a Luis");
        g.addEdge("E", "F",  4, "Sofía usa a Miguel de puente");
        g.addEdge("E", "I",  6, "Sofía intenta aislar a Valeria");
        g.addEdge("F", "G",  5, "Miguel presiona a Paula");
        g.addEdge("F", "J",  3, "Miguel y Jorge se comunican");
        g.addEdge("G", "K",  1, "Paula tiene línea directa con Felipe");
        g.addEdge("H", "L",  3, "Luis apoya a Natalia");
        g.addEdge("I", "L",  2, "Valeria y Natalia se apoyan");
        g.addEdge("I", "M",  4, "Valeria intenta llegar a Diego");
        g.addEdge("J", "M",  3, "Jorge y Diego coordinan");
        g.addEdge("J", "N",  6, "Jorge acosa a Ana");
        g.addEdge("K", "N",  2, "Felipe tiene acceso a Ana");
        g.addEdge("L", "M",  5, "Natalia y Diego se conocen");
        g.addEdge("M", "N",  4, "Diego y Ana interactúan");
        return g;
    }
}