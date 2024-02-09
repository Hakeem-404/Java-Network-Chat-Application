package codex;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws IOException { 
		if (args.length != 1) {
			System.err.println("Pass the server IP ...");
			return; 
		}
		try (Socket socket = new Socket(args[0], 7000)) {
			Scanner in = new Scanner(socket.getInputStream()); 
			System.out.println("Server response: " + in.nextLine());
		}
	}
}