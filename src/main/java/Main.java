import client.Client;
import server.Server;

public class Main {

    private static final boolean isSimultaneous = false;

    private static void simultaneous(String[] args) {
        new Thread(() -> Server.serverMain(args)).start();
        new Thread(() -> Client.clientMain(args)).start();
    }

    public static void main(String[] args) {
        if (isSimultaneous) {
            simultaneous(args);
        } else {
            Client.clientMain(args);
        }
    }
}
