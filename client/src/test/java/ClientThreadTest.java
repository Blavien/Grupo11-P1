import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ClientThreadTest {

    @org.junit.jupiter.api.Test

    void getID() {
        ClientThread clientThread = new ClientThread(8888, 15 , 10 );
        assertEquals(15,clientThread.getID());
    }

    @org.junit.jupiter.api.Test
    void setImDone() {
        ClientThread clientThread = new ClientThread(8888, 1 , 10 );

        assertEquals(true,clientThread.setImDone(true));
    }

    @org.junit.jupiter.api.Test
    void stopLiving() {
        ClientThread clientThread = new ClientThread(8888, 2 , 10 );
        assertEquals(false,clientThread.stopLiving());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test writeLog method and its possibilities")
    void writeLog() {

    }
    @org.junit.jupiter.api.Test
    void sendMessage() {
    }





}