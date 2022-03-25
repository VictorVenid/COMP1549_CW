import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Multithreaded Chat Server
 * Works with:
 *              FLAGS: SUBMITNAME, NAMEACCEPTED, MESSAGE, COORDINATOR, MEMBERS
 *                     - at the begining of every output.
 *
 *
 *
 * When a client connects the server requests a screen
 * name by sending the client the text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received. After a client submits a unique name, the server acknowledges
 * with "NAMEACCEPTED". Then all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name. The broadcast messages are prefixed
 * with "MESSAGE".
 */
public class ChatServer {

    // names(Set) - stores usernames
    private static Set<String> names = new HashSet<>();

    // coordinator - username of coordinator
    private static String coordinator = null;

    // writers  - used to send messages
    //          - dictionary {name : PrintWriter}
    private static HashMap<String, PrintWriter> writers = new HashMap<>();

    // Start the Server
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    /**     CLIENT HANDLER     */
    private static class Handler implements Runnable {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        public Handler(Socket socket) {
            this.socket = socket;
        }


        /**     RUN
         * Requests SUBMITNAME
         */
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            if (coordinator == null) {
                                coordinator = name;
                            }
                            break;
                        }
                    }
                }

                // Now that a successful name has been chosen, add the socket's print writer
                // to the set of all writers so this client can receive broadcast messages.
                // But BEFORE THAT, let everyone else know that the new person has joined!
                out.println("NAMEACCEPTED " + name);
                for (HashMap.Entry<String, PrintWriter> writer : writers.entrySet()) {
                    DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime time = LocalTime.now();
                    writer.getValue().println("MESSAGE " + name + " has joined (" + time.format(hhmm) + ")");
                }
                writers.put(name, out);
                if (names.size() == 1) {
                    DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime time = LocalTime.now();
                    writers.get(name).println("MESSAGE You are the first to join and the coordinator of this chat (" + time.format(hhmm) + ")");
                    writers.get(name).println("COORDINATOR " + coordinator);
                } else {
                    for (HashMap.Entry<String, PrintWriter> writer : writers.entrySet()) {
                        writer.getValue().println("COORDINATOR " + coordinator);
                        writer.getValue().println("MEMBERS " + names);
                    }
                }
                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.startsWith("/[")) {
                        DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");
                        LocalTime time = LocalTime.now();
                        String toName = new String(input.substring(input.indexOf("[")+1, input.indexOf("]")));
                        writers.get(toName).println("MESSAGE " + name + "(pm)(" + time.format(hhmm) + "): " + input.substring(input.indexOf("]")+1));
                        writers.get(name).println("MESSAGE pm to " + toName + "(" + time.format(hhmm) + "): " + input.substring(input.indexOf("]")+1));
                    } else {
                        for (HashMap.Entry<String, PrintWriter> writer : writers.entrySet()) {
                            DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");
                            LocalTime time = LocalTime.now();
                            writer.getValue().println("MESSAGE " + name + "(" + time.format(hhmm) + "): " + input);
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
                    writers.remove(name);
                    if (name == coordinator) {
                        if (names.isEmpty()){
                            coordinator = null;
                        }
                        for (HashMap.Entry<String, PrintWriter> writer : writers.entrySet()) {
                            DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");
                            LocalTime time = LocalTime.now();
                            coordinator = names.iterator().next();
                            writer.getValue().println("MESSAGE " + name + " has left. The new coordinator is: " + coordinator + "(" + time.format(hhmm) + ")");
                            writer.getValue().println("COORDINATOR " + coordinator);
                            writer.getValue().println("MEMBERS " + names);
                        }
                    } else {
                        for (HashMap.Entry<String, PrintWriter> writer : writers.entrySet()) {
                            DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");
                            LocalTime time = LocalTime.now();
                            writer.getValue().println("MESSAGE " + name + " has left (" + time.format(hhmm) + ")");
                            writer.getValue().println("COORDINATOR " + coordinator);
                            writer.getValue().println("MEMBERS " + names);
                        }
                    }
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}
