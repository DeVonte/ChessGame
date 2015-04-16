package multiplayerchess;

import java.io.Serializable;
import multiplayerchess.ChessPiece.TYPE;
import multiplayerchess.ChessPiece.COLOR;

/**
 * This method handles the move validations
 *
 * @date 4/16/2015
 */
public class MoveValidator implements Serializable {

    private static final long serialVersionUID = -9020988873854556911L;

    ChessBoard board;

    /**
     * Constructor that creates new move validator
     */
    public MoveValidator() {

    }

    /**
     * This method validates the move
     *
     * @param theMove Move to validate
     * @param board Board to validate move on
     * @return Whether the move is valid or not
     */
    public boolean Validate(PlayerMove theMove, ChessBoard board) {
        boolean valid = false;
        int sX = theMove.sourceX;
        int sY = theMove.sourceY;
        int tX = theMove.targetX;
        int tY = theMove.targetY;
        this.board = board;
        if (isEmpty(sX, sY)) {
            System.out.println("There's no piece there!");
            return false;
        }
        //Check if piece is black or white
        ChessPiece piece = null;
        piece = board.board[sX][sY];

        if (sX > 7 || sX < 0 || sY > 7 || sY < 0 || tX > 7 || tX < 0 || tY > 7 || tY < 0) { //Is it out of bounds?
            System.out.println("Out of bounds");
            return false;
        }
        if (!board.gamestate.equals(theMove.playerColor)) { //Is it the correct color?
            System.out.println("Not your turn");
            return false;
        }
        if (piece.color != theMove.playerColor) { //make sure player color matches peice color
            System.out.println("Wrong color");
            return false;
        }
        if (!isEmpty(tX, tY)) { //if target space is occupied
            if (board.board[tX][tY].color == theMove.playerColor) { //make sure it isn't same color
                System.out.println("You have a piece there already");
                return false;
            }

        }
        switch (piece.type) {
            case PAWN:
                if (piece.color == COLOR.WHITE) {
                    if (sY == 1 && tY == (sY + 2) && sX == tX && isEmpty(tX, tY) && isEmpty(tX, tY - 1)) //if first move and up 2 and empty on 1 and 2
                    {
                        valid = true;
                    } else if (tY == (sY + 1) && sX == tX && isEmpty(tX, tY)) //if up 1 and empty on 1
                    {
                        valid = true;
                    } else if (tY == (sY + 1) && tX == (sX - 1) && !isEmpty(tX, tY))// if 1 up and to the left to capture
                    {
                        valid = true;
                    } else if (tY == (sY + 1) && tX == (sX + 1) && !isEmpty(tX, tY))//if 1 up and to the right to capture
                    {
                        valid = true;
                    } else {
                        System.out.println("invalid move");
                        valid = false;
                    }
                }
                if (piece.color == COLOR.BLACK) {
                    if (sY == 6 && tY == (sY - 2) && sX == tX && isEmpty(tX, tY) && isEmpty(tX, tY + 1)) //if first move and down 2 and empty on 1 and 2
                    {
                        valid = true;
                    } else if (tY == (sY - 1) && sX == tX && isEmpty(tX, tY)) //if up 1 and empty on 1
                    {
                        valid = true;
                    } else if (tY == (sY - 1) && tX == (sX + 1) && !isEmpty(tX, tY))// if 1 up and to the left to capture
                    {
                        valid = true;
                    } //RUN CAPTURE ROUTINE
                    else if (tY == (sY - 1) && tX == (sX - 1) && !isEmpty(tX, tY))//if 1 up and to the right to capture
                    {
                        valid = true;
                    } //RUN CAPTURE ROUTINE
                    else {
                        System.out.println("invalid move");
                        valid = false;
                    }

                }
                break;
            case ROOK:
                int rowCheck = tX;
                int colCheck = tY;
                if (tY != sY && tX != sX) {
                    System.out.println("not on same row or column");
                    return false;
                }

                if (tY == sY) { //if target is on the same row
                    if (tX > sX) { //if target is to the right
                        rowCheck--;
                        while (rowCheck > sX) {
                            if (!isEmpty(rowCheck, tY)) {
                                System.out.println("1.Found a piece at " + rowCheck + " " + tY);
                                return false;
                            }
                            rowCheck--;
                        }
                    } else if (tX < sX) { //if target is to the left
                        rowCheck++;
                        while (rowCheck < sX) {
                            if (!isEmpty(rowCheck, tY)) {
                                System.out.println("2.Found a piece at " + rowCheck + " " + tY);
                                return false;
                            }
                            rowCheck++;
                        }
                    }
                } else if (tX == sX) { //if target is on the same column
                    if (tY > sY) {//if target is above
                        colCheck--;
                        while (colCheck > sY) {
                            if (!isEmpty(tX, colCheck)) {
                                System.out.println("3.Found a piece at " + tX + " " + colCheck);
                                return false;
                            }
                            colCheck--;
                        }
                    } else if (tY < sY) { //if target is below
                        colCheck++;
                        while (colCheck < sY) {
                            if (!isEmpty(tX, colCheck)) {
                                System.out.println("4.Found a piece at " + tX + " " + colCheck);
                                return false;
                            }
                            colCheck++;
                        }
                    }
                }
                valid = true;
                break;
            case KNIGHT:
                if (tX == (sX - 1) && tY == (sY + 2)) //if up 2 and left 1
                {
                    valid = true;
                } else if (tX == (sX + 1) && tY == (sY + 2)) //up 2 right 1
                {
                    valid = true;
                } else if (tX == (sX - 1) && tY == (sY - 2)) //down 2 left 1
                {
                    valid = true;
                } else if (tX == (sX + 1) && tY == (sY - 2)) //down 2 right 1
                {
                    valid = true;
                } else if (tX == (sX - 2) && tY == (sY + 1)) //left 2 up 1
                {
                    valid = true;
                } else if (tX == (sX - 2) && tY == (sY - 1)) //left 2 down 1
                {
                    valid = true;
                } else if (tX == (sX + 2) && tY == (sY + 1)) //right 2 up 1
                {
                    valid = true;
                } else if (tX == (sX + 2) && tY == (sY - 1)) //right 2 down 1
                {
                    valid = true;
                }
                break;
            case BISHOP:

                double slope = (tY - sY) / (tX - sX);
                if (slope != 1.0 && slope != -1.0) {

                    System.out.println("slope: " + slope);
                    System.out.println("Bishop must move along daigonal (slope = |1|)");
                    return false;
                }

                if (tX > sX && tY > sY) {//upRight
                    while (tX != sX && tY != sY) {
                        tX--;
                        tY--;
                        if (!isEmpty(tX, tY) && tX != sX) {
                            System.out.println("Tried to move Bishop but found a piece at " + tX + " " + tY);
                            return false;
                        }
                    }
                } else if (tX < sX && tY > sX) {//upLeft
                    while (tX != sX && tY != sY) {
                        tX++;
                        tY--;
                        if (!isEmpty(tX, tY) && tX != sX) {
                            System.out.println("Tried to move Bishop but found a piece at " + tX + " " + tY);
                            return false;
                        }
                    }
                } else if (tX < sX && tY < sY) {//downLeft
                    while (tX != sX && tY != sY) {
                        tX++;
                        tY++;
                        if (!isEmpty(tX, tY) && tX != sX) {
                            System.out.println("Tried to move Bishop but found a piece at " + tX + " " + tY);
                            return false;
                        }
                    }
                } else if (tX > sX && tY < sY) {//downRight
                    while (tX != sX && tY != sY) {
                        tX--;
                        tY++;
                        if (!isEmpty(tX, tY) && tX != sX) {
                            System.out.println("Tried to move Bishop but found a piece at " + tX + " " + tY);
                            return false;
                        }
                    }
                }
                valid = true;
                break;
            case QUEEN:
                int rowCheck2 = tX;
                int colCheck2 = tY;
                if (tY != sY && tX != sX) {
                    double slope2 = (tY - sY) / (tX - sX);
                    if (slope2 != 1.0 && slope2 != -1.0) {

                        System.out.println("slope: " + slope2);
                        System.out.println("Queen must move along daigonal (slope = |1|)");
                        return false;
                    }

                    if (tX > sX && tY > sY) {//upRight
                        while (tX != sX && tY != sY) {
                            tX--;
                            tY--;
                            if (!isEmpty(tX, tY) && tX != sX) {
                                System.out.println("Tried to move Queen but found a piece at " + tX + " " + tY);
                                return false;
                            }
                        }
                    } else if (tX < sX && tY > sX) {//upLeft
                        while (tX != sX && tY != sY) {
                            tX++;
                            tY--;
                            if (!isEmpty(tX, tY) && tX != sX) {
                                System.out.println("Tried to move Queen but found a piece at " + tX + " " + tY);
                                return false;
                            }
                        }
                    } else if (tX < sX && tY < sY) {//downLeft
                        while (tX != sX && tY != sY) {
                            tX++;
                            tY++;
                            if (!isEmpty(tX, tY) && tX != sX) {
                                System.out.println("Tried to move Queen but found a piece at " + tX + " " + tY);
                                return false;
                            }
                        }
                    } else if (tX > sX && tY < sY) {//downRight
                        while (tX != sX && tY != sY) {
                            tX--;
                            tY++;
                            if (!isEmpty(tX, tY) && tX != sX) {
                                System.out.println("Tried to move Queen but found a piece at " + tX + " " + tY);
                                return false;
                            }
                        }
                    }
                }

                if (tY == sY) { //if target is on the same row
                    if (tX > sX) { //if target is to the right
                        rowCheck2--;
                        while (rowCheck2 > sX) {
                            if (!isEmpty(rowCheck2, tY)) {
                                System.out.println("1.Found a piece at " + rowCheck2 + " " + tY);
                                return false;
                            }
                            rowCheck2--;
                        }
                    } else if (tX < sX) { //if target is to the left
                        rowCheck2++;
                        while (rowCheck2 < sX) {
                            if (!isEmpty(rowCheck2, tY)) {
                                System.out.println("2.Found a piece at " + rowCheck2 + " " + tY);
                                return false;
                            }
                            rowCheck2++;
                        }
                    }
                } else if (tX == sX) { //if target is on the same column
                    if (tY > sY) {//if target is above
                        colCheck2--;
                        while (colCheck2 > sY) {
                            if (!isEmpty(tX, colCheck2)) {
                                System.out.println("3.Found a piece at " + tX + " " + colCheck2);
                                return false;
                            }
                            colCheck2--;
                        }
                    } else if (tY < sY) { //if target is below
                        colCheck2++;
                        while (colCheck2 < sY) {
                            if (!isEmpty(tX, colCheck2)) {
                                System.out.println("4.Found a piece at " + tX + " " + colCheck2);
                                return false;
                            }
                            colCheck2++;
                        }
                    }
                }
                valid = true;
                break;
            case KING:
                if (tX == sX && tY == sY + 1) //up
                {
                    valid = true;
                } else if (tX == sX && tY == sY - 1) //down
                {
                    valid = true;
                } else if (tY == sY && tX == sX + 1) //right
                {
                    valid = true;
                } else if (tY == sY && tX == sX - 1) //left
                {
                    valid = true;
                } else if (tX == (sX - 1) && tY == (sY + 1))//if up 1 and left 1
                {
                    valid = true;
                } else if (tX == (sX + 1) && tY == (sY + 1)) //up 1 right 1
                {
                    valid = true;
                } else if (tX == (sX - 1) && tY == (sY - 1)) //down 1 left 1
                {
                    valid = true;
                } else if (tX == (sX + 1) && tY == (sY - 1)) //down 1 right 1
                {
                    valid = true;
                }
                break;
        }
        if (valid) {
            return true;
        } else {

            System.out.println("Piece trying to move: " + piece.type);
            System.out.println("Color trying to move: " + piece.color);

            return false;
        }
    }

    /**
     * This method checks to see if the move will switch the piece
     *
     * @param theMove The Move that the player makes
     * @param cb The board to check the move on
     * @return Whether or not the piece will be switched
     */
    public boolean checkPieceSwitch(PlayerMove theMove, ChessBoard cb) {
        ChessPiece piece = cb.board[theMove.sourceX][theMove.sourceY];         if (theMove.targetY == 0 && theMove.playerColor == COLOR.BLACK && piece.type == TYPE.PAWN && cb.gamestate == COLOR.BLACK) {
            return true;
        }
        if (theMove.targetY == 7 && theMove.playerColor == COLOR.WHITE && piece.type == TYPE.PAWN && cb.gamestate == COLOR.WHITE) {
            return true;
        }
        return false;
    }

    /**
     * This method checks to see if the King is at the target coordinates
     *
     * @param theMove Move that the player makes
     * @param board Board to check move on
     * @return Whether or not king is at the target
     */
    public boolean kingAtTarget(PlayerMove theMove, ChessBoard board) {
        this.board = board;
        if (!isEmpty(theMove.targetX, theMove.targetY)) { //if target space is occupied
            if (board.board[theMove.targetX][theMove.targetY].color != theMove.playerColor) { //icheck for the color
                if (board.board[theMove.targetX][theMove.targetY].type == ChessPiece.TYPE.KING) //is it the king?
                {
                    return true;
                }
            }

        }
        return false;

    }

    /**
     * This method checks to see if the target coords are empty
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true or false if it is empty
     */
    public boolean isEmpty(int x, int y) {
        if (board.board[x][y] == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method changes the state of the board
     *
     * @param cb Board to change game state on
     * @return board
     */
    public ChessBoard changeState(ChessBoard cb) {
        cb.changeGameState(); //change gamestate
        return cb;
    }

}
