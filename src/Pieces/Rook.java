package Pieces;

import Board.Board;

import javax.swing.ImageIcon;
import java.awt.Image;

public class Rook extends Piece{

    private boolean hasMoved;


    public Rook(boolean isWhite) {
        super(isWhite);
        hasMoved = false;
    }


    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    @Override
    public boolean isValidMove(Board board, int startX, int startY, int endX, int endY) {
        int xMove = Math.abs(startX - endX);
        int yMove = Math.abs(startY - endY);

        // Check if the move is in a straight line (either horizontal or vertical)
        if ((xMove != 0 && yMove == 0) || (xMove == 0 && yMove != 0)) {
            int xDirection = (xMove != 0) ? (endX - startX) / xMove : 0;
            int yDirection = (yMove != 0) ? (endY - startY) / yMove : 0;

            int x = startX + xDirection;
            int y = startY + yDirection;

            // Check all squares along the path
            while (x != endX || y != endY) {
                if (board.selectPiece(x, y) != null) {
                    return false; // There is a piece blocking the path
                }
                x += xDirection;
                y += yDirection;
            }

            // Check the target square
            Piece targetPiece = board.selectPiece(endX, endY);
            if (targetPiece == null || targetPiece.isWhite() != this.isWhite()) {
                if (targetPiece != null && targetPiece.isWhite() != this.isWhite() && targetPiece instanceof King) {
                    // implement check logic
                    King king = (King) targetPiece;
                    king.setChecked(true);
                    //return false;
                } // else {
                    // King king = board.findKing(this.isWhite());
                    // king.setChecked(false);
                // }
                return true;
            }
        }
        return false;
    }


    @Override
    public Image getImage() {
        String color = (isWhite) ? "white" : "black";
        Image img = new ImageIcon(getClass().getResource("/Pieces/" + color + "_rook.png")).getImage();
        return img;
    }

    @Override
    public char toFENChar() {
        return isWhite() ? 'R' : 'r';
    }
}
