import client.Client;
import server.Server;

public class Main {

    // TODO: Handle client disconnecting from the server (both on the server's and client's side)
    // TODO: Leaving one party and joining a different one
    // TODO: Closing a party when the last user leaves

    private static final boolean isSimultaneous = true;

    private static void simultaneous() {
        new Thread(Server::main).start();
        new Thread(Client::main).start();
    }

    public static void main(String[] args) {
        if (isSimultaneous) {
            simultaneous();
        } else {
            Client.main();
        }
    }
}
