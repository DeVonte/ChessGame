package multiplayerchess;

import java.io.Serializable;

public class Piece implements Serializable {

    public static final long serialVersionUID = 54556922;

    public enum COLOR {

        WHITE, BLACK
    };

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
