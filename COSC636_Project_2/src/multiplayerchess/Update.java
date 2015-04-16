package multiplayerchess;

import java.io.Serializable;
import multiplayerchess.ChessPiece.*;

/**
 * This method implements the messages that the server sends over to the client
 * 
* @date 4/16/2015
 */
public class Update implements Serializable {

    private static final long serialVersionUID = -9020988873854556966L;
    int count, game;
    TYPE newType;
    COLOR turn, team, winner;
    PlayerMove pM;
    String opp;
    boolean gameOver, typeRequest;
    boolean changeP;

    /**
     * Constructor that creates update object with player move, color, game
     * counter, and gameOver booleans
     *
     * @param theMove Move that player makes
     * @param turn Color of whose turn it is
     * @param count GameCounter
     * @param gameOver Is the game over
     */
    public Update(PlayerMove theMove, COLOR turn, int count, boolean gameOver) {
        this.pM = theMove;
        this.turn = turn;
        this.count = count;
        this.gameOver = gameOver;
        this.changeP = false;
        this.typeRequest = false;
    }

    /**
     * Constructor that creates update object with player move, color, game
     * counter, gameOver booleans, piece type and change player boolean
     *
     * @param theMove Move that player makes
     * @param turn Color of whose turn it is
     * @param count GameCounter
     * @param gameOver Is the game over
     * @param t Type of piece
     * @param changeP Change Player boolean
     */
    public Update(PlayerMove pM, COLOR turn, int count, boolean gameOver, TYPE t, boolean changeP) {
        this.pM = pM;
        this.turn = turn;
        this.count = count;
        this.gameOver = gameOver;
        this.newType = t;
        this.changeP = changeP;
        this.typeRequest = false;
    }

    /**
     * Constructor that creates update object with opponent color, players
     * color, and game over boolean
     *
     * @param opp Opponent
     * @param team Team color
     * @param gameOver Game over boolean
     */
    public Update(String opp, COLOR team, boolean gameOver) {
        this.opp = opp;
        this.team = team;
        this.gameOver = gameOver;
        this.changeP = false;
    }

    /**
     * Constructor that creates update object with game over boolean and color
     * of winner
     *
     * @param gameOver Game over boolean
     * @param winner Color of winner
     */
    public Update(boolean gameOver, COLOR winner) {
        this.gameOver = gameOver;
        this.winner = winner;
        this.changeP = false;
        this.typeRequest = false;
    }

    /**
     * Constructor that creates update object with type request
     *
     * @param typeRequest Boolean type request
     */
    public Update(Boolean typeRequest) {
        this.typeRequest = true;
        this.changeP = false;
        this.gameOver = false;
    }
}
