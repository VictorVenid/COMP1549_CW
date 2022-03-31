import java.net.InetAddress;
import java.lang.String;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.ResourceLocks;
import static org.junit.jupiter.api.Assertions.*;

class ChatClientTest {

    /**
     * chat client JUnit test
     * void main(){
     *
     * fetch IP
     * API: DynamicTest?
     * }
     * how to test private methods with JUnit
     * not to test those private method
     * ???
     * https://www.artima.com/articles/testing-private-methods-with-junit-and-suiterunner
     * https://stackoverflow.com/questions/34571/how-do-i-test-a-class-that-has-private-methods-fields-or-inner-classes
    * */


    @Test
    public void main() {
        InetAddress ia = null;
        try{
            ia = ia.getLocalHost();
            String localip = ia.getHostAddress();
            assertTrue(ia != null,"The server err, did not pass IP");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}