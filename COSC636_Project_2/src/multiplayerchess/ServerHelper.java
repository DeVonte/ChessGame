package multiplayerchess;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import multiplayerchess.ChessPiece.COLOR;

/**
 * This class acts as the worker for the Server, it handles the chess game
 *
 * @date 4/16/2015
 */
public class ServerHelper implements Runnable {

    public ChessBoard board;
    Client white, black;
    COLOR turn;
    MoveValidator mV;
    boolean gameOver, colorSwitched;
    Socket p1, p2;
    int gamecount;
    InputStream fromPlayer1, fromPlayer2;
    OutputStream toPlayer1, toPlayer2;
    ObjectInputStream input1, input2;
    ObjectOutputStream output1, output2;
    View chessView;

    /**
     * Constructor for creating chess game with two sockets and the game count
     *
     * @param p1 First Socket
     * @param p2 Second Socket
     * @param gameCount The game counter
     * @throws IOException
     */
    public ServerHelper(Socket p1, Socket p2, int gameCount) throws IOException {
        this.gamecount = gameCount;
        this.p1 = p1;
        this.p2 = p2;
        this.gameOver = false;
        this.colorSwitched = false;
        mV = new MoveValidator();
        System.out.println("GAME SERVER: Starting game # : " + gameCount);
        output1 = new ObjectOutputStream(p1.getOutputStream());
        System.out.println("GAME SERVER: p1 outputstream created");
        output2 = new ObjectOutputStream(p2.getOutputStream());
        output2.flush();
        System.out.println("GAME SERVER: p2 outputstream created");

        chessView = new View();
    }

    /**
     * This method runs and starts the chess game
     */
    public void run() {
        try {
            board = new ChessBoard();
            System.out.println("GAME SERVER: Starting game logic.");

            input1 = new ObjectInputStream(p1.getInputStream());
            input2 = new ObjectInputStream(p2.getInputStream());
            System.out.println("GAME SERVER: p2 and p1 inputstreams created");

            int gCount = 0;
            Update u1, u2;
            PlayerMove color1 = (PlayerMove) input1.readObject();
            PlayerMove color2 = (PlayerMove) input2.readObject();
            if (color1.pieceColor.equalsIgnoreCase("white") && color2.pieceColor.equalsIgnoreCase("black")) {
                u1 = new Update(color2.name, COLOR.WHITE, false);
                u2 = new Update(color1.name, COLOR.BLACK, false);
            } else if (color1.pieceColor.equalsIgnoreCase("black") && color2.pieceColor.equalsIgnoreCase("white")) {
                u1 = new Update(color2.name, COLOR.BLACK, false);
                u2 = new Update(color1.name, COLOR.WHITE, false);
                colorSwitched = true;
            } else {
                int tmp = (int) (Math.random() * 2 + 1);

                if (tmp == 1) {
                    color1.pieceColor = "white";
                    color2.pieceColor = "black";
                    u1 = new Update(color2.name, COLOR.WHITE, false);
                    u2 = new Update(color1.name, COLOR.BLACK, false);
                } else {
                    color1.pieceColor = "black";
                    color2.pieceColor = "white";
                    u1 = new Update(color2.name, COLOR.BLACK, false);
                    u2 = new Update(color1.name, COLOR.WHITE, false);
                }
            }
            output1.writeObject(u1);
            output2.writeObject(u2);
            Update u = new Update(null, board.gamestate, gCount++, false);
            output1.writeObject(u);
            output2.writeObject(u);
            PlayerMove theMove = null;
            while (!gameOver) {
                boolean checker = false;
                PlayerMove changeP = null;
                System.out.println("GAME SERVER: " + board.gamestate + " turn.");
                switch (board.gamestate) {
                    case WHITE:

                        boolean isValidMove = false;
                        boolean checkMate;
                        while (!isValidMove) {
                            if (!colorSwitched) {
                                theMove = (PlayerMove) input1.readObject(); //read the player's move                                                                     
                                checker = mV.checkPieceSwitch(theMove, board);
                                checkMate = mV.kingAtTarget(theMove, board);
                                isValidMove = mV.Validate(theMove, board); //check if it is valid, repeat while loop if it isn't                            
                                if (checkMate && isValidMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.WHITE);
                                    output1.writeObject(win);
                                    output2.writeObject(win);
                                } else if (!isValidMove) {
                                    Update redo = new Update(null, board.gamestate, gCount++, false);
                                    output1.writeObject(redo);
                                }

                                if (checker && isValidMove) {
                                    Update upd = new Update(true);
                                    output1.writeObject(upd);
                                    changeP = (PlayerMove) input1.readObject();
                                }
                            } else {
                                theMove = (PlayerMove) input2.readObject(); //read the player's move
                                checker = mV.checkPieceSwitch(theMove, board);
                                checkMate = mV.kingAtTarget(theMove, board);
                                isValidMove = mV.Validate(theMove, board);
                                if (checkMate && isValidMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.WHITE);
                                    output2.writeObject(win);
                                    output1.writeObject(win);

                                } else if (!isValidMove) {
                                    Update redo = new Update(null, board.gamestate, gCount++, false);
                                    output2.writeObject(redo);
                                }
                                if (checker && isValidMove) {
                                    Update upd = new Update(true);
                                    output2.writeObject(upd);
                                    changeP = (PlayerMove) input2.readObject();
                                }
                            }
                        }
                        break;
                    case BLACK:
                        boolean validbMove = false;
                        while (!validbMove) {
                            if (!colorSwitched) {
                                theMove = (PlayerMove) input2.readObject(); //read the player's move
                                checker = mV.checkPieceSwitch(theMove, board);

                                checkMate = mV.kingAtTarget(theMove, board);
                                validbMove = mV.Validate(theMove, board); //check if it is valid, repeat while loop if it isn't

                                if (checkMate && validbMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.BLACK);
                                    output2.writeObject(win);
                                    output1.writeObject(win);
                                } else if (!validbMove) {
                                    Update redo = new Update(null, board.gamestate, gCount++, false);
                                    output2.writeObject(redo);
                                }
                                if (checker && validbMove) {
                                    Update upd = new Update(true);
                                    output2.writeObject(upd);
                                    changeP = (PlayerMove) input2.readObject();
                                }
                            } else {
                                theMove = (PlayerMove) input1.readObject(); //read the player's move
                                checker = mV.checkPieceSwitch(theMove, board);

                                checkMate = mV.kingAtTarget(theMove, board);
                                validbMove = mV.Validate(theMove, board); //check if it is valid, repeat while loop if it isn't
                                if (checkMate && validbMove) {
                                    gameOver = true;
                                    Update win = new Update(true, COLOR.BLACK);
                                    output1.writeObject(win);
                                    output2.writeObject(win);
                                } else if (!validbMove) {
                                    Update redo = new Update(null, board.gamestate, gCount++, false);
                                    output1.writeObject(redo);
                                }
                                if (checker && validbMove) {
                                    Update upd = new Update(true);
                                    output1.writeObject(upd);
                                    changeP = (PlayerMove) input1.readObject();
                                }
                            }
                        }
                        break;
                }
                System.out.println("GAME SERVER: Move accepted... Making move and changing gamestate on server");

                if (!gameOver) {
                    if (checker) {
                        board = doMove(theMove, board, changeP.type);
                        board = changeState(board);
                        Update update = new Update(theMove, board.gamestate, gCount++, false, changeP.type, true);
                        output1.writeObject(update);
                        output2.writeObject(update);
                    } else {
                        board = doMove(theMove, board);
                        board = changeState(board);
                        Update update = new Update(theMove, board.gamestate, gCount++, false);
                        output1.writeObject(update);
                        output2.writeObject(update);
                    }
                } else {
                    input1.close();
                    input2.close();
                    toPlayer1.close();
                    toPlayer2.close();
                    fromPlayer1.close();
                    fromPlayer2.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method changes the game state of the board
     *
     * @param newB Board to change the gamestate on
     * @return newChess Board
     */
    public ChessBoard changeState(ChessBoard newB) {
        newB.changeGameState(); //change gamestate
        return newB;
    }

    /**
     * This method performs the move on the server side board
     *
     * @param theMove Move to make
     * @param newB Board to change
     * @return Newly changed board
     */
    public ChessBoard doMove(PlayerMove theMove, ChessBoard newB) {
        ChessPiece piece = newB.board[theMove.sourceX][theMove.sourceY];
        newB.board[theMove.targetX][theMove.targetY] = piece;
        newB.board[theMove.sourceX][theMove.sourceY] = null;

        return newB;
    }

    /**
     * This method performs the move on the server side board with the type
     *
     * @param theMove The move that the player is making
     * @param newB The Board that needs to be changed
     * @param t Type of piece
     * @return New chessboard
     */
    public ChessBoard doMove(PlayerMove theMove, ChessBoard newB, ChessPiece.TYPE t) {
        ChessPiece piece = newB.board[theMove.sourceX][theMove.sourceY];         piece.type = t;
        newB.board[theMove.targetX][theMove.targetY] = piece; //move the piece
        newB.board[theMove.sourceX][theMove.sourceY] = null;// set original space to null
        return newB;
    }

}
