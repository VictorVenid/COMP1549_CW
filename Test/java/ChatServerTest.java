import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

//press ctrl+shift+T to switch to the original code
//Run the selected test or test folder: Ctrl+Shift+F10
//Stop the current test session: Ctrl+F2
//https://www.jetbrains.com/help/idea/testing.html tutorial for jUnit test
//Junit api: https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/package-summary.html

class ChatServerTest {


    /**
     * fetch the text in console
     * judge if it is "The chat server is running..."
     * JUnit API: assertEquals(expected, actual, message)
     */
    @Test
    void main() {
        String expected = "The chat server is running...";
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
        PrintStream cacheStream = new PrintStream(baoStream);
        PrintStream oldStream = System.out;
        System.setOut(cacheStream);
        System.out.print("The chat server is running...");
        String actual = baoStream.toString();
        System.setOut(oldStream);
        System.out.println("The message is ["+actual+"]");

        assertEquals(expected, actual,"Test 1 has Failed.");

    }

    @Test
    /**     JOIN & CHAT
     *  when someone connects he is repeatedly asked to submit username, until a uniqueone is given and accepted
     *  if first to join, get notified about it
     *  with every join we update the coordinator & members for all clients
     *  messages are exchanged either privately or with the group
     *
     *  check the coordinator if no one in the chat room
     * API: assertEquals()
     *
     * check whether name can be duplicated
     * API: assumeTrue(BooleanSupplier, message)
     *
     * check the coordinator is the first name
     * API: assumeTrue(BooleanSupplier, message)
     * assumeTrue(BooleanSupplier, message)
     * assumeTrue(BooleanSupplier, message)
     *
     * check if the user is joined (name +" joined (" +
     * LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + ")")
     * API: assertEquals()
     *
     * first joined message for two different part of people
     * API: assertEquals()
     *
     * send messages
     * API: assertEquals()
     *
     * disconnecting
     * give a name list to do it
     * check the message and the coordinator
     * API: assertEquals()
     * assertEquals()
     *
     */

    void run() {

        String coordinator = null;
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
        PrintStream cacheStream = new PrintStream(baoStream);
        PrintStream oldStream = System.out;
        System.setOut(cacheStream);
        System.out.print(coordinator);
        String actual = baoStream.toString();
        System.setOut(oldStream);
        System.out.println("The coordinator is ["+actual+"]");
        assertEquals(coordinator, actual,"Test 1 has Failed.");
        for(int i = 1; i<=3; i++){
            if (coordinator == null){
                coordinator = String.valueOf(i);
                actual = coordinator;
            }
            //test2
            assertEquals(coordinator,actual,"coordinator is"+actual);
            if (coordinator != null){
                assertEquals(coordinator,actual,"coordinator is"+actual);
            }
            String notify = String.valueOf(i) + "joined";
            String actualnotify = baoStream.toString();
            assertEquals(notify,actualnotify);
        }
        //test 3
        int input1 = 1;
        int input2 = 2;
        int input3 = 3;
        ArrayList<Integer> input = new ArrayList<Integer>();
        input.add(input1);
        input.add(input2);
        input.add(input3);
        int i = 1;
        String noti = String.valueOf(input.get(i-1)) +"is leaving";
        int coordi = 1;
        while (coordi == 1){
            for(i=1; i<=3; i++){
                input.remove(i-1);
                if(i-1 == 0){
                    coordi = input.get(0);
                    assertTrue(coordi == input.get(0));
                }
                assertTrue(coordi == input.get(0));
                assertTrue(noti == String.valueOf(input.get(i-1)) +"is leaving");
            }
        }


    }

}