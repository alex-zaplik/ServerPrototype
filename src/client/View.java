package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

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
            } catch (IOException e) {
                e.printStackTrace();

                System.out.println(messages[5]);
            }
        }

        System.out.println(messages[4]);

        while (running) {
            String output;
            String input;

            try {
                System.out.print("> ");
                output = scan.readLine();

                Client.getInstance().getOut().println(output);

                input = Client.getInstance().getIn().readLine();
                if (input != null) {
                    System.out.println(input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
