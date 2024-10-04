package Board;

import Pieces.*;

import Game.ChessGame;

import javax.swing.*;
import java.util.ArrayList;


public class Board {

    private Piece lastMovedPiece;

    private String enPassantTargetSquare;

    private final int[][] boardColours = {
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1}
    };

    private final String[][] squareLabels = {
            {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"},
            {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"},
            {"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6"},
            {"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"},
            {"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"},
            {"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"},
            {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"},
            {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"}
    };



    private Piece[][] piecePosition = new Piece[8][8];

    private ChessGame currentGame;
    public Board(ChessGame game) {
        currentGame = game;
        this.enPassantTargetSquare = "-";
        initialiseBoard(currentGame);

    }

    private void initialiseBoard(ChessGame game) {
        Piece[] backRowBlack = new Piece[] {
                new Rook(false),
                new Knight(false),
                new Bishop(false),
                new Queen(false),
                new King(false),
                new Bishop(false),
                new Knight(false),
                new Rook(false)
        };
        Piece[] backRowWhite = new Piece[] {
                new Rook(true),
                new Knight(true),
                new Bishop(true),
                new Queen(true),
                new King(true),
                new Bishop(true),
                new Knight(true),
                new Rook(true)
        };

        // Place the back rows
        for (int i = 0; i < 8; i++) {
            piecePosition[7][i] = backRowWhite[i];
            piecePosition[7][i].setBoardPosition(7, i);
            currentGame.getWhitePlayer().addPiece(backRowWhite[i]);

            piecePosition[0][i] = backRowBlack[i];
            piecePosition[0][i].setBoardPosition(0, i);
            currentGame.getBlackPlayer().addPiece(backRowBlack[i]);

        }

        // Place pawns
        for (int i = 0; i < 8; i++) {
            piecePosition[6][i] = new Pawn(true);  // White pawns
            piecePosition[6][i].setBoardPosition(6, i);
            currentGame.getWhitePlayer().addPiece(piecePosition[6][i]);
            piecePosition[1][i] = new Pawn(false); // Black pawns
            piecePosition[1][i].setBoardPosition(1, i);
            currentGame.getBlackPlayer().addPiece(piecePosition[1][i]);

        }

        // Initialize empty squares
        for (int i = 2; i <= 5; i++) {
            for (int j = 0; j < 8; j++) {
                piecePosition[i][j] = null;
            }
        }
    }


    public int[][] getBoardColours() {
        return boardColours;
    }

    public Piece[][] getPiecePosition() {
        return piecePosition;
    }

    public Piece selectPiece(int x, int y) {
        return piecePosition[y][x];
    }

    public boolean movePiece(int startRow, int startCol, int endRow, int endCol) {
        Piece piece = selectPiece(startCol, startRow);
        System.out.println(piece);

        if (this.selectPiece(endCol, endRow) instanceof King) {
            return false;
        }

        if (currentGame.whichTurn() == piece.isWhite()) {
            if (piece.isValidMove(this, startCol, startRow, endCol, endRow)) {

                // Debugging logs
                System.out.println("Moving piece: " + piece);
                System.out.println("Start position: (" + startRow + ", " + startCol + ")");
                System.out.println("End position: (" + endRow + ", " + endCol + ")");
                System.out.println(this.selectPiece(endCol, endRow));


                Piece capturedPiece = makeMove(startCol, startRow, endCol, endRow);
                // Check if the move resolves the check condition
                if (isKingInCheck(piece.isWhite())) {
                    undoMove(startCol, startRow, endCol, endRow, capturedPiece);
                    System.out.println("end piece: " + this.selectPiece(endCol, endRow));

                    return false;
                }
                undoMove(startCol, startRow, endCol, endRow, capturedPiece);
                findKing(piece.isWhite()).setChecked(false);
                System.out.println("end piece: " + this.selectPiece(endCol, endRow));
                // Handle en passant capture
                System.out.println("trying enpassant");
                if (piece instanceof Pawn) {
                    System.out.println("Pawn move detected");
                    System.out.println(this.selectPiece(endCol, endRow));
                    if (Math.abs(startCol - endCol) == 1 && this.selectPiece(endCol, endRow) == null) {
                        System.out.println("Potential en passant move detected");
                        //int capturedPawnRow = startRow + (piece.isWhite() ? -1 : 1);
                        Piece adjacentPawn = this.selectPiece(endCol, startRow);
                        System.out.println("Adjacent pawn: " + adjacentPawn);

                        if (adjacentPawn instanceof Pawn && adjacentPawn.isWhite() != piece.isWhite() && ((Pawn) adjacentPawn).getNumberOfMoves() == 1) {
                            if (this.getLastMovedPiece() == adjacentPawn) {
                                System.out.println("En passant capture: " + adjacentPawn);
                                piecePosition[startRow][endCol] = null;
                            }
                        }
                    }
                }
                // castling
                 if (piece instanceof King && Math.abs(startCol - endCol) == 2 && startRow == endRow) {
                    int rookStartX = (endCol > startCol) ? 7 : 0; // Determine which rook is being used
                    int rookEndX = (endCol > startCol) ? endCol - 1 : endCol + 1; // New position for the rook
                    int direction = (endCol > startCol) ? 1 : -1;

                    Piece rook = this.selectPiece(rookStartX, startRow);
                    if (((King) piece).hasMoved() == false && ((Rook) rook).hasMoved() == false) {
                        // Move the rook to its new position
                        this.getPiecePosition()[startRow][rookEndX] = rook;
                        this.getPiecePosition()[startRow][rookStartX] = null;
                        rook.setBoardPosition(startRow, rookEndX); // Update rook's position
                        ((King) piece).setHasMoved(true);
                        ((Rook) rook).setHasMoved(true);
                    }
                } else if (piece instanceof King) {
                    ((King) piece).setHasMoved(true);
                } else if (piece instanceof Rook) {
                    ((Rook) piece).setHasMoved(true);
                }





                piecePosition[endRow][endCol] = piece;
                piecePosition[startRow][startCol] = null;
                piece.setBoardPosition(endRow, endCol); // Update the piece's position
                this.setLastMovedPiece(piece);
                // Handle pawn promotion

                if (piece instanceof Pawn && (endRow == 0 || endRow == 7)) {
                    handlePromotion((Pawn) piece, endRow, endCol);
                }

                if (this.isKingInCheck(!piece.isWhite())) {
                    findKing(!piece.isWhite()).setChecked(true);
                    //SwingUtilities.invokeLater(() -> currentGame.gameGUI.repaint());  // Ensure GUI update on the Event Dispatch Thread
                    System.out.println("check");
                    System.out.println(findKing(!piece.isWhite()).isChecked());
                } else {
                    findKing(!piece.isWhite()).setChecked(false);
                    System.out.println(this.isKingInCheck(!piece.isWhite()));
                }
                currentGame.invertTurn();
                if (piece instanceof Pawn) {
                    ((Pawn) piece).incrementMoves();
                }
                if (this.checkForMate(this.getCurrentGame().whichTurn())){
                    this.getCurrentGame().setCheckMate(true);
                    this.getCurrentGame().setWhiteWon(!currentGame.whichTurn());
                    System.out.println("Checkmate state set for " + (currentGame.isWhiteWon() ? "White" : "Black"));
                } else if (this.checkForStale(this.getCurrentGame().whichTurn())) {
                    this.getCurrentGame().setStaleMate(true);
                }
                System.out.println(this.isKingInCheck(!piece.isWhite()));

                updateEnPassantTargetSquare(piece, startRow, endRow, endCol);
                System.out.println(this.enPassantTargetSquare);


                currentGame.gameGUI.repaint();

                return true;
            }
        }
        return false;
    }


    public ArrayList<int[]> calculateLegalMoves(Piece piece) {
        ArrayList<int[]> legalMoves = new ArrayList<>();
        int startX = piece.getX();
        int startY = piece.getY();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (piece.isValidMove(this, startX, startY, x, y)) {
                    // Temporarily make the move
                    Piece capturedPiece = makeMove(startX, startY, x, y);
                    // Check if the move resolves the check condition
                    if (!isKingInCheck(piece.isWhite())) {
                        legalMoves.add(new int[]{y, x}); // y, x
                    }
                    // Undo the move
                    undoMove(startX, startY, x, y, capturedPiece);

                }
            }
        }
        return legalMoves;
    }

    private Piece makeMove(int startX, int startY, int endX, int endY) {
        Piece movingPiece = piecePosition[startY][startX];
        Piece capturedPiece = piecePosition[endY][endX];
        piecePosition[endY][endX] = movingPiece;
        piecePosition[startY][startX] = null;
        movingPiece.setBoardPosition(endY, endX);
        return capturedPiece;
    }

    private void undoMove(int startX, int startY, int endX, int endY, Piece capturedPiece) {
        Piece movingPiece = piecePosition[endY][endX];
        piecePosition[startY][startX] = movingPiece;
        piecePosition[endY][endX] = capturedPiece;
        movingPiece.setBoardPosition(startY, startX);
    }

    public boolean isKingInCheck(boolean isWhite) {
        King king = findKing(isWhite);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = piecePosition[i][j];
                if (piece != null && piece.isWhite() != isWhite) {
                    if (piece.isValidMove(this, piece.getX(), piece.getY(), king.getX(), king.getY())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public King findKing(boolean isWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = piecePosition[i][j];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return (King) piece;
                }
            }
        }
        return null; // Should never happen if the game is in a valid state
    }


    public ChessGame getCurrentGame(){
        return currentGame;
    }

    public boolean checkForMate(boolean isWhite) {
        King king = findKing(isWhite);
        if (king.isChecked()) {

            for (Piece[] pieces : piecePosition) {
                for (Piece piece : pieces) {
                    if (piece != null && piece.isWhite() == isWhite && !this.calculateLegalMoves(piece).isEmpty()) {
                        return false;
                    }
                }
            }
            return true; // No legal moves available
        }
        return false; // King is not in check
    }

    public boolean checkForStale(boolean isWhite) {
        if (isInsufficientMaterial()) {
            return true;
        }

        King king = findKing(isWhite);
        if (!king.isChecked()) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece piece = piecePosition[i][j];
                    if (piece != null && piece.isWhite() == isWhite) {
                        if (!this.calculateLegalMoves(piece).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }


    private boolean isInsufficientMaterial() {
        ArrayList<Piece> whitePieces = new ArrayList<>();
        ArrayList<Piece> blackPieces = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = piecePosition[i][j];
                if (piece != null) {
                    if (piece.isWhite()) {
                        whitePieces.add(piece);
                    } else {
                        blackPieces.add(piece);
                    }
                }
            }
        }

        if (whitePieces.size() == 1 && blackPieces.size() == 1) {
            return true; // Only two kings left
        }

        if (whitePieces.size() == 2 && blackPieces.size() == 1) {
            Piece whitePiece = whitePieces.get(0).getClass() == King.class ? whitePieces.get(1) : whitePieces.get(0);
            if (whitePiece instanceof Knight || whitePiece instanceof Bishop) {
                return true; // White has king and knight or king and bishop
            }
        }

        if (blackPieces.size() == 2 && whitePieces.size() == 1) {
            Piece blackPiece = blackPieces.get(0).getClass() == King.class ? blackPieces.get(1) : blackPieces.get(0);
            if (blackPiece instanceof Knight || blackPiece instanceof Bishop) {
                return true; // Black has king and knight or king and bishop
            }
        }

        if (whitePieces.size() == 3 && blackPieces.size() == 1) {
            int knightCount = 0;
            for (Piece piece : whitePieces) {
                if (piece instanceof Knight) {
                    knightCount++;
                }
            }
            if (knightCount == 2) {
                return true; // White has king and two knights
            }
        }

        if (blackPieces.size() == 3 && whitePieces.size() == 1) {
            int knightCount = 0;
            for (Piece piece : blackPieces) {
                if (piece instanceof Knight) {
                    knightCount++;
                }
            }
            if (knightCount == 2) {
                return true; // Black has king and two knights
            }
        }

        return false;
    }


    private void handlePromotion(Pawn pawn, int row, int col) {
        // Delegate the promotion choice to the GUI
        Piece newPiece = null;
        if (currentGame.gameGUI != null) {
            newPiece = currentGame.gameGUI.handlePromotion(pawn);
        }

        System.out.println(newPiece);

        // Ensure the new piece is not null and correctly updates the board
        if (newPiece != null) {
            newPiece.setBoardPosition(row, col);
            piecePosition[row][col] = newPiece;
            System.out.println(selectPiece(col, row));

            // Update player's pieces
            if (pawn.isWhite()) {
                currentGame.getWhitePlayer().addPiece(newPiece);
            } else {
                currentGame.getBlackPlayer().addPiece(newPiece);
            }
        } else {
            System.out.println("Promotion failed: newPiece is null");
        }
    }

    public Piece getLastMovedPiece() {
        return lastMovedPiece;
    }

    public void setLastMovedPiece(Piece lastMovedPiece) {
        this.lastMovedPiece = lastMovedPiece;
    }

    // Method to get the square label given row and column
    private String getSquareLabel(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return squareLabels[row][col];
        } else {
            throw new IllegalArgumentException("Row and column must be between 0 and 7 inclusive.");
        }
    }

    // Update en passant target square
    public void updateEnPassantTargetSquare(Piece movedPiece, int startRow, int endRow, int endCol) {
        if (movedPiece instanceof Pawn && Math.abs(startRow - endRow) == 2) {
            int targetRow = (startRow + endRow) / 2;
            enPassantTargetSquare = getSquareLabel(targetRow, endCol);
        } else {
            enPassantTargetSquare = "-";
        }
    }
}
