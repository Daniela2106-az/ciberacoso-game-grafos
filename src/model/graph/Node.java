package model.graph;

public class Node {
    private String id;
    private String name;        // Nombre del usuario
    private String behavior;    // "bully", "victim", "bystander", "supporter"
    private boolean visited;
    private int x, y;           // Posición visual en el panel

    public Node(String id, String name, String behavior, int x, int y) {
        this.id = id;
        this.name = name;
        this.behavior = behavior;
        this.visited = false;
        this.x = x;
        this.y = y;
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getBehavior() { return behavior; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setBehavior(String behavior) { this.behavior = behavior; }

    @Override
    public String toString() { return name + " (" + behavior + ")"; }
}