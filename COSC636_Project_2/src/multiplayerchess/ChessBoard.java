
package multiplayerchess;

import java.io.Serializable;
import javax.swing.*;
import multiplayerchess.ChessPiece.COLOR;
import multiplayerchess.ChessPiece.TYPE;

/**
 * This class implements the ChessBoard
 * @date 4/16/2015
 */
public class ChessBoard extends JFrame implements Serializable {

    public ChessPiece[][] board = new ChessPiece[8][8];
    public ChessPiece[][] blackboard = new ChessPiece[8][8];
    public COLOR gamestate;
    View newV;

    /**
     * Constructor that creates new chess board
     */
    public ChessBoard() {
        initializeWhiteBoard();
        initializeBlackBoard();
        newV = new View();
    }

    /**
     * This method creates the board for the person that chooses white
     */
    public void initializeWhiteBoard() {
        for (int i = 0; i < 8; i++) {
            addPiece(COLOR.BLACK, TYPE.PAWN, 6, i);
        }
        addPiece(COLOR.BLACK, TYPE.ROOK, 7, 0);
        addPiece(COLOR.BLACK, TYPE.KNIGHT, 7, 1);
        addPiece(COLOR.BLACK, TYPE.BISHOP, 7, 2);
        addPiece(COLOR.BLACK, TYPE.QUEEN, 7, 3);
        addPiece(COLOR.BLACK, TYPE.KING, 7, 4);
        addPiece(COLOR.BLACK, TYPE.BISHOP, 7, 5);
        addPiece(COLOR.BLACK, TYPE.KNIGHT, 7, 6);
        addPiece(COLOR.BLACK, TYPE.ROOK, 7, 7);

        for (int i = 0; i < 8; i++) {
            addPiece(COLOR.WHITE, TYPE.PAWN, 1, i);
        }
        addPiece(COLOR.WHITE, TYPE.ROOK, 0, 0);
        addPiece(COLOR.WHITE, TYPE.KNIGHT, 0, 1);
        addPiece(COLOR.WHITE, TYPE.BISHOP, 0, 2);
        addPiece(COLOR.WHITE, TYPE.QUEEN, 0, 3);
        addPiece(COLOR.WHITE, TYPE.KING, 0, 4);
        addPiece(COLOR.WHITE, TYPE.BISHOP, 0, 5);
        addPiece(COLOR.WHITE, TYPE.KNIGHT, 0, 6);
        addPiece(COLOR.WHITE, TYPE.ROOK, 0, 7);
        
        this.gamestate = COLOR.WHITE;
    }

    /**
     * This method adds a piece to the white board
     * @param color Color of piece
     * @param type Type of piece
     * @param row Row of piece
     * @param col  Column of piece
     */
    public void addPiece(COLOR color, TYPE type, int row, int col) {
        board[col][row] = new ChessPiece(color, type, row, col);
    }

    /**
     * This method adds a piece to the black board
     * @param color Color of piece
     * @param type Type of piece
     * @param row Row of piece
     * @param col Column of piece
     */
    public void addOtherPiece(COLOR color, TYPE type, int row, int col) {
        blackboard[col][row] = new ChessPiece(color, type, row, col);
    }

    

    /**
     * This method creates the board for the person that chooses black
     */
    private void initializeBlackBoard() {
        for (int i = 0; i < 8; i++) {
            addOtherPiece(COLOR.WHITE, TYPE.PAWN, 6, i);
        }
        addOtherPiece(COLOR.WHITE, TYPE.ROOK, 7, 0);
        addOtherPiece(COLOR.WHITE, TYPE.KNIGHT, 7, 1);
        addOtherPiece(COLOR.WHITE, TYPE.BISHOP, 7, 2);
        addOtherPiece(COLOR.WHITE, TYPE.KING, 7, 3);
        addOtherPiece(COLOR.WHITE, TYPE.QUEEN, 7, 4);
        addOtherPiece(COLOR.WHITE, TYPE.BISHOP, 7, 5);
        addOtherPiece(COLOR.WHITE, TYPE.KNIGHT, 7, 6);
        addOtherPiece(COLOR.WHITE, TYPE.ROOK, 7, 7);

        for (int i = 0; i < 8; i++) {
            addOtherPiece(COLOR.BLACK, TYPE.PAWN, 1, i);
        }
        addOtherPiece(COLOR.BLACK, TYPE.ROOK, 0, 0);
        addOtherPiece(COLOR.BLACK, TYPE.KNIGHT, 0, 1);
        addOtherPiece(COLOR.BLACK, TYPE.BISHOP, 0, 2);
        addOtherPiece(COLOR.BLACK, TYPE.KING, 0, 3);
        addOtherPiece(COLOR.BLACK, TYPE.QUEEN, 0, 4);
        addOtherPiece(COLOR.BLACK, TYPE.BISHOP, 0, 5);
        addOtherPiece(COLOR.BLACK, TYPE.KNIGHT, 0, 6);
        addOtherPiece(COLOR.BLACK, TYPE.ROOK, 0, 7);

    }
    
    /**
     * This method changes the state of the board
     */
    public void changeGameState() {
        switch (gamestate) {
            case WHITE:
                gamestate = COLOR.BLACK;
                break;
            case BLACK:
                gamestate = COLOR.WHITE;
                break;
        }
    }
}
