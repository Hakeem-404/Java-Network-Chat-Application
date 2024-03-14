package codexx;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class ClientHandler implements Runnable, Observer {
    private String name;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Server server;
    private boolean isCoordinator;
    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static PrintWriter coordinatorWriter = null;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        server.register(this);
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            synchronized (names) {
                isCoordinator = coordinatorWriter == null;
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

            if (isCoordinator) {
                coordinatorWriter = out;
                out.println("MESSAGE You are the coordinator");
            }

            for (String connectedClient : names) {
                if (!connectedClient.equals(name)) {
                    out.println("MESSAGE " + connectedClient + " is online");
                }
            }

            for (PrintWriter writer : writers) {
                writer.println("MESSAGE " + name + " has joined");
            }
            writers.add(out);

            while (true){
                String input = in.nextLine();
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
                } else if (input.startsWith("PRIVATE")) {
                    String[] parts = input.split(" ", 3);
                    if (parts.length == 3) {
                        sendPrivateMessage(name, parts[1], parts[2]);
                    }
                } else {
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
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

                if (isCoordinator) {
                    synchronized (names) {
                        coordinatorWriter = null;
                        if (!writers.isEmpty()) {
                            coordinatorWriter = writers.iterator().next();
                            coordinatorWriter.println("MESSAGE You are the new coordinator");
                        }
                    }
                }
                // Notify other clients about the leaving client including their name
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has left");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
            server.unregister(this); // Unregister the client handler from the server
        }
    }

    private void sendPrivateMessage(String sender, String recipient, String message) {
        for (PrintWriter writer : writers) {
            writer.println("PRIVATE " + sender + " " + recipient + " " + message);
        }
    }

    @Override
    public void update(String message) {
        out.println("MESSAGE " + message);
    }
}