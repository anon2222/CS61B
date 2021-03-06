package graph;

import java.util.TreeSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.asList;

/* See restrictions in Graph.java. */

/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  @author Bo Liu
 */
abstract class GraphObj extends Graph {

    /** A new, empty Graph. */
    GraphObj() {
        _adjList = new LinkedList<List<Integer>>();
        _edges = new LinkedList<List<Integer>>();
    }

    @Override
    public int vertexSize() {
        int size = 0;
        for (int i = 0; i < _adjList.size(); i += 1) {
            if (_adjList.get(i) != null) {
                size += 1;
            }
        }
        return size;
    }

    @Override
    public int maxVertex() {
        for (int i = _adjList.size(); i > 0; i -= 1) {
            if (_adjList.get(i - 1) != null) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int edgeSize() {
        int size = 0;
        for (int i = 0; i < _edges.size(); i += 1) {
            if (_edges.get(i) != null) {
                size += 1;
            }
        }
        return size;

    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        if (contains(v)) {
            if (_adjList.get(v - 1) == null) {
                return 0;
            } else {
                return _adjList.get(v - 1).size();
            }
        }
        return 0;
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        return mine(u);
    }

    @Override
    public boolean contains(int u, int v) {
        if (contains(u) && contains(v)) {
            if (isDirected()) {
                return _adjList.get(u - 1).contains(v);
            } else {
                return _adjList.get(u - 1).contains(v)
                    || _adjList.get(v - 1).contains(u);
            }
        }
        return false;
    }

    @Override
    public int add() {
        int i;
        if (vStore.isEmpty()) {
            _adjList.add(new LinkedList<Integer>());
            i = _adjList.size() - 1;
        } else {
            i = vStore.pollFirst();
            _adjList.set(i, new LinkedList<Integer>());
        }
        return i + 1;
    }

    @Override
    public int add(int u, int v) {
        if (mine(u) && mine(v) && !contains(u, v)) {
            if (isDirected()) {
                _adjList.get(u - 1).add(v);
                addEdge(u, v);
            } else {
                _adjList.get(u - 1).add(v);
                if (u != v) {
                    _adjList.get(v - 1).add(u);
                }
                addEdge(u, v);
            }
        }
        return u;
    }

    /** A helper method adding edge(U, V) to the graph and list of edges. */
    private void addEdge(int u, int v) {
        if (idStore.isEmpty()) {
            _edges.add(asList(u, v));
        } else {
            int i = idStore.pollFirst();
            _edges.set(i, asList(u, v));
        }
    }

    @Override
    public void remove(int v) {
        if (_adjList.get(v - 1) == null) {
            return;
        }
        vStore.add(v - 1);
        for (int successor : _adjList.get(v - 1)) {
            removeEdge(v, successor);
        }
        for (int i = 0; i < _adjList.size(); i += 1) {
            if (_adjList.get(i) != null) {
                if (_adjList.get(i).contains(v)) {
                    _adjList.get(i).remove(new Integer(v));
                    removeEdge(i + 1, v);
                }
            }
        }
        _adjList.set(v - 1, null);
    }

    /** A helper method removing edge(U, V) from list of edges. */
    private void removeEdge(int u, int v) {
        int ind = _edges.indexOf(asList(u, v));
        if (ind != -1) {
            _edges.set(ind, null);
            idStore.add(ind);
        }
    }

    @Override
    public void remove(int u, int v) {
        if (!isDirected()) {
            _adjList.get(v - 1).remove(new Integer(u));
            _adjList.get(u - 1).remove(new Integer(v));
            removeEdge(u, v);
        } else {
            _adjList.get(u - 1).remove(new Integer(v));
            removeEdge(u, v);
        }
    }

    @Override
    public Iteration<Integer> vertices() {
        LinkedList<Integer> temp = new LinkedList<Integer>();
        for (int i = 0; i < _adjList.size(); i += 1) {
            if (_adjList.get(i) != null) {
                temp.add(i + 1);
            }
        }
        return Iteration.iteration(temp);
    }

    @Override
    public int successor(int v, int k) {
        if (contains(v)) {
            for (int i : _adjList.get(v - 1)) {
                if (k == 0) {
                    return i;
                } else {
                    k -= 1;
                }
            }
        }
        return 0;
    }

    @Override
    public abstract int predecessor(int v, int k);

    @Override
    public Iteration<Integer> successors(int v) {
        if (_adjList.get(v - 1) == null) {
            return Iteration.iteration(new LinkedList<Integer>());
        }
        return Iteration.iteration(_adjList.get(v - 1));
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        ArrayList<int[]> temp = new ArrayList<int[]>();
        for (int i = 0; i < _edges.size(); i += 1) {
            if (_edges.get(i) == null) {
                continue;
            }
            temp.add(new int[]{_edges.get(i).get(0), _edges.get(i).get(1)});
        }
        return Iteration.iteration(temp);
    }

    @Override
    protected boolean mine(int v) {
        return (v <= _adjList.size()) && (_adjList.get(v - 1) != null);
    }

    @Override
    protected void checkMyVertex(int v) {
        if (!mine(v)) {
            throw new Error("the vertex is not in the graph");
        }
    }

    @Override
    protected int edgeId(int u, int v) {
        int index = _edges.indexOf(asList(u, v));
        return index + 1;
    }

    /** An adjacency list for graph. */
    private LinkedList<List<Integer>> _adjList;

    /** A list storing graph edges. */
    private LinkedList<List<Integer>> _edges;
    /** Temprarily store the edge IDs which has been removed. */
    private TreeSet<Integer> idStore = new TreeSet<Integer>();
    /** Temprarily store the vertices index which has been removed. */
    private TreeSet<Integer> vStore = new TreeSet<Integer>();


}
