package Pieces;

import Board.Board;

import java.awt.Image;


public abstract class Piece {
    boolean isWhite;
    private int[] boardPosition; // {row, col}



    public Piece(boolean isWhite){
        this.isWhite = isWhite;
    }

    public abstract boolean isValidMove(Board board, int startX, int startY, int endX, int endY);

    public abstract Image getImage();

    public boolean isWhite() {
        return isWhite;
    }

    public int[] getBoardPosition() {
        return boardPosition;
    }

    public int getX(){
        return this.getBoardPosition()[1];
    }

    public int getY(){
        return this.getBoardPosition()[0];
    }


    public void setBoardPosition(int row, int col) {
        this.boardPosition = new int[]{row, col};
    }

    public abstract char toFENChar();

}
