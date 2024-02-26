package codex;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class Server {

    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static Map<String, PrintWriter> clientWriters = new HashMap<>();
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
        private boolean isCoordinator;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (names) {
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
                            clientWriters.put(name, out); // Store PrintWriter associated with client name
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
                else if (isCoordinator && writers.size() == 1) {
                    // If this is the only client, it becomes the coordinator
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
//                    broadcastMessage("MESSAGE " + name + " has joined");
                }
                writers.add(out);
                
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    } else if (input.startsWith("/private")) {
                        handlePrivateMessage(input);
                    } 
//                        else {
//                        // Broadcast regular message to all clients
//                        broadcastMessage("MESSAGE " + name + ": " + input);
//                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                    clientWriters.remove(name);
//                    broadcastMessage("MESSAGE " + name + " has left");
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
        
        private void handlePrivateMessage(String input) {
            String[] tokens = input.split(" ");
            if (tokens.length >= 3 && tokens[0].equalsIgnoreCase("/private")) {
                String recipient = tokens[1];
                StringBuilder message = new StringBuilder();
                for (int i = 2; i < tokens.length; i++) {
                    message.append(tokens[i]).append(" ");
                }
                String formattedMessage = "PRIVATE " + name + ": " + message.toString().trim();
                
                // Retrieve the PrintWriter for the recipient from the clientWriters map
                PrintWriter recipientWriter = clientWriters.get(recipient);
                if (recipientWriter != null) {
                    // Send the private message to the recipient
                    recipientWriter.println(formattedMessage);
                    // Notify the sender that the message was sent successfully
                    out.println("PRIVATE You to " + recipient + ": " + message.toString().trim());
                } else {
                    // Notify the sender that the recipient is offline or doesn't exist
                    out.println("MESSAGE Error: User " + recipient + " not found or offline");
                }
            } else {
                // Notify the sender of incorrect private message format
                out.println("MESSAGE Error: Invalid private message format. Use /private <recipient> <message>");
            }
        }

 }
    
}