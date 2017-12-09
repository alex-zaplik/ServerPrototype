package client;

import message.builder.IMessageBuilder;
import message.builder.JSONMessageBuilder;
import message.parser.IMessageParser;
import message.parser.JSONMessageParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class representing the client functionality
 *
 * @author Aleksander Lasecki
 */
public class Client {

    /**
     * Singleton instance
     */
    private static Client instance = null;

    /**
     * The socket used for communicating with the server
     */
    private Socket socket = null;
    /**
     * PrintWriter used for sending messages to the server
     */
    private PrintWriter out = null;
    /**
     * BufferedReader used to receive messages from the server
     */
    private BufferedReader in = null;

    /**
     * Used to parse received messages
     */
    private IMessageParser parser;
    /**
     * Used to build messages that are to be sent throw a socket
     */
    private IMessageBuilder builder;

    /**
     * Reference to the view
     */
    private View view;

    /**
     * Thread used to wait for messages sent by the server
     */
    private Thread inputListener = new Thread(() -> {
        String input;

        while (true) {
            if (getIn() != null) {
                try {
                    input = getIn().readLine();

                    if (input != null)
                        view.handleInput(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    /**
     * Starts the listening thread
     */
    void startListening() {
        inputListener.start();
    }

    /**
     * Sends a join request to the server
     *
     * @param name  Name of the party that the users wants to join
     */
    void joinParty(String name) {
        getOut().println(
                getBuilder()
                .put("i_action", 1)
                .put("s_name", name)
                .get()
        );
    }

    /**
     * Sends a create request to the server
     *
     * @param name  Name of the party to be created
     * @param max   Maximum number of users connected to the new party
     */
    void sendPartySettings(String name, int max) {
        getOut().println(
                getBuilder().put("i_action", 0)
                .put("s_name", name)
                .put("i_max", max)
                .get()
        );
    }

    /**
     * Sends a message to the server
     *
     * @param msg   The message to be sent
     */
    void sendMessage(String msg) {
        getOut().println(msg);
    }

    /**
     * Gets a list of all running parties from the server
     *
     * @return              The received list
     * @throws IOException  Thrown by the BufferedReader
     */
    List<Party> getParties() throws IOException {
        List<Party> parties = new ArrayList<>();

        String response = getIn().readLine();
        if (response != null) {
            Map<String, Object> responseMap = Client.getInstance().getParser().parse(response);

            if (!responseMap.containsKey("s_msg")) {
                for (int i = (int) responseMap.get("i_size") - 1; i >= 0; i--) {
                    parties.add(new Party((String) responseMap.get("s_name"), (int) responseMap.get("i_left"), (int) responseMap.get("i_max")));

                    if (i > 0)
                        response = Client.getInstance().getIn().readLine();

                    if (response == null)
                        throw new IOException();

                    responseMap = Client.getInstance().getParser().parse(response);
                }
            } else {
                parties.add(new Party((String) responseMap.get("s_msg"), -1, -1));
            }
        }

        return parties;
    }

    /**
     * Initializes the connection with a server
     *
     * @param address   The address of the server
     * @param port      The port of the server
     */
    void initConnection(String address, int port) {
        try {
            setSocket(new Socket(address, port));
            setOut(new PrintWriter(Client.getInstance().getSocket().getOutputStream(), true));
            setIn(new BufferedReader(new InputStreamReader(Client.getInstance().getSocket().getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance
     *
     * @return  The instance
     */
    static Client getInstance() {
        if (instance == null) {
            synchronized (Client.class) {
                if (instance == null) {
                    instance = new Client();
                    instance.init();
                }
            }
        }

        return instance;
    }

    /**
     * Returns the client's socket
     *
     * @return  The client's socket
     */
    private Socket getSocket() {
        return socket;
    }

    /**
     * Sets the client's socket
     *
     * @param socket The socket
     */
    private void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Returns the output of the client
     *
     * @return  The output of the client
     */
    private PrintWriter getOut() {
        return out;
    }

    /**
     * Sets the output of the client
     *
     * @param out   The output PrintWriter
     */
    private void setOut(PrintWriter out) {
        this.out = out;
    }

    /**
     * Returns the input of the client
     *
     * @return  The input of the client
     */
    private BufferedReader getIn() {
        return in;
    }

    /**
     * Sets the input of the client
     *
     * @param in   The input BufferedReader
     */
    private void setIn(BufferedReader in) {
        this.in = in;
    }

    /**
     * Returns the parser used by the client
     *
     * @return  The parser used by the client
     */
    private IMessageParser getParser() {
        return parser;
    }

    /**
     * Returns the builder used by the client
     *
     * @return  The builder used by the client
     */
    private IMessageBuilder getBuilder() {
        return builder;
    }

    /**
     * Disconnects the user from the server
     */
    void disconnect() {
        try {
            out.close();
            in.close();
            socket.close();

            out = null;
            in = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the client is connected to a server
     *
     * @return  True if the client is connected to a server
     */
    boolean isConnected() {
        return socket != null && out != null && in != null;
    }

    /**
     * Initializes the client instance and start the view's thread
     */
    private void init() {
        view = new View();

        parser = new JSONMessageParser();
        builder = new JSONMessageBuilder();

        new Thread(view).start();
    }

    /**
     * The client's main function
     */
    public static void main() {
        getInstance();
    }
}
