import java.io.FileWriter;
import java.io.IOException;

/**
 * Main class that initializes the server
 */
public class Main {
    /**
     *  main of the server
     * @param args It do be an argument
     */
    public static void main ( String[] args ) {
        String filePath = "server/Server.log";
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerThread server = new ServerThread ( 8888 );
        server.start ( );
    }
}



