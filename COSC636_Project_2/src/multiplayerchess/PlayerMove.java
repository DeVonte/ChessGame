
package multiplayerchess;

import java.io.Serializable;
import multiplayerchess.Piece.*;

/**
 * This class implements the messages that the clients sends over to the server
 */
public class PlayerMove implements Serializable {

    private static final long serialVersionUID = -9020988873854556933L;
    COLOR playerColor;
    TYPE type;
    int sourceX, sourceY, targetX, targetY;
    String name, dColor, message;
    char flag;
    boolean changePiece;
    
    /*
    Player Move that uses color and all coordinates
    */
    public PlayerMove(COLOR pC, int sX, int sY, int tX, int tY) {
        this.playerColor = pC;
        this.sourceX = sX;
        this.sourceY = sY;
        this.targetX = tX;
        this.targetY = tY;
        this.changePiece = false;
    }

    /*
    Player Move that uses name and color
    */
    public PlayerMove(String name, String dColor) {
        this.name = name;
        this.dColor = dColor;
        this.changePiece = false;
    }

    /*
    Player Move that uses coordinates, type, and color
    */
    public PlayerMove(int x, int y, TYPE type, COLOR c) {
        this.sourceX = x;
        this.sourceY = y;
        this.type = type;
        this.changePiece = true;
        this.playerColor = c;
    }
    
    /*
    Player Move that uses type
    */
    public PlayerMove(TYPE type) {
        this.type = type;
    }

    /*
    Player Move that uses flag and the name of player
    */
    public PlayerMove(char f, String name) {
        this.flag = f;
        this.name = name;
    }

    /*
    Player move that uses flag, name of player, and message
    */
    public PlayerMove(char f, String name, String message) {
        this.flag = f;
        this.name = name;
        this.message = message;
    }
}
