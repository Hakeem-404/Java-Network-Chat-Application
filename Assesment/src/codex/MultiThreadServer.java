package codex;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MultiThreadServer {
    private static final int PORT = 59001;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws IOException {
        System.out.println("The chat server is running...");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private Scanner in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new Scanner(clientSocket.getInputStream());
                clientWriters.add(out);

                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    broadcastMessage(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clientWriters.remove(out);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private static class ClientHandler extends Thread {
            private final Socket clientSocket;
            private PrintWriter out;
            private Scanner in;

            public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }

            public void run() {
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new Scanner(clientSocket.getInputStream());
                    clientWriters.add(out);

                    while (in.hasNextLine()) {
                        String input = in.nextLine();
                        broadcastMessage(input);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        clientWriters.remove(out);
                    }
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        private void broadcastMessage(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
