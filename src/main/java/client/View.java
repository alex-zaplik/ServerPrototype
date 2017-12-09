package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Simple view that uses the console from communicating with users
 *
 * @author Aleksander Lasecki
 */
public class View implements Runnable {

    /**
     * Buffered reader used from getting input from the console
     */
    private BufferedReader scan;

    /**
     * Set of basic IO messages
     */
    private String[] messages = {
            "Starting the client...",                   // 0
            "Enter the server's IP address: ",          // 1
            "Enter the server's port: ",                // 2
            "Connecting to the server...",              // 3
            "Connected to the server!",                 // 4
            "Connection failed",                        // 5
            "Invalid input given"                       // 6
    };

    /**
     * Class construction that initialized the BufferedReader
     */
    View() {
        scan = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * A method that gets a correctly formatted port number from the user
     *
     * @return              The port number
     * @throws IOException  Exception thrown by the BufferedReader
     */
    private int getPortFromUser() throws IOException {
        int port = -1;
        while (port == -1) {
            try {
                System.out.print(messages[2]);
                port = Integer.parseInt(scan.readLine());

                if (port < 0 || port > 1 << 16) {
                    port = -1;
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println(messages[6]);
                scan.readLine();
            }
        }

        return port;
    }

    /**
     * Creates a party based on user input
     *
     * @param command       Line entered by the user
     * @throws IOException  Thrown if the input was incorrect
     */
    private void createParty(String command) throws IOException {
        String[] settings = command.split(" ");

        // TODO: Handle wrong input
        String name = settings[1];
        int max = Integer.parseInt(settings[2]);

        // TODO: Change that so that the user can try again
        if (max < 1)
            throw new IOException();

        Client.getInstance().sendPartySettings(name, max);
    }

    /**
     * Join a party
     *
     * @param name  The name of the party we want to join
     */
    private void joinParty(String name) {
        // TODO: Handle wrong input
        Client.getInstance().joinParty(name);
    }

    /**
     * Simply prints the input on the screen
     *
     * @param input The input
     */
    void handleInput(String input) {
        System.out.print(input + "\n> ");
    }

    /**
     * The main loop of the view that uses calls methods based on user input
     */
    private void mainLoop() {
        boolean running = true;
        while (running) {
            String output;

            try {
                System.out.print("> ");
                output = scan.readLine();

                if (output.equals("exit"))
                    running = false;
                else
                    Client.getInstance().sendMessage(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connects with a server based on user input
     *
     * @throws IOException  Thrown by the BufferedReader
     */
    private void handleConnecting() throws IOException {
        System.out.print(messages[1]);
        String address = scan.readLine();

        int port = getPortFromUser();

        System.out.println(messages[3]);

        Client.getInstance().initConnection(address, port);

        System.out.println(messages[4]);
    }

    /**
     * Displays the list of available parties in the console
     *
     * @throws IOException  Thrown by the BufferedReader
     */
    private void displayPartyInfo() throws IOException {
        List<Party> partyList = Client.getInstance().getParties();
        if (partyList.size() > 0 && partyList.get(0).max > 0)
            for (Party p : partyList)
                System.out.println(p.name + " [" + p.left + "/" + p.max + "]");
        else if (partyList.get(0).max == -1)
            System.out.println(partyList.get(0).name);
    }

    /**
     * Handles choosing a party based on user input (creating and joining)
     *
     * @throws IOException  Thrown by the BufferedReader
     */
    private void handlePartyChoosing() throws IOException {
        System.out.print("> ");
        String command = scan.readLine();
        if (command.charAt(0) == 'c') {
            // Creating a new party. command = c <name> <max>
            createParty(command);
        } else {
            // Joining party named 'command'
            joinParty(command);
        }
    }

    /**
     * Initializes the connection and runs the main loop
     */
    @Override
    public void run() {
        System.out.println(messages[0]);

        while (!Client.getInstance().isConnected()) {
            try {
                handleConnecting();
                displayPartyInfo();
                handlePartyChoosing();
            } catch (IOException e) {
                e.printStackTrace();

                System.out.println(messages[5]);
            }
        }

        Client.getInstance().startListening();
        mainLoop();
        Client.getInstance().disconnect();
    }
}
