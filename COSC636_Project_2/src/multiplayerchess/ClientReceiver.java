package multiplayerchess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * This class manages the main room for the clients
 *
 * @date 4/16/2015
 */
public class ClientReceiver implements Runnable {

    private Socket socket;
    private ObjectInputStream receive;

    /**
     * Constructor that creates client receiver with socket and input stream
     *
     * @param s Socket
     * @param in InputStream
     */
    public ClientReceiver(Socket s, ObjectInputStream in) {
        socket = s;
        receive = in;
    }

    /**
     * This method runs the management for the main room.
     */
    public void run() {
        // TODO Auto-generated method stub
        try {
            PlayerMove input;
            while ((input = (PlayerMove) receive.readObject()) != null) {
                char flag = input.flag;
                switch (flag) {
                    case '4': // New Message
                        MainRoom.addMessage(input.name, input.message);
                        break;
                    case '3': // New User
                        MainRoom.addUser(input.name);
                        break;
                    case '5': // User Left
                        MainRoom.removeUser(input.name);
                        break;
                    case '6': // You have been challenged
                        MainRoom.receivedChallenge(input.name);
                        break;
                    case '7': // Your challenge was responded to
                        MainRoom.showRequestResponse(input.name, input.message);
                        break;
                    case '8': // Start game;
                        int port = Integer.parseInt(input.name);
                        MainRoom.startGame(port);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }

    }
}
