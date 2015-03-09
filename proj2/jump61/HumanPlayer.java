package jump61;

/** A Player that gets its moves from manual input.
 *  @author Bo Liu
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Side color) {
        super(game, color);
    }

    @Override
    /** Retrieve moves using getGame().getMove() until a legal one is found and
     *  make that move in getGame().  Report erroneous moves to player. */
    void makeMove() {
        Board B = getBoard();
        Game G = getGame();
        int[] arr = new int[2];
        if (!G.getMove(arr)) {
            return;
        }
        G.makeMove(arr[0], arr[1]);
    }

}
