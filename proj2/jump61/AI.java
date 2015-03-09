package jump61;

import java.util.ArrayList;

/** An automated Player.
 *  @author Bo Liu
 */
class AI extends Player {

    /** Time allotted to all but final search depth (milliseconds). */
    private static final long TIME_LIMIT = 15000;

    /** Number of calls to minmax between checks of elapsed time. */
    private static final long TIME_CHECK_INTERVAL = 10000;

    /** Number of milliseconds in one second. */
    private static final double MILLIS = 1000.0;

    /** Very big number. */
    private static final int INF = 23333;

    /** Very small number. */
    private static final int NEGINF = -23333;

    /** Parameter. */
    private static final double C = 0.8;

    /** Parameter #2. */
    private static final int C0 = 255;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Game gg = getGame();
        Board bb = getBoard();
        Side p = getSide();
        ArrayList<Integer> arr = new ArrayList<Integer>();
        minmax(p, new MutableBoard(bb), 5, INF, arr);
        gg.makeMove(arr.get(0));
        gg.reportMove(p, bb.row(arr.get(0)), bb.col(arr.get(0)));
    }
    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int minmax(Side p, Board b, int d, int cutoff,
    ArrayList<Integer> moves) {
        if (b.numOfSide(p) == Math.pow(b.size(), 2)) {
            return INF;
        } else if (b.numOfSide(p.opposite()) == Math.pow(b.size(), 2)) {
            return NEGINF;
        } else if (d == 0) {
            return staticEval(p, b);
        }
        int[] bestSoFar = new int[] {-2, NEGINF};
        for (int i = 0; i < Math.pow(b.size(), 2); i += 1) {
            if (b.isLegal(p, i)) {
                b.addSpot(p, i);
                int comp = minmax(p.opposite(), b, d - 1, -bestSoFar[1], null);
                b.undo();
                if (-comp > bestSoFar[1]) {
                    bestSoFar[0] = i;
                    bestSoFar[1] = -comp;
                    if (bestSoFar[1] >= cutoff) {
                        break;
                    }
                }
            }
        }
        if (moves != null) {
            if (bestSoFar[1] == NEGINF) {
                int gg = getGame().randInt(b.size() * b.size());
                while (!b.isLegal(p, gg)) {
                    gg = getGame().randInt(b.size() * b.size());
                }
                moves.add(gg);
            } else {
                moves.add(bestSoFar[0]);
            }
        }
        return Math.min(cutoff, bestSoFar[1]);
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side p, Board b) {
        if (p.equals(Side.RED) && b.numOfSide(p) > C * Math.pow(b.size(), 2)) {
            return C0;
        } else if (p.equals(Side.BLUE)
            && b.numOfSide(p) > C * Math.pow(b.size(), 2)) {
            return C0;
        } else {
            return 0;
        }
    }

}
