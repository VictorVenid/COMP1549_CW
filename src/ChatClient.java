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

/**     CHAT CLIENT
 * Simple swing chat interface:
 *      - message box
 *      - text (out) box
 *      - members box (displays active members)
 *
 * Works by reading flags at start of received input:
 *      SUBMITNAME, NAMEACCEPTED, MESSAGE, COORDINATOR, MEMBERS
 *
 * Remembers user and messages until exit (even if server crashes)
 *
 * If server connection cannot be established, retry option appears
 */
public class ChatClient {
    String serverAddress;
    Scanner in;
    PrintWriter out;
    String name;
    String coordinator;

    // Interface
    JFrame frame = new JFrame("Chat");
    JTextArea membersArea = new JTextArea(18, 10);
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(17, 40);

    /**     CLIENT
     * constructs the client
     *
     */
    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;

        // Interface
        textField.setEditable(false);
        messageArea.setEditable(false);
        membersArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(membersArea), BorderLayout.EAST);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Text field setup
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                // exit on "/quit"
                if (message.toLowerCase().startsWith("/quit")) {
                    System.exit(0);
                    // otherwise: send out contents
                } else {
                    out.println(textField.getText());
                }
                // set to empty
                textField.setText("");
            }
        });
    }

    // LogIn popup window & client based name handling
    private String getName() {
        // if there is a previous session, resume it
        if (name != null && !name.isEmpty()) {return name;}
        // otherwise, ask for LogIn
        name = JOptionPane.showInputDialog(frame, "Choose a username:", "Username selection",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null) {System.exit(0);}
        // replace all " " in name (name standard is any character except " ")
        name = name.replaceAll(" ", "");
        name = name.replaceAll(",", "");
        return name;
    }

    /**     RUN
     *  runs the client, tries to connect
     *  if not able to connect(Exception ServerNotResponding):
     *      display a option box with retry and cancel(close) option
     *  if connected(try):
     *      reads the input from the server
     *      handles Flag interpretation
     */
    private void run() throws IOException {
        while (true) {
            try {
                Socket socket = new Socket(serverAddress, 59001);
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Handle Flags
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
                        coordinator = line.substring(12);
                        membersArea.setText(null);
                        membersArea.append("Coordinator: \n" + coordinator + "\n\nMembers: \n");
                    } else if (line.startsWith("MEMBERS")) {
                        String members = " " + line.substring(line.indexOf("[") + 1, line.indexOf("]")) + ", ";
                        members = members.replaceAll(" " + coordinator + ", ", " ");
                        members = members.replaceAll(" ", "");
                        membersArea.append(members.replaceAll(",", "\n"));
                    }
                }
            } catch (Exception ServerNotResponding){
                String[] options = {"Retry", "Cancel"};
                int retry = JOptionPane.showOptionDialog(frame,
                        "Server not responding!", "Connection Problem",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (retry == 1 || retry == -1) {
                    System.exit(0);
                }
            }
        }
    }

    /**     MAIN
     * server IP should be passed as argument to the client
     * if not, ask for it
     * otherwise create and run the client
     */
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
