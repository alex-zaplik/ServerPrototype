package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Map;

public class View implements Runnable {

    private boolean running = true;
    //private Client client;

    private BufferedReader scan;

    private String[] messages = {
            "Starting the client...",                   // 0
            "Enter the server's IP address: ",          // 1
            "Enter the server's port: ",                // 2
            "Connecting to the server...",              // 3
            "Connected to the server!",                 // 4
            "Connection failed",                        // 5
            "Invalid input given"                       // 6
    };


    View() {
        scan = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println(messages[0]);

        while (!Client.getInstance().isConnected()) {
            try {
                System.out.print(messages[1]);
                String address = scan.readLine();

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

                System.out.println(messages[3]);

                Client.getInstance().setSocket(new Socket(address, port));
                Client.getInstance().setOut(new PrintWriter(Client.getInstance().getSocket().getOutputStream(), true));
                Client.getInstance().setIn(new BufferedReader(new InputStreamReader(Client.getInstance().getSocket().getInputStream())));

                System.out.println(messages[4]);

                String response = Client.getInstance().getIn().readLine();
                if (response != null) {
                    Map<String, Object> responseMap = Client.getInstance().getParser().parse(response);

                    if (!responseMap.containsKey("s_msg")) {
                        for (int i = (int) responseMap.get("i_size") - 1; i >= 0; i--) {
                            System.out.println(responseMap.get("s_name") + " [" + responseMap.get("i_left") + "/" + responseMap.get("i_max") + "]");

                            if (i > 0)
                                response = Client.getInstance().getIn().readLine();

                            if (response == null) {
                                System.out.println("Something went wrong...");
                                break;
                            }

                            responseMap = Client.getInstance().getParser().parse(response);
                        }
                    } else {
                        System.out.println(responseMap.get("s_msg"));
                    }
                }

                System.out.print("> ");
                String command = scan.readLine();
                if (command.charAt(0) == 'c') {
                    // Creating a new party. command = c <name> <max>
                    String[] settings = command.split(" ");

                    // TODO: Handle wrong input
                    String name = settings[1];
                    int max = Integer.parseInt(settings[2]);

                    Client.getInstance().getOut().println(
                            Client.getInstance().getBuilder()
                            .put("i_action", 0)
                            .put("s_name", name)
                            .put("i_max", max)
                            .get()
                    );
                } else {
                    // Joining party named 'command'

                    // TODO: Handle wrong input
                    Client.getInstance().getOut().println(
                            Client.getInstance().getBuilder()
                                    .put("i_action", 1)
                                    .put("s_name", command)
                                    .get()
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();

                System.out.println(messages[5]);
            }
        }

        // TODO: This is just a ping pong conversation
        new Thread(() -> {
            String input;

            while (true) {
                try {
                    input = Client.getInstance().getIn().readLine();

                    if (input != null) {
                        System.out.println(input);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (running) {
            String output;

            try {
                System.out.print("> ");
                output = scan.readLine();

                Client.getInstance().getOut().println(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
