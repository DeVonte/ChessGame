package multiplayerchess;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This class starts up the server and creates the game
 * 
 * @date 4/16/2015
 */
public class Server implements Runnable {

    protected int serverPort, gamePort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    public int playerCount, gamecount;
    private ArrayList<UserThread> users;

    public Server(int port) {
        this.serverPort = port;
        this.gamePort = this.serverPort + 1;
        this.playerCount = this.gamecount = 0;
        users = new ArrayList<UserThread>();
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                synchronized (this) {
                    UserThread t = new UserThread(clientSocket);
                    t.start();
                    users.add(t);
                    System.out.println("SERVER: Player added to queue.");
                }
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }

        }
        System.out.println("Server Stopped.");
    }

    /**
     * This code checks to see if the program has stopped
     * @return if the program is stopped 
     */
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Close the port and stop the server
     */
    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     * This method opens the server port
     */
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
        System.out.println("SERVER: Socket opened on " + serverPort);
    }

    /**
     * This method runs the server
     * @param args
     * @throws IOException 
     */
    public static void main(String args[]) throws IOException {
        new Server(8080).run();
    }

    /**
     * This class implements the user threads
     */
    private class UserThread extends Thread {

        ObjectInputStream objectIn;
        ObjectOutputStream objectOut;
        private String name;
        Socket socket;

        /**
         * Constructor to create UserThread
         * @param newsock Socket to put user thread on
         * @throws IOException 
         */
        public UserThread(Socket newsock) throws IOException {
            socket = newsock;
            objectOut = new ObjectOutputStream(newsock.getOutputStream());
            objectIn = new ObjectInputStream(newsock.getInputStream());
            name = "";
        }

        /**
        * This method gets and returns the user name
        * @return name to return
        */
        public synchronized String getUserName() {
            return name;
        }

        /**
        * This method sets the user name
        * @user Name to set
        */
        public synchronized void setUserName(String user) {
            name = user;
        }

        /**
        * This method gets the object input stream
        * @return object input stream
        */
        public synchronized ObjectInputStream getObjectInputStream() {
            return this.objectIn;
        }

        /**
        * This method gets the object output stream
        * @return object output stream
        */
        public synchronized ObjectOutputStream getObjectOutputStream() {
            return this.objectOut;
        }

        /**
        * This method gets the socket
        * @return The socket
        */
        public synchronized Socket getSocket() {
            return this.socket;
        }

        /**
         * This class runs the server
         */
        public void run() {
            boolean alive = true;
            while (alive) {
                try {
                    PlayerMove move;
                    while ((move = (PlayerMove) objectIn.readObject()) != null) {
                        if (move.flag == '1') {
                            if ((move.name.trim().equals("InvalidUser"))
                                    || (move.name.trim().equals(""))) {
                                PlayerMove response = new PlayerMove('2', "InvalidUser");
                                objectOut.writeObject(response);
                            } else {
                                boolean valid = validateNewUser(move.name.trim());
                                if (!valid) {
                                    PlayerMove response = new PlayerMove('2', "InvalidUser");
                                    objectOut.writeObject(response);
                                } else {
                                    PlayerMove response = new PlayerMove('2', "Success");
                                    objectOut.writeObject(response);
                                    this.setUserName(move.name.trim());

                                    for (int i = 0; i < users.size(); ++i) {
                                        if (!(users.get(i).getUserName().equals(move.name.trim()))) {
                                            try {
                                                PlayerMove list = new PlayerMove('3', users.get(i).getUserName());
                                                objectOut.writeObject(list);
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    newUser(move.name.trim());
                                }
                            }
                        } else if (move.flag == '4') {
                            for (int i = 0; i < users.size(); ++i) {
                                synchronized (this) {
                                    try {
                                        PlayerMove msg = new PlayerMove('4', this.name, move.message);
                                        users.get(i).getObjectOutputStream().writeObject(msg);
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else if (move.flag == '6') {
                            for (int i = 0; i < users.size(); ++i) {
                                if (users.get(i).getUserName().toUpperCase().equals(move.name.toUpperCase())) {
                                    synchronized (this) {
                                        try {
                                            PlayerMove msg = new PlayerMove('6', this.name);
                                            users.get(i).getObjectOutputStream().writeObject(msg);
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                    i = users.size() + 1;
                                }
                            }
                        } else if (move.flag == '7') {
                            for (int i = 0; i < users.size(); ++i) {
                                if (users.get(i).getUserName().toUpperCase().equals(move.name.toUpperCase())) {
                                    synchronized (this) {
                                        try {
                                            if (move.message.equals("ACCEPTED")) {
                                                ServerSocket temp_sock = new ServerSocket(gamePort);

                                                PlayerMove msg = new PlayerMove('8', Integer.toString(gamePort));
                                                this.getObjectOutputStream().writeObject(msg);

                                                msg = new PlayerMove('8', Integer.toString(gamePort));
                                                users.get(i).getObjectOutputStream().writeObject(msg);

                                                ++gamePort;

                                                createGame(temp_sock, this.name, users.get(i).getUserName());

                                                /*  
                                                 * I need to get this to work  
                                                 */
                                            } else {
                                                PlayerMove msg = new PlayerMove('7', this.name, move.message);
                                                users.get(i).getObjectOutputStream().writeObject(msg);
                                            }
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                    i = users.size() + 1;
                                }
                            }
                        } else {
                            System.out.println("I got something I couldn't process!!!");
                        }
                    }
                } catch (IOException e) {
                    System.out.println(this.name + " has left");
                    alive = false;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            removeClient(this.name);
        }
    }

    /**
     * This method creates the game based on the two users that have accepted.
     * @param temp_sock Socket to start game on
     * @param p1 First player
     * @param p2 Second player
     */
    public synchronized void createGame(ServerSocket temp_sock, String p1, String p2) {
        UserThread player1 = null, player2 = null;
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getUserName().toUpperCase().equals(p1.toUpperCase())) {
                player1 = users.get(i);
            } else if (users.get(i).getUserName().toUpperCase().equals(p2.toUpperCase())) {
                player2 = users.get(i);
            }
        }

        try {
            if ((player1 != null)
                    && (player2 != null)) {

                Socket p1_new = temp_sock.accept();
                Socket p2_new = temp_sock.accept();

                System.out.println("I got here");
                new Thread(new ServerHelper(p1_new, p2_new, gamecount)).start();
                gamecount++;

                for (int i = (users.size() - 1); i > -1; --i) {
                    if (users.get(i).getUserName().toUpperCase().equals(p1.toUpperCase())) {
                        users.get(i).interrupt();
                        users.remove(i);
                    } else if (users.get(i).getUserName().toUpperCase().equals(p2.toUpperCase())) {
                        users.get(i).interrupt();
                        users.remove(i);
                    }
                }

                for (int i = 0; i < users.size(); ++i) {
                    synchronized (this) {
                        try {
                            PlayerMove msg = new PlayerMove('5', p1);
                            users.get(i).getObjectOutputStream().writeObject(msg);
                            msg = new PlayerMove('5', p2);
                            users.get(i).getObjectOutputStream().writeObject(msg);
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        playerCount = 0;
    }

    /**
     * Client gets removed from the array after it is disconnected.
     *
     * @param removeid ID of the user that disconnected
     */
    public synchronized void removeClient(String name) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getUserName().toUpperCase().equals(name.toUpperCase())) {
                System.out.println("Removing " + name);
                users.remove(i);
                i = users.size() + 1;
            }
        }

        for (int i = 0; i < users.size(); ++i) {
            synchronized (this) {
                try {
                    PlayerMove msg = new PlayerMove('5', name);
                    users.get(i).getObjectOutputStream().writeObject(msg);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method validates the new user
     * @param name User to validate
     * @return Boolean if user is validated
     */
    public synchronized boolean validateNewUser(String name) {
        boolean valid = true;
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getUserName().toUpperCase().equals(name.toUpperCase())) {
                valid = false;
                i = users.size() + 1;
            }
        }
        return valid;
    }

    /**
     * Notifies users connected that a new user has connected to the server.
     *
     * @param name Name of the user connected.
     */
    public synchronized void newUser(String name) {
        for (int i = 0; i < users.size(); ++i) {
            synchronized (this) {
                try {
                    PlayerMove newUser = new PlayerMove('3', name);
                    users.get(i).getObjectOutputStream().writeObject(newUser);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
