package multiplayerchess;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This class implements the main room where all users connect and chat
 *
 * @date 4/16/2015
 */
@SuppressWarnings("serial")
public class MainRoom extends JFrame implements ActionListener {

    private static DefaultListModel<String> messages, users;
    private static Socket socket;
    private static ObjectOutputStream objectOut;
    private static ObjectInputStream objectIn;
    private static String username;
    private static JTextArea textArea;
    private static JLabel login;
    private static JButton challenge, send;
    private static JList<String> list;
    private static MainRoom frame;

    /**
     * Constructor that creates new Main Room
     */
    public MainRoom() {
        setSize(new Dimension(780, 525));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("COSC636 Project 2");

        setScreen();
        setLocationRelativeTo(null);
    }

    /**
     * Main method that starts up main room with the frame
     *
     * @param args
     */
    public static void main(String[] args) {
        frame = new MainRoom();
        frame.setVisible(true);
    }

    /**
     * This method sets up the screen for the main room
     */
    public void setScreen() {
        getContentPane().removeAll();

        JPanel container = new JPanel();
        container.setLayout(new FlowLayout(FlowLayout.CENTER));
        container.setAlignmentY(TOP_ALIGNMENT);

        /*  Left side  */
        JPanel panel1 = new JPanel();
        login = new JLabel("Logged in as: ");
        panel1.add(login);

        panel1.add(Box.createRigidArea(new Dimension(0, 7)));

        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel1.setMaximumSize(new Dimension(325, 0));
        panel1.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Available Players");
        panel1.add(label);

        users = new DefaultListModel<String>();
        list = new JList<String>(users);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0) {
                // TODO Auto-generated method stub
                challenge.setEnabled(true);
            }
        });

        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 370));
        panel1.add(listScroller);

        panel1.add(Box.createRigidArea(new Dimension(0, 7)));

        challenge = new JButton("Challenge");
        challenge.addActionListener(this);
        panel1.add(challenge);
        challenge.setEnabled(false);
        container.add(panel1);

        /*  Right side  */
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel2.setMaximumSize(new Dimension(405, 0));
        panel2.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label2 = new JLabel("Message Board");
        panel2.add(label2);

        messages = new DefaultListModel<String>();
        JList<String> list2 = new JList<String>(messages);

        list2.setLayoutOrientation(JList.VERTICAL_WRAP);
        list2.setVisibleRowCount(-1);

        JScrollPane listScroller2 = new JScrollPane(list2);
        listScroller2.setPreferredSize(new Dimension(400, 300));
        panel2.add(listScroller2);

        panel2.add(Box.createRigidArea(new Dimension(0, 7)));

        textArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(true);
        textArea.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void keyReleased(KeyEvent arg0) {
                // TODO Auto-generated method stub
                if (textArea.getText().length() > 0) {
                    send.setEnabled(true);
                } else {
                    send.setEnabled(false);
                }

            }

            public void keyTyped(KeyEvent arg0) {
                // TODO Auto-generated method stub

            }
        });
        panel2.add(scrollPane);

        panel2.add(Box.createRigidArea(new Dimension(0, 7)));

        send = new JButton("Send");
        send.addActionListener(this);
        send.setEnabled(false);
        send.setAlignmentX(RIGHT_ALIGNMENT);
        panel2.add(send);
        container.add(panel2);

        add(container);

        /*  Connect the client to the server  */
        ClientReceiver receiver;

        try {
            InetAddress ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, 8080);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
            receiver = new ClientReceiver(socket, objectIn);

            boolean valid = false;
            String additional = "";
            while (!valid) {
                String name = JOptionPane.showInputDialog(null, "Enter a username" + additional, "");

                PlayerMove info = new PlayerMove('1', name);
                objectOut.writeObject(info); //send desired color and name to server
                Thread.sleep(100);

                PlayerMove response = (PlayerMove) objectIn.readObject();
                if (response.name.equals("InvalidUser")) {
                    additional = ": Your name was either invalid or in use";
                } else {
                    valid = true;
                    username = name;
                    login.setText("Logged in as: " + username);
                }
            }
            Thread t = new Thread(receiver);
            t.start();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This method adds the users message to the chat
     *
     * @param name Name of user
     * @param message Message
     */
    public static void addMessage(String name, String message) {
        messages.addElement((name + " >>> " + message));
    }

    /**
     * This method adds a user to the chat room
     *
     * @param user User to add
     */
    public static void addUser(String user) {
        users.addElement(user);
    }

    /**
     * This method removes the user from the chat room
     *
     * @param user User to remove
     */
    public static void removeUser(String user) {
        int pos = users.indexOf(user);
        users.removeElementAt(pos);
    }

    /**
     * This method handles the challenge that the user receives
     *
     * @param user User that sends challenge
     * @throws IOException
     */
    public static void receivedChallenge(String user) throws IOException {
        String msg = user + " has challenged you to a game.  Do you accept?";
        int res = JOptionPane.showConfirmDialog(null, msg, "Challenge", JOptionPane.YES_NO_OPTION);

        if (res == 0) {
            PlayerMove response = new PlayerMove('7', user, "ACCEPTED");
            objectOut.writeObject(response);
        } else {
            PlayerMove response = new PlayerMove('7', user, "REJECTED");
            objectOut.writeObject(response);
        }
    }

    /**
     * This method starts the chess game on the client side
     *
     * @param port Port to connect with
     * @throws IOException
     */
    public static void startGame(int port) throws IOException {
        Socket s = new Socket(socket.getInetAddress().getHostAddress(), port);
        Client p1 = new Client(username, s);
        new Thread(p1).start();
        frame.dispose();
    }

    /**
     * This method handles the response that the user sends from the challenge
     *
     * @param user User that responses
     * @param result The response
     * @throws UnknownHostException
     */
    public static void showRequestResponse(String user, String result) throws UnknownHostException {
        if (result.equals("REJECTED")) {
            String msg = user + " has rejected your challenge";
            JOptionPane.showMessageDialog(null, msg);
        }
    }

    /**
     * This method listens to what the user is doing in the chat room
     *
     * @param act Event to listen to
     */
    public void actionPerformed(ActionEvent act) {
        JButton b = (JButton) act.getSource();
        try {
            if (b.getText().equals("Send")) {
                PlayerMove message = new PlayerMove('4', username, textArea.getText());
                objectOut.writeObject(message);
                textArea.setText("");
                send.setEnabled(false);
            } else if (b.getText().equals("Challenge")) {
                String chall_user = list.getSelectedValue();
                if (chall_user.toUpperCase().equals(username.toUpperCase())) {
                    JOptionPane.showMessageDialog(null, "You cannot challenge yourself!");
                } else {
                    PlayerMove game_request = new PlayerMove('6', chall_user);
                    objectOut.writeObject(game_request);
                    challenge.setEnabled(false);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
