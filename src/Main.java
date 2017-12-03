import client.Client;
import server.Server;

public class Main {

    private static final boolean isServer = false;

    public static void main(String[] args) {
        if (isServer) {
            Server.serverMain(args);
        } else {
            Client.clientMain(args);
        }
    }
}
