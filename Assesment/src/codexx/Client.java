package codexx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

    String serverAddress;
    String port;
    String clientID;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea publicMessageArea = new JTextArea(16, 50);
    JTextArea privateMessageArea = new JTextArea(16, 20);

    public Client(String serverAddress, String port) {
        this.serverAddress = serverAddress;
        this.port = port;
        
        String assignedID = UUID.randomUUID().toString();
        this.clientID = assignedID.substring(0, 8);

        //Frame configuration
        textField.setEditable(false);
        publicMessageArea.setEditable(false);
        privateMessageArea.setEditable(false);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(publicMessageArea));
        splitPane.setRightComponent(new JScrollPane(privateMessageArea));
        splitPane.setDividerLocation(400);
        
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        frame.pack();


        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                textField.setText("");
                if (message.startsWith("@")) {
                    String[] parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        out.println("PRIVATE " + parts[0].substring(1) + " " + parts[1]);
                    } else {
                        publicMessageArea.append("Invalid format. Usage: @[name] [message]\n");
                    }
                } else {
                    out.println(message);
                }
            }
        });
    }

    public String getName() {
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
            
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(screenName + " (" + clientID + ")" );
                } else if (line.startsWith("NAMEACCEPTED")) {
                    frame.setTitle("Name: " + screenName + "  ID: " + clientID);
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    publicMessageArea.append(line.substring(8) + "\n");
                } else if (line.startsWith("PRIVATE")) {
                    String[] parts = line.split(" ", 4);
                    String sender = parts[1];
                    String recipient = parts[2];
                    String message = parts[3];
                    if (clientID.equals(recipient)) {
                    	LocalDateTime now = LocalDateTime.now();
                        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        privateMessageArea.append("[" + time + "] " + "Private message from " + sender + " : "  + message + "\n");
                    }
                }

            }
        } catch (IOException e) {
            // Handle IOException (e.g., connection failure)
            JOptionPane.showMessageDialog(frame, "Error: Unable to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Or log the exception
        } catch (NumberFormatException e) {
            // Handle NumberFormatException (e.g., invalid port)
            JOptionPane.showMessageDialog(frame, "Error: Invalid port number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Or log the exception
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Or log the exception
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

        if (serverAddress == null || serverAddress.trim().isEmpty()) {
        	    JOptionPane.showMessageDialog(null, "Server address cannot be blank");
        	    return;
        	}; 

        if ( port == null || port.trim().isEmpty()) {
        	    JOptionPane.showMessageDialog(null, "Server port not cannot be blank");
        	    return;
        	};

        Client client = new Client(serverAddress, port);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}