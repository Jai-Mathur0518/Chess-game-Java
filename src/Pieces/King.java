package Pieces;

import Board.Board;

import javax.swing.*;
import java.awt.*;

public class King extends Piece{

    private boolean hasMoved;

    private boolean isChecked;

    public King(boolean isWhite) {
        super(isWhite);
        this.hasMoved = false;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean isValidMove(Board board, int startX, int startY, int endX, int endY) {
        int xMove = Math.abs(startX - endX);
        int yMove = Math.abs(startY - endY);

        // Regular King moves: one square in any direction
        if (xMove <= 1 && yMove <= 1 && !(xMove == 0 && yMove == 0)) {
            Piece targetPiece = board.selectPiece(endX, endY);
            if (targetPiece == null || targetPiece.isWhite() != this.isWhite()) {
                // Check that the move does not place the king within one square of the opponent's king
                if (!isOpponentKingNearby(board, endX, endY) && !isSquareAttacked(board, endX, endY)) {
                    this.setChecked(false);
                    return true;
                }
            }
        }


        // Castling logic placeholder
        if (isCastlingMove(board, startX, startY, endX, endY)) {
            this.setChecked(false);
            return true;
        }

        return false;
    }

    private boolean isCastlingMove(Board board, int startX, int startY, int endX, int endY) {
        if (hasMoved) {
            return false; // King has already moved
        }

        // Castling is only valid if the king moves two squares horizontally
        if (Math.abs(startX - endX) == 2 && startY == endY) {
            int rookStartX = (endX > startX) ? 7 : 0; // Determine which rook is being used
            Piece rook = board.selectPiece(rookStartX, startY);

            if (rook instanceof Rook && !((Rook) rook).hasMoved()) {
                // Check if all squares between the king and the rook are empty
                int direction = (endX > startX) ? 1 : -1;
                for (int x = startX + direction; x != endX; x += direction) {
                    if (board.selectPiece(x, startY) != null) {
                        return false;
                    }
                }

                // Check that the move does not place the king in check at any point
                for (int x = startX; x != endX + direction; x += direction) {
                    if (isSquareAttacked(board, x, startY)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
    private boolean isSquareAttacked(Board board, int x, int y) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.selectPiece(i, j);
                if (piece != null && piece.isWhite() != this.isWhite()) {
                    if (piece instanceof Pawn) {
                        int direction = piece.isWhite() ? -1 : 1; // Determine the direction of the pawn
                        // Check the two diagonal attack squares for the pawn
                        if ((x == i + 1 || x == i - 1) && y == j + direction) {
                            return true;
                        }
                    } else {
                        if (piece.isValidMove(board, i, j, x, y)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isOpponentKingNearby(Board board, int x, int y) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Piece adjacentPiece = board.selectPiece(newX, newY);
                if (adjacentPiece instanceof King && adjacentPiece.isWhite() != this.isWhite()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Image getImage() {
        String color = (isWhite) ? "white" : "black";
        Image img = new ImageIcon(getClass().getResource("/Pieces/" + color + "_king.png")).getImage();
        return img;
    }

    @Override
    public char toFENChar() {
        return isWhite() ? 'K' : 'k';
    }
}
