/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayerchess;

import java.io.Serializable;
import multiplayerchess.Piece.*;

/*
This method implements the messages that the server sends over to the client
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

    /*
    Update object that uses playermove, color, counter, and gameover boolean
    */
    public Update(PlayerMove pM, COLOR turn, int count, boolean gameOver) {
        this.pM = pM;
        this.turn = turn;
        this.count = count;
        this.gameOver = gameOver;
        this.changeP = false;
        this.typeRequest = false;
    }

    /*
    Update object that uses playermove, color, counter, type and gameover boolean
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

    /*
    Update object that uses opponents info, color, and gameover boolean
    */
    public Update(String opp, COLOR team, boolean gameOver) {
        this.opp = opp;
        this.team = team;
        this.gameOver = gameOver;
        this.changeP = false;
    }

    /*
    Update object that uses  gameover boolean and the color of the winner
    */
    public Update(boolean gameOver, COLOR winner) {
        this.gameOver = gameOver;
        this.winner = winner;
        this.changeP = false;
        this.typeRequest = false;
    }

    /*
    Update object that uses the type request
    */
    public Update(Boolean typeRequest) {
        this.typeRequest = true;
        this.changeP = false;
        this.gameOver = false;
    }
}
