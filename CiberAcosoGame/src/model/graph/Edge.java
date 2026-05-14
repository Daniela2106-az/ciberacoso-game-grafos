package model.graph;

public class Edge {
    private Node source;
    private Node target;
    private int weight;         // Nivel de riesgo / costo / capacidad
    private boolean directed;   // true = dirigido, false = no dirigido
    private String message;

    public Edge(Node source, Node target, int weight, boolean directed) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.directed = directed;
        this.message = "";
    }

    public Edge(Node source, Node target, int weight, boolean directed, String message) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.directed = directed;
        this.message = message;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public void setWeight(int weight) { this.weight = weight; }
    public boolean isDirected() { return directed; }

    @Override
    public String toString() {
        String arrow = directed ? " → " : " ↔ ";
        return source.getName() + arrow + target.getName() + " [peso: " + weight + "]";
    }
}