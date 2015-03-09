package graph;

import java.util.HashMap;

/* See restrictions in Graph.java. */

/** A partial implementation of ShortestPaths that contains the weights of
 *  the vertices and the predecessor edges.   The client needs to
 *  supply only the two-argument getWeight method.
 *  @author Bo Liu
 */
public abstract class SimpleShortestPaths extends ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
    }

    @Override
    public double getWeight(int v) {
        if (weights.get(v) == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return weights.get(v);
        }
    }

    @Override
    protected void setWeight(int v, double w) {
        weights.put(v, w);
    }

    @Override
    public int getPredecessor(int v) {
        if (preds.get(v) == null) {
            return 0;
        } else {
            return preds.get(v);
        }
    }

    @Override
    protected void setPredecessor(int v, int u) {
        preds.put(v, u);
    }

    /** Stores the predecessors. */
    private HashMap<Integer, Integer> preds
        = new HashMap<Integer, Integer>();
    /** Stores the weight. */
    private HashMap<Integer, Double> weights
        = new HashMap<Integer, Double>();

}
