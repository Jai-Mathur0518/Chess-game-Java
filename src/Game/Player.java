package Game;

import Pieces.*;

import java.util.ArrayList;

public class Player {
    private ArrayList<Piece> pieces;
    private boolean white;

    public Player(boolean colour){
        this.white = colour;
        pieces = new ArrayList<Piece>();
    }

    public boolean isWhite(){
        return white;
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public ArrayList<Piece> getPieces(){
        return pieces;
    }




}
