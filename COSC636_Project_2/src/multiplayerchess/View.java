/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayerchess;

import java.io.Serializable;

/*
 This class implements the regular board for the output window
 */
public class View implements Serializable {

    public static String[][] str = new String[8][8];

    public View() {

    }

    /*
     This method prints the board to the output window
     @param board The board to print
     */
    public void printBoard(Piece[][] board) {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                String s = "";
                Piece p;
                if (board[j][i] == null) {
                    s = "-";
                } else {
                    p = board[j][i];
                    switch (p.color) {
                        case WHITE:
                            s += "w";
                            break;
                        case BLACK:
                            s += "b";
                            break;
                    }
                    switch (p.type) {
                        case ROOK:
                            s += "R";
                            break;
                        case KNIGHT:
                            s += "N";
                            break;
                        case BISHOP:
                            s += "B";
                            break;
                        case QUEEN:
                            s += "Q";
                            break;
                        case KING:
                            s += "K";
                            break;
                        case PAWN:
                            s += "P";
                            break;
                    }
                }
                str[i][j] = s;
                System.out.print(s + "\t");
            }
            System.out.println();
        }
    }

}
