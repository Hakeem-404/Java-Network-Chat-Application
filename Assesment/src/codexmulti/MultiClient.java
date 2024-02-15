package codexmulti;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MultiClient {
    String serverAddress;
    String port;
    String id;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);

    public MultiClient(String serverAddress, String port, String id) {
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
                sendMessageToServer(message);
                textField.setText("");
            }
        });
    }

    private void sendMessageToServer(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void run() throws IOException {
        try (Socket socket = new Socket(serverAddress, Integer.parseInt(port))) {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(id);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(id);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    setTextFieldEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    private void setTextFieldEditable(boolean editable) {
        SwingUtilities.invokeLater(() -> {
            textField.setEditable(editable);
        });
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = JOptionPane.showInputDialog(null, "Enter the server address:", "Server Address",
                JOptionPane.QUESTION_MESSAGE);
        String port = JOptionPane.showInputDialog(null, "Enter the server port:", "Server Port",
                JOptionPane.QUESTION_MESSAGE);

        String id = JOptionPane.showInputDialog(null, "Enter your ID:", "ID Selection", JOptionPane.QUESTION_MESSAGE);

        if (serverAddress == null || port == null || id == null) {
            return;
        }
        MultiClient client = new MultiClient(serverAddress, port, id);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
