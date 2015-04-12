package multiplayerchess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientReceiver implements Runnable {

    private Socket socket;
    private ObjectInputStream receive;

    public ClientReceiver(Socket s, ObjectInputStream in) {
        socket = s;
        receive = in;
    }

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
//				System.out.println("From Server: " + msgRecieved);
//				System.out.println("Please enter something to send to server..");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
