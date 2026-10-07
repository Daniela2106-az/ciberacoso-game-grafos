package view;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class GraphPanel extends JPanel {

    private Graph graph;
    private Set<String> highlighted;
    private Set<String> pathNodes;
    private Set<String[]> pathEdges;
    private Map<String, Integer> distLabels;
    private Set<String> mstEdgeKeys;
    private Set<String> rejectedKeys;
    private Map<String, int[]> flowData;
    private Set<String> saturatedKeys;
    private Set<String> activePathKeys;
    private Consumer<Node> onNodeClicked;
    private Consumer<Edge> onEdgeClicked;
    private boolean allowClick     = false;
    private boolean allowEdgeClick = false;
    private boolean neutralMode    = false;

    private Map<String, int[]> scaledPositions = new HashMap<>();

    public GraphPanel(Graph graph) {
        this.graph         = graph;
        this.highlighted   = new HashSet<>();
        this.pathNodes     = new HashSet<>();
        this.pathEdges     = new HashSet<>();
        this.distLabels    = new HashMap<>();
        this.mstEdgeKeys   = new HashSet<>();
        this.rejectedKeys  = new HashSet<>();
        this.flowData      = new HashMap<>();
        this.saturatedKeys = new HashSet<>();
        this.activePathKeys= new HashSet<>();
        setBackground(Constants.BG_PANEL);
        setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 2, true));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (allowClick && onNodeClicked != null) {
                    Node n = getNodeAt(e.getX(), e.getY());
                    if (n != null) { onNodeClicked.accept(n); return; }
                }
                if (allowEdgeClick && onEdgeClicked != null) {
                    Edge ed = getEdgeAt(e.getX(), e.getY());
                    if (ed != null) onEdgeClicked.accept(ed);
                }
            }
        });
    }

    // ── Escalar posiciones al tamaño actual del panel ─────────────
    private void computeScaledPositions() {
        int pw = getWidth()  - 20;
        int ph = getHeight() - 20;
        if (pw <= 0 || ph <= 0) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Node n : graph.getAllNodes()) {
            minX = Math.min(minX, n.getX()); minY = Math.min(minY, n.getY());
            maxX = Math.max(maxX, n.getX()); maxY = Math.max(maxY, n.getY());
        }
        int rangeX = Math.max(maxX - minX, 1);
        int rangeY = Math.max(maxY - minY, 1);
        int margin = Constants.NODE_RADIUS + 14;
        int availW = pw - margin * 2;
        int availH = ph - margin * 2;

        scaledPositions.clear();
        for (Node n : graph.getAllNodes()) {
            int sx = margin + 10 + (int)((double)(n.getX() - minX) / rangeX * availW);
            int sy = margin + 10 + (int)((double)(n.getY() - minY) / rangeY * availH);
            scaledPositions.put(n.getId(), new int[]{sx, sy});
        }
    }

    private int[] pos(Node n) {
        int[] p = scaledPositions.get(n.getId());
        return p != null ? p : new int[]{n.getX(), n.getY()};
    }

    // ── API pública ───────────────────────────────────────────────
    public void enableClickMode(Consumer<Node> cb) {
        onNodeClicked = cb; allowClick = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    public void disableClickMode() {
        allowClick = false; setCursor(Cursor.getDefaultCursor());
    }
    public void enableEdgeClickMode(Consumer<Edge> cb) {
        onEdgeClicked = cb; allowEdgeClick = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    public void disableEdgeClickMode() {
        allowEdgeClick = false; onEdgeClicked = null;
        setCursor(Cursor.getDefaultCursor());
    }
    public void setNeutralMode(boolean v) { neutralMode = v; repaint(); }

    public void highlightNode(Node n) { highlighted.add(n.getId()); repaint(); }

    public void updateDistLabel(String id, int d) { distLabels.put(id, d); repaint(); }
    public void clearDistLabels() { distLabels.clear(); repaint(); }

    public void showFinalPath(List<Node> path) {
        pathNodes.clear(); pathEdges.clear();
        for (Node n : path) pathNodes.add(n.getId());
        for (int i = 0; i < path.size()-1; i++)
            pathEdges.add(new String[]{path.get(i).getId(), path.get(i+1).getId()});
        repaint();
    }

    public void addMSTEdge(Edge e) {
        mstEdgeKeys.add(e.getSource().getId()+"-"+e.getTarget().getId());
        mstEdgeKeys.add(e.getTarget().getId()+"-"+e.getSource().getId());
        repaint();
    }
    public void addRejectedEdge(Edge e) {
        rejectedKeys.add(e.getSource().getId()+"-"+e.getTarget().getId());
        rejectedKeys.add(e.getTarget().getId()+"-"+e.getSource().getId());
        repaint();
    }
    public void showMST(List<Edge> mst, List<Edge> rej) {
        mstEdgeKeys.clear(); rejectedKeys.clear();
        mst.forEach(this::addMSTEdge); rej.forEach(this::addRejectedEdge);
    }

    public void setFlowData(Map<String,int[]> fd) { flowData = fd; repaint(); }
    public void setSaturated(Set<String> s)        { saturatedKeys = s; repaint(); }
    public void setActivePath(List<String> ids) {
        activePathKeys.clear();
        for (int i=0;i<ids.size()-1;i++) activePathKeys.add(ids.get(i)+"-"+ids.get(i+1));
        repaint();
    }
    public void clearActivePath() { activePathKeys.clear(); repaint(); }

    public void reset() {
        highlighted.clear();
        pathNodes.clear();
        pathEdges.clear();
        distLabels.clear();
        mstEdgeKeys.clear();
        rejectedKeys.clear();
        flowData.clear();
        saturatedKeys.clear();
        activePathKeys.clear();
        // NO tocar allowClick ni onNodeClicked aquí
        // El modo clic se desactiva explícitamente con disableClickMode()
        graph.resetVisited();
        repaint();
    }

    public void fullReset() {
        allowClick     = false;
        allowEdgeClick = false;
        onNodeClicked  = null;
        onEdgeClicked  = null;
        setCursor(Cursor.getDefaultCursor());
        reset();
    }

    // ── Detección de nodo ─────────────────────────────────────────
    private Node getNodeAt(int px, int py) {
        int r = Constants.NODE_RADIUS;
        for (Node n : graph.getAllNodes()) {
            int[] p = pos(n);
            int dx = px-p[0], dy = py-p[1];
            if (dx*dx+dy*dy <= r*r) return n;
        }
        return null;
    }

    // ── Detección de arista ───────────────────────────────────────
    // Busca en rejectedKeys para confirmar que es una arista real del grafo
    private Edge getEdgeAt(int px, int py) {
        int threshold = 10;
        // Iterar sobre aristas únicas del grafo
        Set<String> seen = new HashSet<>();
        for (Map.Entry<String, List<Edge>> entry : graph.getAdjList().entrySet()) {
            for (Edge edge : entry.getValue()) {
                String fwd = edge.getSource().getId()+"-"+edge.getTarget().getId();
                String rev = edge.getTarget().getId()+"-"+edge.getSource().getId();
                if (seen.contains(fwd) || seen.contains(rev)) continue;
                seen.add(fwd); seen.add(rev);

                int[] ps = pos(edge.getSource());
                int[] pt = pos(edge.getTarget());
                double dx  = pt[0]-ps[0], dy = pt[1]-ps[1];
                double len = Math.sqrt(dx*dx+dy*dy);
                if (len == 0) continue;
                double t = Math.max(0, Math.min(1,
                        ((px-ps[0])*dx+(py-ps[1])*dy)/(len*len)));
                double nx = ps[0]+t*dx, ny = ps[1]+t*dy;
                double dist = Math.sqrt((px-nx)*(px-nx)+(py-ny)*(py-ny));
                if (dist <= threshold) return edge;
            }
        }
        return null;
    }

    // ── Paint ─────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        computeScaledPositions();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawEdges(g2);
        drawNodes(g2);
        if (allowClick || allowEdgeClick) drawClickHint(g2);
    }

    private void drawClickHint(Graphics2D g2) {
        g2.setFont(Constants.FONT_SMALL);
        g2.setColor(Constants.ACCENT);
        String hint = allowEdgeClick
                ? "🎯 Haz clic en una arista roja punteada"
                : "🎯 Haz clic en el nodo objetivo";
        g2.drawString(hint, 12, 20);
    }

    private boolean isPathEdge(String a, String b) {
        for (String[] e : pathEdges)
            if ((e[0].equals(a)&&e[1].equals(b))||(e[0].equals(b)&&e[1].equals(a))) return true;
        return false;
    }

    private void drawEdges(Graphics2D g2) {
        Set<String> seen = new HashSet<>();
        for (Map.Entry<String,List<Edge>> entry : graph.getAdjList().entrySet()) {
            for (Edge edge : entry.getValue()) {
                Node s = edge.getSource(), t = edge.getTarget();
                String fwd = s.getId()+"-"+t.getId();
                String rev = t.getId()+"-"+s.getId();
                if (seen.contains(fwd)||seen.contains(rev)) continue;
                seen.add(fwd); seen.add(rev);

                int[] ps = pos(s), pt = pos(t);

                boolean onMST      = mstEdgeKeys.contains(fwd);
                boolean rejected   = rejectedKeys.contains(fwd);
                boolean onPath     = isPathEdge(s.getId(), t.getId());
                boolean bothVisit  = highlighted.contains(s.getId())&&highlighted.contains(t.getId());
                boolean saturated  = saturatedKeys.contains(fwd);
                boolean activePath = activePathKeys.contains(fwd);

                if (activePath) {
                    g2.setStroke(new BasicStroke(5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(255,180,0));
                } else if (saturated) {
                    g2.setStroke(new BasicStroke(4f,BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,10f,new float[]{7f,4f},0f));
                    g2.setColor(new Color(220,50,50));
                } else if (onMST) {
                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(60, 200, 120));  // verde MST — se mantiene distinto
                } else if (rejected) {
                    g2.setStroke(new BasicStroke(3f,BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,10f,new float[]{8f,6f},0f));
                    g2.setColor(new Color(220,60,60));
                } else if (onPath) {
                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(Constants.COLOR_FINAL_PATH);
                } else if (bothVisit) {
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.setColor(Constants.COLOR_PATH);
                } else {
                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(new Color(190,200,215));
                }

                drawArrow(g2, ps[0],ps[1],pt[0],pt[1], graph.isDirected());

                // Etiqueta
                int mx=(ps[0]+pt[0])/2, my=(ps[1]+pt[1])/2;
                String label = flowData.containsKey(fwd)
                        ? flowData.get(fwd)[0]+"/"+flowData.get(fwd)[1]
                        : String.valueOf(edge.getWeight());
                g2.setFont(Constants.FONT_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(label);
                g2.setColor(new Color(255,255,255,210));
                g2.fillRoundRect(mx-tw/2-3,my-13,tw+6,16,6,6);
                g2.setColor(saturated  ? new Color(180, 30, 30)
                        : activePath  ? new Color(180, 100, 0)
                        : onPath      ? new Color(180, 100, 0)
                        : onMST       ? new Color(30, 140, 70)
                        : rejected     ? new Color(180, 30, 30)
                        : Constants.TEXT_LIGHT);
                g2.drawString(label, mx-tw/2, my-1);
            }
        }
    }

    private void drawArrow(Graphics2D g2,int x1,int y1,int x2,int y2,boolean dir) {
        int r = Constants.NODE_RADIUS+2;
        double dx=x2-x1,dy=y2-y1,len=Math.sqrt(dx*dx+dy*dy);
        if(len==0) return;
        double ux=dx/len,uy=dy/len;
        int sx=(int)(x1+ux*r),sy=(int)(y1+uy*r);
        int ex=(int)(x2-ux*r),ey=(int)(y2-uy*r);
        g2.drawLine(sx,sy,ex,ey);
        if(!dir) return;
        int as=10; double angle=Math.atan2(ey-sy,ex-sx);
        g2.drawLine(ex,ey,(int)(ex-as*Math.cos(angle-Math.PI/6)),(int)(ey-as*Math.sin(angle-Math.PI/6)));
        g2.drawLine(ex,ey,(int)(ex-as*Math.cos(angle+Math.PI/6)),(int)(ey-as*Math.sin(angle+Math.PI/6)));
    }

    private void drawNodes(Graphics2D g2) {
        int r = Constants.NODE_RADIUS;
        for (Node node : graph.getAllNodes()) {
            int[] p = pos(node);
            int cx=p[0],cy=p[1],x=cx-r,y=cy-r;

            Color fill;
            if (pathNodes.contains(node.getId()))        fill = Constants.COLOR_FINAL_PATH;            else if (highlighted.contains(node.getId())) fill = Constants.COLOR_VISITED;
            else fill = switch(node.getBehavior()) {
                    case "bully"     -> neutralMode ? Constants.COLOR_NEUTRAL : Constants.COLOR_BULLY;
                    case "victim"    -> neutralMode ? Constants.COLOR_NEUTRAL : Constants.COLOR_VICTIM;
                    case "supporter" -> Constants.COLOR_SUPPORT;
                    default          -> Constants.COLOR_NEUTRAL;
                };

            g2.setColor(new Color(0,0,0,30));
            g2.fillOval(x+3,y+3,r*2,r*2);
            g2.setColor(fill);
            g2.fillOval(x,y,r*2,r*2);
            g2.setColor(fill.darker());
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x,y,r*2,r*2);

            g2.setFont(Constants.FONT_NODE);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(node.getName());
            g2.drawString(node.getName(), cx-tw/2, cy+5);

            if (distLabels.containsKey(node.getId())) {
                int dist = distLabels.get(node.getId());
                String lbl = dist==Integer.MAX_VALUE ? "∞" : String.valueOf(dist);
                g2.setFont(new Font("Segoe UI",Font.BOLD,12));
                FontMetrics fm2 = g2.getFontMetrics();
                int lw = fm2.stringWidth(lbl);
                g2.setColor(new Color(255,255,255,220));
                g2.fillRoundRect(cx-lw/2-4,cy-r-20,lw+8,18,8,8);
                g2.setColor(new Color(30,100,200));
                g2.drawString(lbl,cx-lw/2,cy-r-6);
            }
        }
    }

    public void clearHighlights() {
        highlighted.clear();
        repaint();
    }
}