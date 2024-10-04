package Game;
import Board.Board;
public class ChessGame {

    private Player black;
    private Player white;
    public GameGUI gameGUI;

    private boolean checkMate;

    private boolean whiteWon;

    private boolean whiteTurn;

    private boolean staleMate;

    private Board board;
    public ChessGame(){
        black = new Player(false);
        white = new Player(true);
        whiteTurn = true;
        board = new Board(this);
        gameGUI = new GameGUI(board, this);
    }

    public Board getBoard() {
        return this.board;
    }

    public boolean whichTurn(){
        return whiteTurn;
    }

    public void invertTurn() {
        whiteTurn = !whiteTurn;
    }

    public Player getWhitePlayer() {
        return white;
    }

    public Player getBlackPlayer() {
        return black;
    }

    public Player getPlayer(boolean colour) {
        if (colour == true) {
            return white;
        } else if (colour == false) {
            return black;
        }
        return null;
    }


    public void setCheckMate(boolean mate) {
        checkMate = mate;
    }

    public boolean getCheckMate() {
        return checkMate;
    }


    public void setWhiteWon(boolean colour) {
        whiteWon = colour;
    }

    public boolean isWhiteWon() {
        return whiteWon;
    }

    public boolean isStaleMate() {
        return staleMate;
    }

    public void setStaleMate(boolean stale){
        staleMate = stale;
    }



}
