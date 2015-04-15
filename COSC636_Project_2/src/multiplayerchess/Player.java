package multiplayerchess;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import multiplayerchess.Piece.*;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import javax.swing.*;

import static multiplayerchess.View.str;

import org.apache.commons.lang3.time.*;

public class Player extends JFrame implements Runnable {

    ChessBoard board;
    COLOR team;
    Scanner in;
    Socket clientSocket;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    View v;
    MoveValidator mV;
    ObjectInputStream objectIn;
    ObjectOutputStream objectOut;
    boolean gameOver;
    String name, opp;
    StopWatch s;
    String destinationIP;
    InetAddress ip;
    BufferedReader input;
    int mouseX, mouseY, newMouseX, newMouseY;
    public boolean firstClick = false;
    public boolean secondClick = false;
    public Color sourceColor = null;
    public Color sourceColor1 = null;
    public JButton colorHolder = null;
    public int l = 0;

    public JButton[][] pnlCells = new JButton[8][8];
    public JPanel pnlBoard = new JPanel(new GridLayout(8, 8));
    public JPanel pnlText = new JPanel(new BorderLayout());
    public JPanel pnlMain = new JPanel();
    public ImageIcon rookBlack = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/B_Rook.png");
    public ImageIcon rookWhite = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/W_Rook.png");
    public ImageIcon pawnBlack = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/B_Pawn.png");
    public ImageIcon pawnWhite = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/W_Pawn.png");
    public ImageIcon bishopBlack = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/B_Bishop.png");
    public ImageIcon bishopWhite = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/W_Bishop.png");
    public ImageIcon knightBlack = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/B_Knight.png");
    public ImageIcon knightWhite = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/W_Knight.png");
    public ImageIcon queenBlack = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/B_Queen.png");
    public ImageIcon queenWhite = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/W_Queen.png");
    public ImageIcon kingBlack = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/B_King.png");
    public ImageIcon kingWhite = new ImageIcon(System.getProperty("user.dir") + "/build/classes/images/W_King.png");
    public JLabel message = new JLabel();
    public Container c;
    public boolean boolMoveSelection = false;
    public Point pntMoveFrom, pntMoveTo;

    public Player(String nm, Socket sock) throws UnknownHostException {
        input = new BufferedReader(new InputStreamReader(System.in));
        s = new StopWatch();
        mV = new MoveValidator();
        board = new ChessBoard();
        gameOver = false;
        v = new View();
        name = nm;
        clientSocket = sock;
        pnlBoard.repaint();

        c = getContentPane();
        setBounds(0, 0, 470, 530);
        setBackground(new Color(204, 204, 204));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chess");
        setResizable(false);
        c.setLayout(null);
        //pnlText.setBackground(Color.WHITE);
        pnlText.setMaximumSize(new Dimension(465, 45));
        pnlMain.setBounds(0, 0, 465, 525);
        pnlMain.setBackground(new Color(255, 255, 255));
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlBoard.setMaximumSize(new Dimension(460, 460));
        c.add(pnlMain);

    }

    public void run() {
        establishConnection();
        nameAndColor();
        startPlaying();
    }

    public void establishConnection() {
        try {
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println(name + ": outputStream created");
        } catch (IOException ex) {
            System.out.println(name);
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nameAndColor() {
        try {
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println(name + ": input stream created.");
            Panel p = new Panel();
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            model.addElement("White");
            model.addElement("Black");
            JComboBox comboBox = new JComboBox(model);
            p.add(comboBox);

            String dColor = "";
            while (dColor.equals("")){
                int result = JOptionPane.showConfirmDialog(null, p, "Please choose your color", JOptionPane.OK_OPTION, JOptionPane.DEFAULT_OPTION);
                switch (result) {
                    case JOptionPane.OK_OPTION:
                        dColor = (String) comboBox.getSelectedItem();
                        break;
                }
            }

            this.drawBoard();
            //String dColor = JOptionPane.showInputDialog(null, name + ": Please enter your desired color", "");
            //System.out.println(name + ": Please enter your desired color");
            //System.out.println("Entered " + dColor);
            //JOptionPane.showMessageDialog(null, dColor);
            PlayerMove info = new PlayerMove(name, dColor);
            objectOut.writeObject(info); //send desired color and name to server
            Update u = (Update) objectIn.readObject(); //should be oppenent name and assigned player color

            if (u.team.equals("WHITE")) {
                v.printBoard(board.board);
                this.arrangePieces();
                show();
            } else {
                v.printBoard(board.blackboard);
                this.arrangePieces();
                show();
            }
            
            team = u.team;

            opp = u.opp;
            System.out.println(name + ": I have been assigned " + team + " and will be facing " + opp);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startPlaying() {

        try {
            while (!gameOver) {
                Update u = (Update) objectIn.readObject(); //grab the update
                if (u.gameOver) {
                    gameOver = true;
                    if (u.winner == team) {
                        JOptionPane.showMessageDialog(null, "You Win!");
                    } else {
                        JOptionPane.showMessageDialog(null, "You Lost!");
                    }
                    clientSocket.close();
                } else {

                    if (u.typeRequest) {

                        TYPE ty = changePiece();
                        PlayerMove pmv = new PlayerMove(ty);
                        objectOut.writeObject(pmv);
                    } else {
                        if (u.pM != null) { //either first move or server didn't like last move submitted

                            board = doMove(u.pM, board);
                            if (u.changeP) {
                                if (team == COLOR.WHITE) {
                                    int tempTargetX = u.pM.targetX;
                                    int tempTargetY = u.pM.targetY;
                                    board.board[tempTargetX][tempTargetY].type = u.newType;
                                } else {
                                    int tempTargetX = 7 - u.pM.targetX;
                                    int tempTargetY = 7 - u.pM.targetY;
                                    board.blackboard[tempTargetX][tempTargetY].type = u.newType;
                                }
                            }
                            if (team == COLOR.WHITE) {
                                str = convertBoard(board.board);/////reset strings
                                arrangePieces();//pring board

                            } else {
                                str = convertBoard(board.blackboard);/////reset strings
                                arrangePieces();//pring board
                            }
                        }
                        if (u.turn == team) {
                            if (!s.isStarted()) {
                                s.start();
                            } else {
                                s.resume();
                            }
                            message.setText(team + " (" + name + ")" + ": My Turn" + "   Time taken so far: " + convertTime(s.getTime()));
                            System.out.println("Time taken so far: " + convertTime(s.getTime()));

                            if (team == COLOR.WHITE) {
                                v.printBoard(board.board);
                                str = convertBoard(board.board);/////reset strings
                                arrangePieces();//pring board
                            } else {
                                v.printBoard(board.blackboard);
                                str = convertBoard(board.blackboard);/////reset strings
                                arrangePieces();//pring board
                            }

                            //PlayerMove pM = turn(); //send turn to server
                            //objectOut.writeObject(pM); //send turn to server
                            //System.out.println(name + ": sent move to server");
                            //s.suspend();
                        } else {

                        }

                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlayerMove turn() throws IOException {

    	message.setText(team + " (" + name + ") " + " TURN");
        //System.out.println(" Enter source position X,Y: ");
        //String source = JOptionPane.showInputDialog(null, "Enter source position X,Y:", "");
        //source = input.readLine();
        //System.out.println("source: " +source);
        //int sourceX = Integer.parseInt("" + source.charAt(0));
        //int sourceY = Integer.parseInt("" + source.charAt(2));
        //System.out.print(team + " TURN ");
        //System.out.println("Enter target position X,Y: ");
        //String target = JOptionPane.showInputDialog(null, "Enter target position X,Y:", "");
        //int targetX = Integer.parseInt("" + target.charAt(0));
        //int targetY = Integer.parseInt("" + target.charAt(2));
        if (team == COLOR.BLACK) {
            mouseX = 7 - mouseX;
            mouseY = 7 - mouseY;
            newMouseX = 7 - newMouseX;
            newMouseY = 7 - newMouseY;
        }
        return new PlayerMove(team, mouseX, mouseY, newMouseX, newMouseY);
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("mm:ss");
        return format.format(date).toString();
    }

    public ChessBoard doMove(PlayerMove pM, ChessBoard b) throws IOException {

        if (team == COLOR.WHITE) {
            int tempSourceX = pM.sourceX;
            int tempSourceY = pM.sourceY;
            int tempTargetX = pM.targetX;
            int tempTargetY = pM.targetY;
            Piece piece = b.board[tempSourceX][tempSourceY]; //get piece
            b.board[tempTargetX][tempTargetY] = piece; //move the piece
            b.board[tempSourceX][tempSourceY] = null;// set original space to null
        } else {
            int tempSourceX = 7 - pM.sourceX;
            int tempSourceY = 7 - pM.sourceY;
            int tempTargetX = 7 - pM.targetX;
            int tempTargetY = 7 - pM.targetY;
            Piece piece = b.blackboard[tempSourceX][tempSourceY]; //get piece
            b.blackboard[tempTargetX][tempTargetY] = piece; //move the piece
            b.blackboard[tempSourceX][tempSourceY] = null;// set original space to null
        }

        return b;
    }

    public Piece.TYPE changePiece() throws IOException {
        System.out.println("You made it to the other side! Which piece would like you to replace it with?");
        System.out.println("1: Rook");
        System.out.println("2: Knight");
        System.out.println("3: Bishop");
        System.out.println("4: Queen");
        int choice = Integer.parseInt(input.readLine());
        switch (choice) {
            case 1:
                return Piece.TYPE.ROOK;
            case 2:
                return Piece.TYPE.KNIGHT;
            case 3:
                return Piece.TYPE.BISHOP;
            case 4:
                return Piece.TYPE.QUEEN;
            default:
                return Piece.TYPE.PAWN;
        }

    }

    public void drawBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                pnlCells[y][x] = new JButton();
                pnlBoard.add(pnlCells[y][x], new BorderLayout());
                if (y % 2 == 0) {
                    if (x % 2 != 0) {
                        pnlCells[y][x].setBackground(Color.DARK_GRAY);
                    } else {
                        pnlCells[y][x].setBackground(Color.WHITE);
                    }
                } else if (x % 2 == 0) {
                    pnlCells[y][x].setBackground(Color.DARK_GRAY);
                } else {
                    pnlCells[y][x].setBackground(Color.WHITE);
                }
            }
        }
        
        pnlText.add(message, BorderLayout.WEST);
        pnlMain.add(pnlText);
        pnlMain.add(pnlBoard);
    }

    public void arrangePieces() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {

                pnlCells[i][j].removeAll(); //remove all pieces so piece doesnt stay in same place

                //System.out.println(j+", "+(i)+":"+v.str[(7-i)][j]);
            }
        }

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                final int tempi = i;
                final int tempj = j;
                pnlCells[i][j].repaint();
                pnlCells[i][j].add(getPieceObject(str[(7 - i)][j]), BorderLayout.CENTER);
                pnlCells[i][j].validate();
                if (pnlCells[i][j].getActionListeners().length < 1) {
                    pnlCells[i][j].addActionListener(ml); //add action listener so we can check if user presses button
                }
            }
        }
    }

    ActionListener ml = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

            try {
                System.out.println("Count of listeners: " + ((JButton) e.getSource()).getActionListeners().length);
                if (firstClick == false || secondClick == false) {
                    JButton source = null;
                    if (e.getSource() instanceof JButton) {
                        source = (JButton) e.getSource();
                    } else {
                        return;
                    }
                    int tempi = 0;
                    int tempj = 0;

                    for (int i = 0; i < pnlCells.length; i++) {
                        for (int j = 0; j < pnlCells[i].length; j++) {
                            if (pnlCells[i][j] == source) {
                                tempi = i;
                                tempj = j;
                                break;
                            }
                        }
                    }
                    if (firstClick == false) {
                        colorHolder = source;
                        sourceColor = source.getBackground();

                        source.setBackground(Color.red);
                        mouseX = tempj;
                        mouseY = 7 - tempi;
                        //System.out.println("First You pressed" + mouseX + ", " + mouseY);
                        firstClick = true;
                    } else if (secondClick == false) {
                        colorHolder.setBackground(sourceColor);
                        sourceColor1 = source.getBackground();
                        source.setBackground(Color.blue);
                        newMouseX = tempj;
                        newMouseY = 7 - tempi;
                        //System.out.println("Second You pressed" + newMouseX + ", " + newMouseY);
                        secondClick = true;
                    }

                    if (firstClick == true && secondClick == true) {
                        //Thread.sleep(500);
                        source.setBackground(sourceColor1);
                        firstClick = false;
                        secondClick = false;
                        PlayerMove pM = turn(); //send turn to server
                        objectOut.writeObject(pM); //send turn to server
                        System.out.println(name + ": sent move to server");
                        s.suspend();
                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

    public JLabel getPieceObject(String strPieceName) {
        JLabel lblTemp;
        if (strPieceName.equals("bR")) {
            lblTemp = new JLabel(this.rookBlack);
        } else if (strPieceName.equals("bB")) {
            lblTemp = new JLabel(this.bishopBlack);
        } else if (strPieceName.equals("bN")) {
            lblTemp = new JLabel(this.knightBlack);
        } else if (strPieceName.equals("bQ")) {
            lblTemp = new JLabel(this.queenBlack);
        } else if (strPieceName.equals("bK")) {
            lblTemp = new JLabel(this.kingBlack);
        } else if (strPieceName.equals("bP")) {
            lblTemp = new JLabel(this.pawnBlack);
        } else if (strPieceName.equals("wR")) {
            lblTemp = new JLabel(this.rookWhite);
        } else if (strPieceName.equals("wB")) {
            lblTemp = new JLabel(this.bishopWhite);
        } else if (strPieceName.equals("wN")) {
            lblTemp = new JLabel(this.knightWhite);
        } else if (strPieceName.equals("wQ")) {
            lblTemp = new JLabel(this.queenWhite);
        } else if (strPieceName.equals("wK")) {
            lblTemp = new JLabel(this.kingWhite);
        } else if (strPieceName.equals("wP")) {
            lblTemp = new JLabel(this.pawnWhite);
        } else {
            lblTemp = new JLabel();
        }
        return lblTemp;
    }

    public String[][] convertBoard(Piece[][] board) {
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
                //System.out.print(s + "\t");
            }
            //System.out.println();
        }
        return str;
    }

}
