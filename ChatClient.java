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

/**
 * A simple Swing-based client for the chat server. Graphically it is a frame with a text
 * field for entering messages and a textarea to see the whole dialog.
 *
 * The client follows the following Chat Protocol. When the server sends "SUBMITNAME" the
 * client replies with the desired screen name. The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are already in use. When the
 * server sends a line beginning with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all chatters connected to the
 * server. When the server sends a line beginning with "MESSAGE" then all characters
 * following this string should be displayed in its message area.
 */
public class ChatClient {

    String serverAddress;
    Scanner in;
    PrintWriter out;
    String name;

    // Interface
    JFrame frame = new JFrame("Chat");
    JTextArea membersArea = new JTextArea(18, 10);
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(17, 40);

    /**
     * Constructs the client by laying out the GUI and registering a listener with the
     * textfield so that pressing Return in the listener sends the textfield contents
     * to the server. Note however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED message from
     * the server.
     */
    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;

        textField.setEditable(false);
        messageArea.setEditable(false);
        membersArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(membersArea), BorderLayout.EAST);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Send on enter then clear to prepare for next message
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                if (message.toLowerCase().startsWith("/quit")) {
                    System.exit(0);
                } else {
                    out.println(textField.getText());
                }
                textField.setText("");
            }
        });
    }

    private String getName() {
        if (name != null && !name.isEmpty()) {return name;}
        name = JOptionPane.showInputDialog(frame, "Choose a username:", "Username selection", JOptionPane.PLAIN_MESSAGE);
        if (name == null) {System.exit(0);}
        name = name.replaceAll(" ", "");

        return name;
    }

    private void run() throws IOException {
        while (true) {
            try {
                Socket socket = new Socket(serverAddress, 59001);
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (line.startsWith("SUBMITNAME")) {
                        out.println(getName());
                        name = null;
                    } else if (line.startsWith("NAMEACCEPTED")) {
                        this.frame.setTitle("Chatter - " + line.substring(13));
                        name = line.substring(13);
                        textField.setEditable(true);
                    } else if (line.startsWith("MESSAGE")) {
                        messageArea.append(line.substring(8) + " \n");
                    } else if (line.startsWith("COORDINATOR")) {
                        membersArea.setText(null);
                        membersArea.append("Coordinator: \n" + line.substring(12) + "\n");
                    } else if (line.startsWith("MEMBERS")) {
                        String members = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                        membersArea.append("Members: \n" + members.replaceAll(", ", "\n"));
                    }
                }
            } catch (Exception ServerNotResponding){
                String[] options = {"Retry", "Cancel"};
                int retry = JOptionPane.showOptionDialog(frame, "Server not responding!", "Title",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (retry == 1) {
                    System.exit(0);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        ChatClient client = new ChatClient(args[0]);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
