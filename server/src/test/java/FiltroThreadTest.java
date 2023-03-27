import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
class FiltroThreadTest {

    private BlockingQueue producer;
    private FiltroThread filtro;

    @BeforeEach
    void setUp() {
        this.producer = new LinkedBlockingQueue<>();
        this.filtro = new FiltroThread(producer, "C:\\Users\\RP\\IdeaProjects\\Grupo11-P1\\server\\test.txt");

    }



    @Test
    @DisplayName("test if getFilteredMessage works")
    void getFilteredMessage() throws InterruptedException {
        String message = "Hello World";
        producer.put(message); //
        filtro.run();
        assertEquals(message,filtro.getFilteredMessage());
    }


    @Test
    @DisplayName("test if filter works correctly removing the word teste")
    void filterRemove() throws InterruptedException {
        String message = "teste";
        producer.put(message); // coloca a mensagem na fila de entrada
        filtro.run();
        String expected = "";
        assertEquals(expected, filtro.getFilteredMessage());

    }

    @Test
    @DisplayName("test if filter removes more than one words ")
    void filterRemoveMultiplesWords() throws InterruptedException {
        String message = "teste cara teste";
        producer.put(message);
        String expected = "";

        filtro.run();
        Assertions.assertEquals("", filtro.getFilteredMessage());
    }

    @Test
    @DisplayName("test if filter removes more than one words ")
    void filterDontRemove() throws InterruptedException {
        String message = "Hello World";
        producer.put(message);
        String expected = message;
        filtro.run();
        assertEquals(expected, filtro.getFilteredMessage());
    }

    @Test
    @DisplayName("test if exist InterruptedException ")
    void interruptedExceptionTest() throws InterruptedException {
            producer.clear();
            Thread thread= new Thread(filtro);
            thread.start();
            thread.sleep(1000);
            thread.interrupt();
            Assertions.assertEquals(Thread.interrupted(),false);

    }
    @Test
    @DisplayName("test if exist IO")
    void IOException() throws IOException, InterruptedException {
        FiltroThread filtro1 = new FiltroThread(producer, "C:\\Users\\RP\\IdeaProjects\\Grupo11-P1\\server\\test.txt");
        String message = "Hello World";
        producer.put(message);
        filtro1.run();

    }

}