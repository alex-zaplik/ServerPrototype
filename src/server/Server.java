package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void serverMain(String[] args) {
        System.out.println("Starting the server...");

        try {
            ServerSocket sSocket = new ServerSocket(4444);
            Socket socket = sSocket.accept();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected with client!");

            String input;

            while ((input = in.readLine()) != null) {
                System.out.println("Client's message: " + input);

                out.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
