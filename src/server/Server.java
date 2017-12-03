package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private ServerSocket sSocket;
    private Party party;

    private Runnable emptySocket = new Runnable() {
        @Override
        public void run() {
            if (sSocket != null && party != null) {
                while (true) {
                    try {
                        ConnectedUser user = new ConnectedUser(sSocket.accept());
                        party.addUser(user);
                        System.out.println("Connected with client!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void init() {
        // Wait for the first user
        try {
            sSocket = new ServerSocket(4444);
            party = new Party(3);

            ConnectedUser user = new ConnectedUser(sSocket.accept());
            party.addUser(user);

            System.out.println("Connected with client!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wait for more users
        new Thread(emptySocket).start();
    }

    public static void serverMain(String[] args) {
        System.out.println("Starting the server...");

        Server server = new Server();
        server.init();
    }
}
