package codexmulti;

import java.net.ServerSocket;
import java.util.concurrent.*;

public class Server {

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(3000)) {
            while (true) {
                pool.execute(new ClientHandler(listener.accept()));
            }
        }
    }
}