package codexx;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client {
    private static final int PORT = 59001;

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Send messages typed by the user to the server
            while (true) {
                String message = scanner.nextLine();
                out.println(message);
            }
        }
    }
}
