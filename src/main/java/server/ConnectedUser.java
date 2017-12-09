package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedUser {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int ID;

    public ConnectedUser(Socket socket, int ID) throws IOException {
        this.socket = socket;
        this.ID = ID;

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public int getID() {
        return ID;
    }
}
