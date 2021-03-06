package multiplayerchess;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import multiplayerchess.ChessPiece.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.*;
import static multiplayerchess.View.str;
import org.apache.commons.lang3.time.*;

/*
 This class implements the Clients
 */
public class Client extends JFrame implements Runnable {

    boolean gameOver;
    String name, opp;
    StopWatch s;
    String destinationIP;
    InetAddress ip;
    BufferedReader input;
    ChessBoard board;
    COLOR team;
    Scanner in;
    Socket clientSocket;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    View chessView;
    MoveValidator mV;
    ObjectInputStream objectIn;
    ObjectOutputStream objectOut;

    int mouseX, mouseY, newMouseX, newMouseY;
    public boolean firstClick = false;
    public boolean secondClick = false;
    public Color sourceColor = null;
    public Color sourceColor1 = null;
    public JButton colorHolder = null;

    public JButton[][] chessCells = new JButton[8][8];
    public JPanel chessBoard = new JPanel(new GridLayout(8, 8));
    public JPanel chessText = new JPanel(new BorderLayout());
    public JPanel mainChess = new JPanel();
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
    public Container cont;

    /**
     * Constructor creates a new player with name and socket
     *
     * @param playerName The player Name
     * @param sock Socket for client
     * @throws UnknownHostException
     */
    public Client(String playerName, Socket sock) throws UnknownHostException {
        input = new BufferedReader(new InputStreamReader(System.in));
        s = new StopWatch();
        mV = new MoveValidator();
        board = new ChessBoard();
        gameOver = false;
        chessView = new View();
        name = playerName;
        clientSocket = sock;
        chessBoard.repaint();

        cont = getContentPane();
        setBounds(0, 0, 470, 530);
        setBackground(new Color(204, 204, 204));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chess");
        setResizable(false);
        cont.setLayout(null);
        chessText.setMaximumSize(new Dimension(465, 45));
        mainChess.setBounds(0, 0, 465, 525);
        mainChess.setBackground(new Color(255, 255, 255));
        mainChess.setLayout(new BoxLayout(mainChess, BoxLayout.Y_AXIS));
        chessBoard.setMaximumSize(new Dimension(460, 460));
        cont.add(mainChess);

    }

    /**
     * This method runs the initial connections, name/color initialization, and
     * starts the players game
     */
    public void run() {
        establishConnection();
        nameAndColor();
        startPlaying();
    }

    /**
     * This method establishes a connection with the server
     */
    public void establishConnection() {
        try {
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println(name + ": outputStream created");
        } catch (IOException ex) {
            System.out.println(name);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows a player to insert their name and pick a color from a
     * dropdown list
     */
    public void nameAndColor() {
        try {
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println(name + ": input stream created.");
            Panel pan = new Panel();
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            model.addElement("White");
            model.addElement("Black");
            JComboBox comboBox = new JComboBox(model);
            pan.add(comboBox);

            String pieceColor = "";
            while (pieceColor.equals("")) {
                int result = JOptionPane.showConfirmDialog(null, pan, "Please choose your color", JOptionPane.OK_OPTION, JOptionPane.DEFAULT_OPTION);
                switch (result) {
                    case JOptionPane.OK_OPTION:
                        pieceColor = (String) comboBox.getSelectedItem();
                        break;
                }
            }

            this.drawBoard();
            PlayerMove playerInfo = new PlayerMove(name, pieceColor);
            objectOut.writeObject(playerInfo); //send desired color and name to server
            Update u = (Update) objectIn.readObject(); //should be oppenent name and assigned player color

            if (u.team.equals("WHITE")) {
                chessView.printBoard(board.board);
                this.arrangePieces();
                show();
            } else {
                chessView.printBoard(board.blackboard);
                this.arrangePieces();
                show();
            }

            team = u.team;

            opp = u.opp;
            System.out.println(name + ": I have been assigned " + team + " and will be facing " + opp);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows the player to start playing chess.
     */
    public void startPlaying() {

        try {
            while (!gameOver) {
                Update up = (Update) objectIn.readObject(); //grab the update
                if (up.gameOver) {
                    gameOver = true;
                    if (up.winner == team) {
                        JOptionPane.showMessageDialog(null, "You Win!");
                    } else {
                        JOptionPane.showMessageDialog(null, "You Lost!");
                    }
                    clientSocket.close();
                } else {

                    if (up.typeRequest) {

                        TYPE ty = changePiece();
                        PlayerMove pmv = new PlayerMove(ty);
                        objectOut.writeObject(pmv);
                    } else {
                        if (up.pM != null) {

                            board = doMove(up.pM, board);
                            if (up.changeP) {
                                if (team == COLOR.WHITE) {
                                    int tempTargetX = up.pM.targetX;
                                    int tempTargetY = up.pM.targetY;
                                    board.board[tempTargetX][tempTargetY].type = up.newType;
                                } else {
                                    int tempTargetX = 7 - up.pM.targetX;
                                    int tempTargetY = 7 - up.pM.targetY;
                                    board.blackboard[tempTargetX][tempTargetY].type = up.newType;
                                }
                            }
                            if (team == COLOR.WHITE) {
                                str = convertBoard(board.board);
                                arrangePieces();

                            } else {
                                str = convertBoard(board.blackboard);
                                arrangePieces();
                            }
                        }
                        if (up.turn == team) {
                            if (!s.isStarted()) {
                                s.start();
                            } else {
                                s.resume();
                            }
                            message.setText(team + " (" + name + ")" + ": My Turn" + "   Time taken so far: " + convertTime(s.getTime()));

                            if (team == COLOR.WHITE) {
                                chessView.printBoard(board.board);
                                str = convertBoard(board.board);
                                arrangePieces();
                            } else {
                                chessView.printBoard(board.blackboard);
                                str = convertBoard(board.blackboard);
                                arrangePieces();
                            }

                        }

                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method starts the turn of the player
     *
     * @return The player move that was just made
     * @throws IOException
     */
    public PlayerMove turn() throws IOException {

        message.setText(team + " (" + name + ") " + " TURN");

        if (team == COLOR.BLACK) {
            mouseX = 7 - mouseX;
            mouseY = 7 - mouseY;
            newMouseX = 7 - newMouseX;
            newMouseY = 7 - newMouseY;
        }
        return new PlayerMove(team, mouseX, mouseY, newMouseX, newMouseY);
    }

    /**
     * This method converts the time to minutes and seconds
     *
     * @param time Current time
     * @return The time in the time format of a string
     */
    public String convertTime(long time) {
        Date date = new Date(time);
        Format timeformat = new SimpleDateFormat("mm:ss");
        return timeformat.format(date);
    }

    /**
     * This method performs the move for the player
     *
     * @param theMove The player move object that the person has made
     * @param cb The board that needs to be changed
     * @return The updated board object
     * @throws IOException
     */
    public ChessBoard doMove(PlayerMove theMove, ChessBoard cb) throws IOException {

        if (team == COLOR.WHITE) {
            int tempSourceX = theMove.sourceX;
            int tempSourceY = theMove.sourceY;
            int tempTargetX = theMove.targetX;
            int tempTargetY = theMove.targetY;
            ChessPiece piece = cb.board[tempSourceX][tempSourceY];
            cb.board[tempTargetX][tempTargetY] = piece;
            cb.board[tempSourceX][tempSourceY] = null;
        } else {
            int tempSourceX = 7 - theMove.sourceX;
            int tempSourceY = 7 - theMove.sourceY;
            int tempTargetX = 7 - theMove.targetX;
            int tempTargetY = 7 - theMove.targetY;
            ChessPiece piece = cb.blackboard[tempSourceX][tempSourceY];
            cb.blackboard[tempTargetX][tempTargetY] = piece;
            cb.blackboard[tempSourceX][tempSourceY] = null;
        }

        return cb;
    }

    /**
     * This method shows a drop down list and allows the client to change the
     * piece if they reach the other side of the board.
     *
     * @return ChessPiece type
     */
    public ChessPiece.TYPE changePiece() throws IOException {
        Panel pan = new Panel();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("Rook");
        model.addElement("Knight");
        model.addElement("Bishop");
        model.addElement("Queen");
        JComboBox comboBox = new JComboBox(model);
        pan.add(comboBox);

        String piece = "";
        while (piece.equals("")) {
            int result = JOptionPane.showConfirmDialog(null, pan, "You made it to the other side! Which piece would like you to replace it with?", JOptionPane.OK_OPTION, JOptionPane.DEFAULT_OPTION);
            switch (result) {
                case JOptionPane.OK_OPTION:
                    piece = (String) comboBox.getSelectedItem();
                    break;
            }
        }

        switch (piece) {
            case "Rook":
                return ChessPiece.TYPE.ROOK;
            case "Knight":
                return ChessPiece.TYPE.KNIGHT;
            case "Bishop":
                return ChessPiece.TYPE.BISHOP;
            case "Queen":
                return ChessPiece.TYPE.QUEEN;
            default:
                return ChessPiece.TYPE.PAWN;
        }

    }

    /**
     * This method draws the GUI
     */
    public void drawBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                chessCells[y][x] = new JButton();
                chessBoard.add(chessCells[y][x], new BorderLayout());
                if (y % 2 == 0) {
                    if (x % 2 != 0) {
                        chessCells[y][x].setBackground(Color.DARK_GRAY);
                    } else {
                        chessCells[y][x].setBackground(Color.WHITE);
                    }
                } else if (x % 2 == 0) {
                    chessCells[y][x].setBackground(Color.DARK_GRAY);
                } else {
                    chessCells[y][x].setBackground(Color.WHITE);
                }
            }
        }

        chessText.add(message, BorderLayout.WEST);
        mainChess.add(chessText);
        mainChess.add(chessBoard);
    }

    /**
     * This method changes the pieces on the gui once the player makes a move
     */
    public void arrangePieces() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {

                chessCells[i][j].removeAll(); //remove all pieces so piece doesnt stay in same place

            }
        }

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                chessCells[i][j].repaint();
                chessCells[i][j].add(getPieceObject(str[(7 - i)][j]), BorderLayout.CENTER);
                chessCells[i][j].validate();
                if (chessCells[i][j].getActionListeners().length < 1) {
                    chessCells[i][j].addActionListener(ml); //add action listener so we can check if user presses button
                }
            }
        }
    }

    /*
     This method adds an actionListener to the chessboard
     @param e Event to listen for
     */
    ActionListener ml = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

            try {
                if (firstClick == false || secondClick == false) {
                    JButton source = null;
                    if (e.getSource() instanceof JButton) {
                        source = (JButton) e.getSource();
                    } else {
                        return;
                    }
                    int tempi = 0;
                    int tempj = 0;

                    for (int i = 0; i < chessCells.length; i++) {
                        for (int j = 0; j < chessCells[i].length; j++) {
                            if (chessCells[i][j] == source) {
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
                        firstClick = true;
                    } else if (secondClick == false) {
                        colorHolder.setBackground(sourceColor);
                        sourceColor1 = source.getBackground();
                        source.setBackground(Color.blue);
                        newMouseX = tempj;
                        newMouseY = 7 - tempi;
                        secondClick = true;
                    }

                    if (firstClick == true && secondClick == true) {
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
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

    /**
     * This method turns the string into a JLabel object
     *
     * @param strPieceName piece to turn into object
     * @return JLabel object with specific piece
     */
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

    /**
     * This method adds all the strings to the regular chessboard
     *
     * @param chessboard Board to convert to strings
     * @return Return 2d array of strings for the chess chessboard
     */
    public String[][] convertBoard(ChessPiece[][] chessboard) {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                String stringPiece = "";
                ChessPiece p;
                if (chessboard[j][i] == null) {
                    stringPiece = "-";
                } else {
                    p = chessboard[j][i];
                    switch (p.color) {
                        case WHITE:
                            stringPiece += "w";
                            break;
                        case BLACK:
                            stringPiece += "b";
                            break;
                    }
                    switch (p.type) {
                        case ROOK:
                            stringPiece += "R";
                            break;
                        case KNIGHT:
                            stringPiece += "N";
                            break;
                        case BISHOP:
                            stringPiece += "B";
                            break;
                        case QUEEN:
                            stringPiece += "Q";
                            break;
                        case KING:
                            stringPiece += "K";
                            break;
                        case PAWN:
                            stringPiece += "P";
                            break;
                    }
                }
                str[i][j] = stringPiece;

            }
        }
        return str;
    }

}
