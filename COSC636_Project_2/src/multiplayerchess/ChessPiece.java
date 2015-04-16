package multiplayerchess;

import java.io.Serializable;

/**
 * This program implements the piece Class.
 * 
 * @date 4/16/2015
 */
public class ChessPiece implements Serializable {

    public static final long serialVersionUID = 54556922;

    /*
     Colors that can be used
     */
    public enum COLOR {

        WHITE, BLACK
    };

    /*
     Pieces that can be used
     */
    public enum TYPE {

        ROOK, QUEEN, KING, BISHOP, PAWN, KNIGHT
    };
    COLOR color;
    TYPE type;
    boolean isAvailable;
    private int row, col;

    /**
     * Constructs a new Piece with the color, type, row, and column
     *
     * @param color Color of the Piece
     * @param type Type of piece
     * @param row Row that the piece is currently in
     * @param col Column that the piece is currently in
     */
    public ChessPiece(COLOR color, TYPE type, int row, int col) {
        this.color = color;
        this.type = type;
        this.col = col;
        this.row = row;
        this.isAvailable = true;
    }
}
