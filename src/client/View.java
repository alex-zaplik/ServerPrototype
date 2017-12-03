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
    private Client client;

    private Scanner scan;

    private String[] messages = {
            "Starting the client...",                   // 0
            "Enter the server's IP address: ",          // 1
            "Enter the server's port: ",                // 2
            "Connecting to the server...",              // 3
            "Connected to the server!",                 // 4
            "Connection failed",                        // 5
            "Invalid input given"                       // 6
    };


    View(Client client) {
        this.client = client;
        scan = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println(messages[0]);

        while (!client.isConnected()) {
            try {
                System.out.print(messages[1]);
                String address = scan.nextLine();

                int port = -1;
                while (port == -1) {
                    try {
                        System.out.print(messages[2]);
                        port = scan.nextInt();

                        if (port < 0 || port > 1 << 16) {
                            port = -1;
                            throw new InputMismatchException();
                        }
                    } catch (InputMismatchException e) {
                        System.out.println(messages[6]);
                        scan.nextLine();
                    }
                }

                System.out.println(messages[3]);

                client.setSocket(new Socket(address, port));
                client.setOut(new PrintWriter(client.getSocket().getOutputStream(), true));
                client.setIn(new BufferedReader(new InputStreamReader(client.getSocket().getInputStream())));
            } catch (IOException e) {
                e.printStackTrace();

                System.out.println(messages[5]);
                scan.nextLine();
            }
        }

        System.out.println(messages[4]);

        while (running) {
            String output;
            String input;

            System.out.print("> ");
            output = scan.nextLine();

            System.out.println("==> " + output);

            try {
                client.getOut().println(output);

                if ((input = client.getIn().readLine()) != null) {
                    System.out.println(output);
                    System.out.println(input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
