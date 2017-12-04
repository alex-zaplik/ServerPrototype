package server;

import exceptions.FullPartyException;

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
                    ConnectedUser user = null;
                    try {
                        user = new ConnectedUser(sSocket.accept());
                        party.addUser(user);
                        user.getOut().println("Added to party");
                        System.out.println("Connected with client!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FullPartyException e) {
                        user.getOut().println("Party was full");
                    }
                }
            }
        }
    };

    private void createParty() {

    }

    private void init() {
        // Wait for the first user
        ConnectedUser user = null;
        try {
            sSocket = new ServerSocket(4444);
            party = new Party(3);

            user = new ConnectedUser(sSocket.accept());
            party.addUser(user);

            user.getOut().println("Added to party");
            System.out.println("Connected with client!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FullPartyException e) {
            user.getOut().println("Party was full");
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
