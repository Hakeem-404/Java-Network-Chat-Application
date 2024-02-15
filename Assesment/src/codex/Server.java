package codex;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;

public class Server {

    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static PrintWriter coordinatorWriter = null; // sets default coordinator to zero or null

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    private static class Handler implements Runnable {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private boolean isFirstClient;
        private boolean isCoordinator;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (names) {
                    isFirstClient = names.isEmpty();
                    isCoordinator = coordinatorWriter == null; // Check if the client is the first connected client
                }

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                
                out.println("NAMEACCEPTED " + name);

                // Check if the client is the first connected client and Notify the new client that they are the coordinator
                if (isCoordinator) {
					coordinatorWriter = out;
                    out.println("MESSAGE You are the coordinator");
                }
                 
                // Notify the new client about the currently connected clients
                for (String connectedClient : names) {
                    // Skip sending the current user's name
                    if (!connectedClient.equals(name)) {
                        out.println("MESSAGE " + connectedClient + " is online");
                    }
                }

                // Notify all clients that a new client has joined, including the screen name
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has joined");
                }
                writers.add(out);

                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println(name + " is leaving");
                    names.remove(name);
                    
                 // If the leaving client is the coordinator, choose a new coordinator
                    if (isCoordinator) {
                        synchronized (names) {
                            coordinatorWriter = null; // returns coordinator to default, which is null
                            if (!writers.isEmpty()) {
                                // Choose a random client to be the new coordinator
                                coordinatorWriter = writers.iterator().next();
                                coordinatorWriter.println("MESSAGE You are the new coordinator");
                            }
                        }
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + " has left");
                    }
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}