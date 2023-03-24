import java.io.FileWriter;
import java.io.IOException;

public class  Main {

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



