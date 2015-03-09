package graph;

import java.util.LinkedList;

/* See restrictions in Graph.java. */

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author Bo Liu
 */
public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        int inD = 0;
        for (int[] i : edges()) {
            if (i[1] == v) {
                inD += 1;
            }
        }
        return inD;
    }

    @Override
    public int predecessor(int v, int k) {
        for (int[] i : edges()) {
            if (i[1] == v && k == 0) {
                return i[0];
            } else if (i[1] == v) {
                k -= 1;
            }
        }
        return 0;
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        LinkedList<Integer> preds = new LinkedList<Integer>();
        for (int[] i : edges()) {
            if (i[1] == v) {
                preds.add(i[0]);
            }
        }
        return Iteration.iteration(preds);
    }
}
