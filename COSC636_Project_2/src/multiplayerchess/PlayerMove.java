
package multiplayerchess;

import java.io.Serializable;
import multiplayerchess.ChessPiece.*;

/**
 * This class implements the messages that the clients sends over to the server
 * 
 * @date 4/16/2015
 */
public class PlayerMove implements Serializable {

    private static final long serialVersionUID = -9020988873854556933L;
    COLOR playerColor;
    TYPE type;
    int sourceX, sourceY, targetX, targetY;
    String name;
    String pieceColor;
    String message;
    char flag;
    boolean changePiece;
    
    /**
     * Constructor that creates playermove with color and coordinates
     * @param pC Color of piece
     * @param sX Source X coords
     * @param sY Source Y coords
     * @param tX Target X coords
     * @param tY Target Y coords
     */
    public PlayerMove(COLOR pC, int sX, int sY, int tX, int tY) {
        this.playerColor = pC;
        this.sourceX = sX;
        this.sourceY = sY;
        this.targetX = tX;
        this.targetY = tY;
        this.changePiece = false;
    }

    /**
     * Constructor that creates player move with name and color
     * @param name Name of player
     * @param theColor Color of piece
     */
    public PlayerMove(String name, String theColor) {
        this.name = name;
        this.pieceColor = theColor;
        this.changePiece = false;
    }

    /**
     * Constructor that creates player move with source coords, type, and color
     * @param x Source x coordinates
     * @param y Source y coordinates
     * @param type Type of piece
     * @param col Color of piece
     */
    public PlayerMove(int x, int y, TYPE type, COLOR col) {
        this.sourceX = x;
        this.sourceY = y;
        this.type = type;
        this.changePiece = true;
        this.playerColor = col;
    }
    
    /**
     * Constructor that creates player move with type
     * @param type Type of piece
     */
    public PlayerMove(TYPE type) {
        this.type = type;
    }

    /**
     * Constructor that creates player move with flag and name of player
     * @param f Flag for move
     * @param name Name of player
     */
    public PlayerMove(char f, String name) {
        this.flag = f;
        this.name = name;
    }

    /**
     * Constructor that creates player move with flag, name of player, and message
     * @param f Flag for move
     * @param name Name of player
     * @param msg Message for move
     */
    public PlayerMove(char f, String name, String msg) {
        this.flag = f;
        this.name = name;
        this.message = msg;
    }
}
