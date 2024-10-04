package Pieces;

import Board.Board;

import javax.swing.*;
import java.awt.*;

public class Pawn extends Piece{

    private int moves;
    private boolean promote;


    public Pawn(boolean isWhite) {
        super(isWhite);
        moves = 0;
        promote = false;
    }

    public void setPromote(boolean yes) {
        promote = yes;
    }

    public boolean getPromote() {
        return promote;
    }


    public int getNumberOfMoves(){
        return moves;
    }

    public void incrementMoves() {
        moves++;
    }
    @Override
    public boolean isValidMove(Board board, int startX, int startY, int endX, int endY) {
        int direction = (isWhite()) ? -1 : 1;

        int currentY = getY();
        int currentX = getX();

        // Diagonal square is the king
        if (Math.abs(currentX - endX) == 1 && currentY + direction == endY && board.selectPiece(endX, endY) instanceof King) {
            // add some way of saying that this is check
            King king = (King) board.selectPiece(endX, endY);
            king.setChecked(true);
            //return false;
        } //else {
            //King king = board.findKing(this.isWhite());
            //king.setChecked(false);
        //}

        // Moving one square forward
        if (currentX == endX && currentY + direction == endY && board.selectPiece(endX, endY) == null) {
            return true;
        }

        // Moving two squares forward from starting position
        if (currentX == endX && currentY + 2 * direction == endY && currentY == (isWhite() ? 6 : 1) && board.selectPiece(endX, endY) == null) {
            if (!this.isWhite() && board.selectPiece(endX, endY - 1) == null){
                return true;
            } else if (this.isWhite() && board.selectPiece(endX, endY + 1) == null){
                return true;
            }
        }

        // Capturing diagonally
        if (Math.abs(currentX - endX) == 1 && currentY + direction == endY && board.selectPiece(endX, endY) != null && board.selectPiece(endX, endY).isWhite() != this.isWhite()) {
            return true;
        }

        // En passant capture
        if (Math.abs(currentX - endX) == 1 && currentY == (this.isWhite() ? 3 : 4)) {
            Piece adjacentPawn = board.selectPiece(endX, currentY);
            if (adjacentPawn instanceof Pawn && adjacentPawn.isWhite() != this.isWhite() && ((Pawn) adjacentPawn).getNumberOfMoves() == 1) {
                if (board.selectPiece(endX, endY) == null && board.getLastMovedPiece() == adjacentPawn) {
                    if (this.isWhite() && (currentY - endY == 1)) {
                        return true;
                    } else if (!this.isWhite() && (endY - currentY ==1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }







    @Override
    public Image getImage() {
        String color = (isWhite) ? "white" : "black";
        Image img = new ImageIcon(getClass().getResource("/Pieces/" + color + "_pawn.png")).getImage();
        return img;
    }

    @Override
    public char toFENChar() {
        return isWhite() ? 'P' : 'p';
    }

}
