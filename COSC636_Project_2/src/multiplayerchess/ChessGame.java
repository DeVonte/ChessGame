/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayerchess;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import multiplayerchess.Piece.COLOR;

public class ChessGame implements Runnable {

    public ChessBoard board;
    Player white, black;
    COLOR turn;
    MoveValidator mV;
    boolean gameOver, colorSwitched;
    Socket p1, p2;
    int gamecount;
    InputStream fromPlayer1, fromPlayer2;
    OutputStream toPlayer1, toPlayer2;
    ObjectInputStream ois1, ois2;
    ObjectOutputStream oos1, oos2;
    View v;

    public ChessGame(Socket p1, Socket p2, int gameCount) throws IOException {
        this.gamecount = gameCount;
        this.p1 = p1;
        this.p2 = p2;
        this.gameOver = false;
        this.colorSwitched = false;
        mV = new MoveValidator();
        System.out.println("GAME SERVER: Starting game: " + gameCount);
        oos1 = new ObjectOutputStream(p1.getOutputStream());
        System.out.println("GAME SERVER: p1 outputstream created");
        oos2 = new ObjectOutputStream(p2.getOutputStream());
        oos2.flush();
        System.out.println("GAME SERVER: p2 outputstream created");

        v = new View();
    }

    public void run() {
        try {
            board = new ChessBoard();
            System.out.println("GAME SERVER: Starting game logic.");

            ois1 = new ObjectInputStream(p1.getInputStream());
            ois2 = new ObjectInputStream(p2.getInputStream());
            System.out.println("GAME SERVER: p2 and p1 inputstreams created");

            int ucount = 0;
            Update u1, u2;
            PlayerMove nameColor1 = (PlayerMove) ois1.readObject();
            PlayerMove nameColor2 = (PlayerMove) ois2.readObject();
            if (nameColor1.dColor.equalsIgnoreCase("white") && nameColor2.dColor.equalsIgnoreCase("black")) {
                u1 = new Update(nameColor2.name, COLOR.WHITE, false); //opponent name, team color
                u2 = new Update(nameColor1.name, COLOR.BLACK, false);
            } else if (nameColor1.dColor.equalsIgnoreCase("black") && nameColor2.dColor.equalsIgnoreCase("white")) {
                u1 = new Update(nameColor2.name, COLOR.BLACK, false);
                u2 = new Update(nameColor1.name, COLOR.WHITE, false);
                colorSwitched = true;
            } else {
            	int tmp = (int) ( Math.random() * 2 + 1);
            	
            	if (tmp == 1){
            		nameColor1.dColor = "white";
            		nameColor2.dColor = "black";
                    u1 = new Update(nameColor2.name, COLOR.WHITE, false);
                    u2 = new Update(nameColor1.name, COLOR.BLACK, false);
            	}
            	else{
            		nameColor1.dColor = "black";
            		nameColor2.dColor = "white";
                    u1 = new Update(nameColor2.name, COLOR.BLACK, false);
                    u2 = new Update(nameColor1.name, COLOR.WHITE, false);
            	}
            }
            oos1.writeObject(u1);
            oos2.writeObject(u2);
            Update u = new Update(null, board.gamestate, ucount++, false); //initial update for gamestate
            oos1.writeObject(u);
            oos2.writeObject(u);
            PlayerMove pMove = null;
            while (!gameOver) {
                boolean c = false;
                PlayerMove changeP = null;
                System.out.println("GAME SERVER: " + board.gamestate + " turn.");
                switch (board.gamestate) {
                    case WHITE:

                        boolean validwMove = false;
                        boolean killshot;
                        while (!validwMove) {
                            if (!colorSwitched) {
                                pMove = (PlayerMove) ois1.readObject(); //read the player's move                                                                     
                                c = mV.checkForPieceSwitch(pMove, board);
                                killshot = mV.kingAtTarget(pMove, board);
                                validwMove = mV.Validate(pMove, board); //check if it is valid, repeat while loop if it isn't                            
                                if (killshot && validwMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.WHITE);
                                    oos1.writeObject(win);
                                    oos2.writeObject(win);
                                } else if (!validwMove) {
                                    Update redo = new Update(null, board.gamestate, ucount++, false);
                                    oos1.writeObject(redo);
                                }

                                if (c && validwMove) {
                                    Update upd = new Update(true);
                                    oos1.writeObject(upd);
                                    changeP = (PlayerMove) ois1.readObject();
                                }
                            } else {
                                pMove = (PlayerMove) ois2.readObject(); //read the player's move
                                c = mV.checkForPieceSwitch(pMove, board);
                                killshot = mV.kingAtTarget(pMove, board);
                                validwMove = mV.Validate(pMove, board); //check if it is valid, repeat while loop if it isn't
                                if (killshot && validwMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.WHITE);
                                    oos2.writeObject(win);
                                    oos1.writeObject(win);

                                } else if (!validwMove) {
                                    Update redo = new Update(null, board.gamestate, ucount++, false);
                                    oos2.writeObject(redo);
                                }
                                if (c && validwMove) {
                                    Update upd = new Update(true);
                                    oos2.writeObject(upd);
                                    changeP = (PlayerMove) ois2.readObject();
                                }
                            }
                        }
                        break;
                    case BLACK:
                        boolean validbMove = false;
                        while (!validbMove) {
                            if (!colorSwitched) {
                                pMove = (PlayerMove) ois2.readObject(); //read the player's move
                                c = mV.checkForPieceSwitch(pMove, board);

                                killshot = mV.kingAtTarget(pMove, board);
                                validbMove = mV.Validate(pMove, board); //check if it is valid, repeat while loop if it isn't

                                if (killshot && validbMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.BLACK);
                                    oos2.writeObject(win);
                                    oos1.writeObject(win);
                                } else if (!validbMove) {
                                    Update redo = new Update(null, board.gamestate, ucount++, false);
                                    oos2.writeObject(redo);
                                }
                                if (c && validbMove) {
                                    Update upd = new Update(true);
                                    oos2.writeObject(upd);
                                    changeP = (PlayerMove) ois2.readObject();
                                }
                            } else {
                                pMove = (PlayerMove) ois1.readObject(); //read the player's move
                                c = mV.checkForPieceSwitch(pMove, board);

                                killshot = mV.kingAtTarget(pMove, board);
                                validbMove = mV.Validate(pMove, board); //check if it is valid, repeat while loop if it isn't
                                if (killshot && validbMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.BLACK);
                                    oos1.writeObject(win);
                                    oos2.writeObject(win);
                                } else if (!validbMove) {
                                    Update redo = new Update(null, board.gamestate, ucount++, false);
                                    oos1.writeObject(redo);
                                }
                                if (c && validbMove) {
                                    Update upd = new Update(true);
                                    oos1.writeObject(upd);
                                    changeP = (PlayerMove) ois1.readObject();
                                }
                            }
                        }
                        break;
                }
                System.out.println("GAME SERVER: Move accepted... Making move and changing gamestate on server");
                    //v.printBoard(board.board);

                if (!gameOver) {
                    if (c) {
                        board = doMove(pMove, board, changeP.type);
                        board = changeState(board);
                        //board.board[changeP.targetX][changeP.targetY].type=changeP.type;
                        Update update = new Update(pMove, board.gamestate, ucount++, false, changeP.type, true);
                        oos1.writeObject(update);
                        oos2.writeObject(update);
                    } else {
                        board = doMove(pMove, board);
                        board = changeState(board);
                        Update update = new Update(pMove, board.gamestate, ucount++, false);
                        oos1.writeObject(update);
                        oos2.writeObject(update);
                    }
                } else {
                    ois1.close();
                    ois2.close();
                    toPlayer1.close();
                    toPlayer2.close();
                    fromPlayer1.close();
                    fromPlayer2.close();
                        //p1.close();
                    //p2.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ChessGame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChessGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ChessBoard doMove(PlayerMove pM, ChessBoard b) {
        Piece piece = b.board[pM.sourceX][pM.sourceY]; //get piece
        b.board[pM.targetX][pM.targetY] = piece; //move the piece
        b.board[pM.sourceX][pM.sourceY] = null;// set original space to null

        return b;
    }

    public ChessBoard doMove(PlayerMove pM, ChessBoard b, Piece.TYPE t) {
        Piece piece = b.board[pM.sourceX][pM.sourceY]; //get piece
        piece.type = t;
        b.board[pM.targetX][pM.targetY] = piece; //move the piece
        b.board[pM.sourceX][pM.sourceY] = null;// set original space to null
        return b;
    }

    public ChessBoard changeState(ChessBoard b) {
        b.changeState(); //change gamestate
        return b;
    }

}
