/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package multiplayerchess;
import java.io.Serializable;
import multiplayerchess.Piece.*;
public class PlayerMove implements Serializable {
    private static final long serialVersionUID = -9020988873854556933L;
    COLOR playerColor;
    TYPE type;
    int sourceX, sourceY, targetX, targetY;
    String name, dColor, message;
    char flag;
    boolean changePiece;
    
    public PlayerMove(COLOR pC, int sX, int sY, int tX, int tY){
        this.playerColor=pC;
        this.sourceX= sX;
        this.sourceY=sY;
        this.targetX=tX;
        this.targetY=tY;
        this.changePiece = false;
    }
    
    public PlayerMove(String name, String dColor){
        this.name = name;
        this.dColor = dColor;
        this.changePiece=false;       
    }
    
    public PlayerMove(int x, int y, TYPE type, COLOR c){
        this.sourceX= x;
        this.sourceY=y;
        this.type = type;
        this.changePiece=true;
        this.playerColor = c; 
    }
    
    public PlayerMove(TYPE type){
        this.type = type;
    }
    
    public PlayerMove(char f, String name){
    	this.flag = f;
    	this.name = name;
    }
    public PlayerMove(char f, String name, String message){
    	this.flag = f;
    	this.name = name;
    	this.message = message;
    }
}
