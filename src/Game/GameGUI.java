package Game;

import Board.Board;
import Pieces.King;
import Pieces.Piece;
import Pieces.*;
import Game.ChessGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GameGUI extends JPanel implements MouseListener {
    private ChessGame chessGame; // Reference to ChessGame
    private Board board;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private ArrayList<int[]> legalMoves = new ArrayList<>();
    private boolean isBoardFlipped = false; // Add this flag

    public GameGUI(Board board, ChessGame chessGame) {
        this.chessGame = chessGame;
        this.board = board; // Get the initial board from ChessGame
        setPreferredSize(new Dimension(400, 400)); // Adjust as needed

        // Add mouse listener to handle clicks
        addMouseListener(this);
    }

    public void resetGame() {
        this.chessGame = new ChessGame(); // Create a new ChessGame instance
        this.board = chessGame.getBoard(); // Update the board
        selectedRow = -1;
        selectedCol = -1;
        legalMoves.clear();
        if (isBoardFlipped) {
            flipBoard();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Render the board colors
        for (int i = 0; i < board.getBoardColours().length; i++) {
            for (int j = 0; j < board.getBoardColours()[i].length; j++) {
                Color color = (board.getBoardColours()[i][j] == 1) ? Color.WHITE : Color.darkGray;
                g.setColor(color);

                int x = isBoardFlipped ? (7 - j) * 50 : j * 50;
                int y = isBoardFlipped ? (7 - i) * 50 : i * 50;

                g.fillRect(x, y, 50, 50);
            }
        }

        // Render the pieces
        for (int i = 0; i < board.getPiecePosition().length; i++) {
            for (int j = 0; j < board.getPiecePosition()[i].length; j++) {
                Piece piece = board.getPiecePosition()[i][j];
                if (piece != null) {
                    Image img = getImageForPiece(piece);

                    int x = isBoardFlipped ? (7 - j) * 50 : j * 50;
                    int y = isBoardFlipped ? (7 - i) * 50 : i * 50;

                    g.drawImage(img, x, y, 50, 50, this);
                }
            }
        }

        // Highlight the king's square if in check
        highlightKingCheck(g);

        // Highlight selected square, if any
        if (selectedRow != -1 && selectedCol != -1) {
            int x = isBoardFlipped ? (7 - selectedCol) * 50 : selectedCol * 50;
            int y = isBoardFlipped ? (7 - selectedRow) * 50 : selectedRow * 50;

            g.setColor(Color.blue);
            g.drawRect(x, y, 50, 50);

            // Highlight legal moves
            g.setColor(new Color(144, 238, 144, 128)); // Light green (semi-transparent)
            for (int[] move : legalMoves) {
                int moveX = isBoardFlipped ? (7 - move[1]) * 50 : move[1] * 50;
                int moveY = isBoardFlipped ? (7 - move[0]) * 50 : move[0] * 50;

                g.fillRect(moveX, moveY, 50, 50); // x, y
            }
        }
    }

    private void highlightKingCheck(Graphics g) {
        King whiteKing = board.findKing(true);
        King blackKing = board.findKing(false);

        if (whiteKing != null && whiteKing.isChecked()) {
            int x = isBoardFlipped ? (7 - whiteKing.getX()) * 50 : whiteKing.getX() * 50;
            int y = isBoardFlipped ? (7 - whiteKing.getY()) * 50 : whiteKing.getY() * 50;

            g.setColor(Color.RED);
            g.drawRect(x, y, 50, 50);
        }

        if (blackKing != null && blackKing.isChecked()) {
            int x = isBoardFlipped ? (7 - blackKing.getX()) * 50 : blackKing.getX() * 50;
            int y = isBoardFlipped ? (7 - blackKing.getY()) * 50 : blackKing.getY() * 50;

            g.setColor(Color.RED);
            g.drawRect(x, y, 50, 50);
        }
    }

    private Image getImageForPiece(Piece piece) {
        String color = (piece.isWhite()) ? "white" : "black";
        String pieceType = piece.getClass().getSimpleName().toLowerCase(); // Assumes class name matches file name
        URL imageURL = getClass().getResource("/Pieces/" + color + "_" + pieceType + ".png");

        if (imageURL == null) {
            System.err.println("Resource not found: /Pieces/" + color + "_" + pieceType + ".png");
            // Return a default image or handle error as needed
            return null;
        }

        return new ImageIcon(imageURL).getImage();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int row = isBoardFlipped ? (getHeight() - y) / 50 : y / 50;
        int col = isBoardFlipped ? (getWidth() - x) / 50 : x / 50;

        Piece clickedPiece = board.selectPiece(col, row);
        if (clickedPiece != null && clickedPiece.isWhite() != board.getCurrentGame().whichTurn()) {
            clickedPiece = null;
        }

        if (selectedRow == -1 && selectedCol == -1) {
            // No piece selected, try to select one
            if (clickedPiece != null) {
                selectedRow = row;
                selectedCol = col;
                legalMoves = board.calculateLegalMoves(clickedPiece);
            }
        } else {
            // A piece is already selected
            if (row == selectedRow && col == selectedCol) {
                // Clicked on the same piece again, deselect it
                selectedRow = -1;
                selectedCol = -1;
            } else {
                // Try to move the selected piece to the clicked position
                boolean moveValid = board.movePiece(selectedRow, selectedCol, row, col);
                if (moveValid) {
                    // Move successful, clear selection
                    selectedRow = -1;
                    selectedCol = -1;
                    if (board.getCurrentGame().getCheckMate()) {
                        SwingUtilities.invokeLater(() -> {
                            showCheckMate(board.getCurrentGame().isWhiteWon());
                        });
                    } else if (board.getCurrentGame().isStaleMate()) {
                        SwingUtilities.invokeLater(this::showStalemate);
                    }
                } else {
                    // Move invalid, keep selection
                    // Optionally provide feedback to the user
                }
            }
        }

        // Repaint the board after any updates
        repaint();
    }

    public void showCheckMate(boolean isWhite) {
        String winner = isWhite ? "White" : "Black";
        JOptionPane.showMessageDialog(this, winner + " wins by checkmate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStalemate() {
        JOptionPane.showMessageDialog(this, "Stalemate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public Piece handlePromotion(Pawn pawn) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(this, "Promote to:", "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        Piece newPiece;
        switch (choice) {
            case 1:
                newPiece = new Rook(pawn.isWhite());
                break;
            case 2:
                newPiece = new Bishop(pawn.isWhite());
                break;
            case 3:
                newPiece = new Knight(pawn.isWhite());
                break;
            default:
                newPiece = new Queen(pawn.isWhite());
        }

        return newPiece;
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public void flipBoard() {
        isBoardFlipped = !isBoardFlipped;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 429); // Adjust as needed
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGame chessGame = new ChessGame();
            GameGUI gameGUI = chessGame.gameGUI; // Ensure gameGUI is accessible here

            // Create the JFrame
            JFrame frame = new JFrame("Chess Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Center the frame

            // Create the menu bar
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("Menu");
            JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
            flipBoardMenuItem.addActionListener(e -> gameGUI.flipBoard());
            menu.add(flipBoardMenuItem);

            // Add "New Game" option
            JMenuItem newGameMenuItem = new JMenuItem("New Game");
            newGameMenuItem.addActionListener(e -> {
                gameGUI.resetGame(); // Reset game
                // Optionally, you might want to update the frame size here if needed
            });
            menu.add(newGameMenuItem);

            JMenuItem offerDrawMenuItem = new JMenuItem("Offer a Draw");
            offerDrawMenuItem.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(frame, "Draw?", "Offer a Draw", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    gameGUI.resetGame(); // Reset game if the draw is accepted
                }
                // Do nothing if the draw is declined
            });
            menu.add(offerDrawMenuItem);

            menuBar.add(menu);

            menuBar.add(menu);

            // Add GameGUI to the frame
            frame.add(gameGUI);

            // Add menu bar to the frame
            frame.setJMenuBar(menuBar);

            // Pack the frame to fit its components
            frame.pack();

            // Calculate the required size for the menu bar
            Dimension menuBarSize = menuBar.getPreferredSize();
            Dimension gameGUIPrefSize = gameGUI.getPreferredSize();
            int width = gameGUIPrefSize.width;
            int height = gameGUIPrefSize.height + menuBarSize.height;

            // Set the size of the frame
            frame.setSize(width, height);

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }


}
