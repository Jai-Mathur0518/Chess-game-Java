package Pieces;

import Board.Board;

import javax.swing.*;
import java.awt.*;

public class Knight extends Piece{
    public Knight(boolean isWhite) {
        super(isWhite);
    }
    @Override
    public boolean isValidMove(Board board, int startX, int startY, int endX, int endY) {
        // The knight moves in an "L" shape: two squares in one direction and one in the other
        int xMove = Math.abs(startX - endX);
        int yMove = Math.abs(startY - endY);

        // Check for valid knight move
        if ((xMove == 2 && yMove == 1) || (xMove == 1 && yMove == 2)) {
            // Check if the target square is empty or contains an opponent's piece
            Piece targetPiece = board.selectPiece(endX, endY);
            if (targetPiece == null || targetPiece.isWhite() != this.isWhite()) {
                if (targetPiece != null && targetPiece.isWhite() != this.isWhite() && (targetPiece instanceof King)) {
                    // implement check logic
                    King king = (King) targetPiece;
                    king.setChecked(true);
                    //return false;
                } //else {
                    //King king = board.findKing(this.isWhite());
                    //king.setChecked(false);
                //}
                return true;
            }
        }
        return false;
    }


    @Override
    public Image getImage() {
        String color = (isWhite) ? "white" : "black";
        Image img = new ImageIcon(getClass().getResource("/Pieces/" + color + "_knight.png")).getImage();
        return img;
    }

    @Override
    public char toFENChar() {
        return isWhite() ? 'N' : 'n';
    }
}
