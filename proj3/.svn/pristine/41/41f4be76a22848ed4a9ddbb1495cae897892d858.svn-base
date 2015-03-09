package graph;

/* See restrictions in Graph.java. */

import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.PriorityQueue;

/** The shortest paths through an edge-weighted graph.
 *  By overrriding methods getWeight, setWeight, getPredecessor, and
 *  setPredecessor, the client can determine how to represent the weighting
 *  and the search results.  By overriding estimatedDistance, clients
 *  can search for paths to specific destinations using A* search.
 *  @author Bo Liu
 */
public abstract class ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public ShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public ShortestPaths(Graph G, int source, int dest) {
        _G = G;
        _source = source;
        _dest = dest;
    }

    /** Initialize the shortest paths.  Must be called before using
     *  getWeight, getPredecessor, and pathTo. */
    public void setPaths() {
        for (int i : _G.vertices()) {
            setWeight(i, Double.POSITIVE_INFINITY);
        }
        setWeight(_source, 0.0 + estimatedDistance(_source));
        Trav t = new Trav(this);
        t.traverse(_source);
    }

    /** Returns the starting vertex. */
    public int getSource() {
        return _source;
    }

    /** Returns the target vertex, or 0 if there is none. */
    public int getDest() {
        return _dest;
    }

    /** Returns the current weight of vertex V in the graph.  If V is
     *  not in the graph, returns positive infinity. */
    public abstract double getWeight(int v);

    /** Set getWeight(V) to W. Assumes V is in the graph. */
    protected abstract void setWeight(int v, double w);

    /** Returns the current predecessor vertex of vertex V in the graph, or 0 if
     *  V is not in the graph or has no predecessor. */
    public abstract int getPredecessor(int v);

    /** Set getPredecessor(V) to U. */
    protected abstract void setPredecessor(int v, int u);

    /** Returns an estimated heuristic weight of the shortest path from vertex
     *  V to the destination vertex (if any).  This is assumed to be less
     *  than the actual weight, and is 0 by default. */
    protected double estimatedDistance(int v) {
        return 0.0;
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    protected abstract double getWeight(int u, int v);

    /** Returns a list of vertices starting at _source and ending
     *  at V that represents a shortest path to V.  Invalid if there is a
     *  destination vertex other than V. */
    public List<Integer> pathTo(int v) {
        if (_dest != 0 && _dest != v) {
            throw new IllegalArgumentException();
        }
        LinkedList<Integer> preds = new LinkedList<Integer>();
        int curr = v;
        while (curr != 0) {
            preds.addFirst(curr);
            curr = getPredecessor(curr);
        }
        return preds;
    }

    /** Returns a list of vertices starting at the source and ending at the
     *  destination vertex. Invalid if the destination is not specified. */
    public List<Integer> pathTo() {
        return pathTo(getDest());
    }

    /** Comparator for the priority queue. */
    private class Comp<T extends Integer> implements Comparator<T> {

        /** A new comparator for vertex constructed from SP. */
        Comp(ShortestPaths sp) {
            _sp = sp;
        }

        @Override
        public int compare(T v0, T v1) {
            if (_sp.getWeight(v0) > _sp.getWeight(v1)) {
                return 1;
            } else if (_sp.getWeight(v0) < _sp.getWeight(v1)) {
                return -1;
            } else {
                return 0;
            }
        }

        /** The class that constructed this. */
        private ShortestPaths _sp;
    }

    /** Traversal for shortestpath. */
    private class Trav extends Traversal {

        /** Constructor for traversal constructed from SP. */
        Trav(ShortestPaths sp) {
            super(_G, new PriorityQueue<Integer>(7,
                      new Comp<Integer>(sp)));
            _sp = sp;
        }

        @Override
        protected boolean visit(int v) {
            return v != _dest;
        }

        @Override
        protected boolean processSuccessor(int u, int v) {
            double weight = _sp.getWeight(u) + _sp.getWeight(u, v)
                + _sp.estimatedDistance(v) - _sp.estimatedDistance(u);
            if (weight < _sp.getWeight(v)) {
                _sp.setWeight(v, weight);
                _sp.setPredecessor(v, u);
                _fringe.remove(v);
                return true;
            }
            return !marked(v);
        }

        /** Stores the ShortestPaths. */
        private ShortestPaths _sp;
    }

    /** The graph being searched. */
    protected final Graph _G;
    /** The starting vertex. */
    private final int _source;
    /** The target vertex. */
    private final int _dest;

}
