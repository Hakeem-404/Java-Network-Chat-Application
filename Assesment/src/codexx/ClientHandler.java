package codexx;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private PrintWriter out;
    private Scanner in;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner(clientSocket.getInputStream());
            synchronized (clientWriters) {
                clientWriters.add(out);
            }

            while (in.hasNextLine()) {
                String input = in.nextLine();
                broadcastMessage(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
