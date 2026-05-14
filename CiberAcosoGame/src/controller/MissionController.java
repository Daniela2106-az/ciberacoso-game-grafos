package controller;

import model.algorithms.BFS;
import model.algorithms.DFS;
import model.graph.Graph;
import model.graph.Node;
import model.algorithms.Dijkstra;
import model.algorithms.Kruskal;
import model.algorithms.FordFulkerson;

import java.util.List;

public class MissionController {
    private Graph graph;
    private BFS bfs;
    private DFS dfs;
    private List<String> lastLog;
    private Dijkstra dijkstra;
    private Kruskal kruskal;
    private FordFulkerson fordFulkerson;

    public MissionController(Graph graph) {
        this.graph = graph;
        this.bfs = new BFS(graph);
        this.dfs = new DFS(graph);
        this.dijkstra = new Dijkstra(graph);
        this.kruskal = new Kruskal(graph);
        this.fordFulkerson = new FordFulkerson(graph);
    }

    public List<Node> runBFS(String startId) {
        bfs.execute(startId);
        lastLog = bfs.getStepLog();
        return bfs.getVisitOrder();
    }

    public List<Node> runDFS(String startId) {
        dfs.execute(startId);
        lastLog = dfs.getStepLog();
        return dfs.getVisitOrder();
    }

    public List<String> getLastLog() { return lastLog; }

    public List<Node> runDijkstra(String startId, String targetId) {
        dijkstra.execute(startId);
        lastLog = dijkstra.getStepLog();
        return dijkstra.getPath(targetId);
    }

    public int getDijkstraDistance(String targetId) {
        return dijkstra.getDistance(targetId);
    }

    public Dijkstra getDijkstra() { return dijkstra; }

    public void runKruskal() {
        kruskal.execute();
        lastLog = kruskal.getStepLog();
    }

    public Kruskal getKruskal() { return kruskal; }

    public int runFordFulkerson(String sourceId, String sinkId) {
        int flow = fordFulkerson.execute(sourceId, sinkId);
        lastLog  = fordFulkerson.getStepLog();
        return flow;
    }

    public FordFulkerson getFordFulkerson() { return fordFulkerson; }
}