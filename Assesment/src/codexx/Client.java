package codexx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

    String serverAddress;
    String port;
    String id;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);

    public Client(String serverAddress, String port, String id) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.id = id;

        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                if (message.startsWith("@")) { // Modified to use '@' as the prefix for private messages
                    String[] parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        out.println("PRIVATE " + parts[0].substring(1) + " " + parts[1]); // Sending private message command to server
                    } else {
                        messageArea.append("Invalid format. Usage: @[name] [message]\n");
                    }
                } else {
                    // Handle normal messages (broadcast to everyone)
                    out.println("MESSAGE " + message);
                }
                textField.setText("");
            }
        });
    }

    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    private void run() throws IOException {
        String screenName = getName();
        try (Socket socket = new Socket(serverAddress, Integer.parseInt(port))) {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(id);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(screenName);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter: " + "Name: " + screenName + ", ID: " + id);
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                } else if (line.startsWith("PRIVATE")) {
                    String[] parts = line.split(" ", 4);
                    String sender = parts[1];
                    String recipient = parts[2];
                    String message = parts[3];
                    if (id.equals(recipient)) {
                        messageArea.append("private message: [" + sender + " -> " + recipient + "]: "  + message + "\n");
                    } else if (id.equals(sender)) {
                        messageArea.append("[" + sender + " -> " + recipient + "]: "  + message + "\n");
                    }
                }

            }
        }

        finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = JOptionPane.showInputDialog(
                null,
                "Enter the server address:",
                "Server Address",
                JOptionPane.QUESTION_MESSAGE
        );
        String port = JOptionPane.showInputDialog(
                null,
                "Enter the server port:",
                "Server Port",
                JOptionPane.QUESTION_MESSAGE
        );

        String id = JOptionPane.showInputDialog(
                null,
                "Enter your ID:",
                "ID Selection",
                JOptionPane.QUESTION_MESSAGE
        );

        if (serverAddress == null || port == null || id == null) {
            return;
        }
        Client client = new Client(serverAddress, port, id);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
