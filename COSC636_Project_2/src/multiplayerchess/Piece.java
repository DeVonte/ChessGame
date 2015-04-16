package multiplayerchess;

import java.io.Serializable;

/*
This program implements the piece Class.
*/
public class Piece implements Serializable {

    public static final long serialVersionUID = 54556922;

    /*
    Enum to describe each color
    */
    public enum COLOR {

        WHITE, BLACK
    };

    /*
    Enum to describe each piece type
    */
    public enum TYPE {

        ROOK, QUEEN, KING, BISHOP, PAWN, KNIGHT
    };
    COLOR color;
    TYPE type;
    boolean isAvailable;
    private int row, col;

    public Piece(COLOR color, TYPE type, int row, int col) {
        this.color = color;
        this.type = type;
        this.col = col;
        this.row = row;
        this.isAvailable = true;
    }
}
