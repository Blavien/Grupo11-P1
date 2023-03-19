import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.lang.Runnable;

public class FiltroThread implements Runnable {
    private String messagefiltered;
    private String message;
    private String file;

    public FiltroThread(String message, String file) {
        this.message = message;
        this.file = file;
    }

    public String getFilteredMessage() {
        return messagefiltered;
    }

    public void run() {
        File filtro = new File(file);
        try {
            Scanner reader = new Scanner(filtro);
            reader.useDelimiter("[,\\s]+");
            List<String> messages = new ArrayList<>(Arrays.asList(message.split("[,\\s]+")));
            System.out.println("Palavra " + messages);
            while (reader.hasNext()) {
                String word = reader.next();
                while (messages.contains(word)) {
                    System.out.println("Palavra " + word);
                    messages.remove(word);

                }
            }
            messagefiltered = String.join(" ", messages);
            System.out.println("Final filtered message: " + messagefiltered);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}