package jump61;

import static jump61.Side.*;
import static jump61.Square.square;
import java.util.Stack;
/** A Jump61 board state that may be modified.
 *  @author Bo Liu
 */
class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _size = N;
        initialize();
    }


   /** Initializing the board. */
    private void initialize() {
        _mutableBoard = new Square[_size][_size];
        for (int i = 0; i < size(); i += 1) {
            for (int j = 0; j < size(); j += 1) {
                _mutableBoard[i][j] = Square.INITIAL;
            }
        }
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear. */
    MutableBoard(Board board0) {
        _size = board0.size();
        copy(board0);
        _undoHistory = new Stack<MutableBoard>();
    }

    @Override
    void clear(int N) {
        initialize();
        _undoHistory = new Stack<MutableBoard>();
        announce();
    }

    @Override
    void copy(Board board) {
        _mutableBoard = new Square[board.size()][board.size()];
        for (int i = 0; i < size(); i += 1) {
            for (int j = 0; j < size(); j += 1) {
                _mutableBoard[i][j] = board.get(i + 1, j + 1);
            }
        }

    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history.  Assumes BOARD and I have the same size. */
    private void internalCopy(MutableBoard board) {
        for (int i = 0; i < board.size(); i += 1) {
            for (int j = 0; j < board.size(); j += 1) {
                _mutableBoard[i][j] = board.get(i + 1, j + 1);
            }
        }

    }

    @Override
    int size() {
        return _size;
    }

    @Override
    Square get(int n) {
        return _mutableBoard[row(n) - 1][col(n) - 1];
    }

    @Override
    int numOfSide(Side side) {
        int count = 0;
        for (int i = 0; i < size(); i += 1) {
            for (int j = 0; j < size(); j += 1) {
                if (_mutableBoard[i][j].getSide() == side) {
                    count += 1;
                }
            }
        }
        return count;
    }

    @Override
    int numPieces() {
        int sum = 0;
        for (int i = 0; i < size(); i += 1) {
            for (int j = 0; j < size(); j += 1) {
                sum += _mutableBoard[i][j].getSpots();
            }
        }
        return sum;
    }

    @Override
    void addSpot(Side player, int r, int c) {
        markUndo();
        add(player, r, c);
        announce();
    }

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). Deals with overfull. */
    private void add(Side player, int r, int c) {
        int sp = get(r, c).getSpots();
        if (getWinner() == null) {
            _mutableBoard[r - 1][c - 1] = square(player, sp + 1);
            if (!overfull(player, r, c)) {
                return;
            }
            if (exists(r, c)) {
                _mutableBoard[r - 1][c - 1] = square(player, 1);
                if (exists(r, c - 1)) {
                    add(player, r, c - 1);
                }
                if (exists(r, c + 1)) {
                    add(player, r, c + 1);
                }
                if (exists(r - 1, c)) {
                    add(player, r - 1, c);
                }
                if (exists(r + 1, c)) {
                    add(player, r + 1, c);
                } else {
                    return;
                }
            }
        }
    }

    /** RETURNS if the sqaure from PLAYER at row R, column C
      * is overfull or not. */
    private boolean overfull(Side player, int r, int c) {
        return _mutableBoard[r - 1][c - 1].getSpots() > neighbors(r, c);
    }

    @Override
    void addSpot(Side player, int n) {
        addSpot(player, row(n), col(n));
        announce();
    }

    @Override
    void set(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), square(player, num));
    }

    @Override
    void set(int n, int num, Side player) {
        internalSet(n, square(player, num));
        announce();
    }

    @Override
    void undo() {
        if (_undoHistory != null) {
            MutableBoard temp = _undoHistory.pop();
            internalCopy(temp);
        }
    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        MutableBoard cp = new MutableBoard(this);
        _undoHistory.push(cp);
    }

    /** Set the contents of the square with index IND to SQ. Update counts
     *  of numbers of squares of each color.  */
    private void internalSet(int ind, Square sq) {
        _mutableBoard[row(ind) - 1][col(ind) - 1] = sq;
    }

    /** Notify all Observers of a change. */
    private void announce() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoard)) {
            return obj.equals(this);
        } else {
            if (((Board) obj).size() != size()) {
                return false;
            }
            for (int i = 0; i < Math.pow(size(), 2); i += 1) {
                Square sq = ((Board) obj).get(i);
                Square sq0 = get(i);
                if (sq.getSpots() == sq0.getSpots()
                    && sq.getSide() == sq0.getSide()) {
                    continue;
                }
                return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        return _mutableBoard.hashCode();
    }

    /** Stores the undo history in a stack. */
    private Stack<MutableBoard> _undoHistory = new Stack<MutableBoard>();

    /** Datastructure for mutableboard implementation. */
    private Square [][] _mutableBoard;

    /** Stores the size for this mutableBoard. */
    private int _size;

}
