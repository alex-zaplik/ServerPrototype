package client;

import message.builder.IMessageBuilder;
import message.builder.JSONMessageBuilder;
import message.parser.IMessageParser;
import message.parser.JSONMessageParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static Client instance = null;

    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;

    private IMessageParser parser;
    private IMessageBuilder builder;

    private View view;
    private Model model;

    public static Client getInstance() {
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public IMessageParser getParser() {
        return parser;
    }

    public IMessageBuilder getBuilder() {
        return builder;
    }

    public void disconnect() {
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

    public boolean isConnected() {
        return socket != null && out != null && in != null;
    }

    public void init() {
        model = new Model(getInstance());
        view = new View();

        parser = new JSONMessageParser();
        builder = new JSONMessageBuilder();

        new Thread(view).start();
    }

    public static void clientMain(String[] args) {
        getInstance();
    }
}
