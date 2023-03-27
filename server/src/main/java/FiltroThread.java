import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;


public class FiltroThread implements Runnable {
    private String messagefiltered;
    private String message;
    private String file;
    private BlockingQueue<String> queue;  //Buffer de memória partilhado

    public FiltroThread(BlockingQueue queue, String file) {
        this.queue = queue;
        this.file = file;
    }

    public String getFilteredMessage() {
        return messagefiltered;
    }
    public void filter(String message){

            File filtro = new File(file);
            try {
                Scanner reader = new Scanner(filtro);
                reader.useDelimiter("[,\\s]+");
                List<String> messages = new ArrayList<>(Arrays.asList(message.split("[,\\s]+")));
                while (reader.hasNext()) {
                    String word = reader.next();
                    while (messages.contains(word)) {
                        messages.remove(word);
                    }
                }
            messagefiltered = String.join(" ", messages);
            System.out.println("***** " + messagefiltered+ " *****");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            int size=0;

            while(queue.size()!=size) {
                 String message = queue.take();
                 filter(message);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("The queue is empty");
        }
    }
}