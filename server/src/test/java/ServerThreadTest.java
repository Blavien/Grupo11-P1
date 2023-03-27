import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class ServerThreadTest {




    @Test
    @DisplayName("Test if the port is not empty")
    void ServerThreadNotEmpty ()  {
        int port=8080;
        ServerThread serverTest = new ServerThread(port);
        assertNotNull(serverTest);

    }

    @Test
    @DisplayName("Test if accept the exception")
    void ServerThreadException() throws IOException{
        int port = 10;
        ServerSocket serverTest = new ServerSocket(port);
        assertThrows(BindException.class, () -> new ServerSocket(port));
        serverTest.close();
    }



    @Test
    @DisplayName("Test message of the producer")
    void exceptionProducer() throws InterruptedException {
        String message ="messsage";
        BlockingQueue producer = new LinkedBlockingQueue();
        producer.put(message);
        assertTrue(producer.contains(message));
    }

}