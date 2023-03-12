import java.io.File;
import java.io.IOException;

public class Main {

    public static void main ( String[] args ) {
        /**
         * Every time we run this project we will have ONE new empty file, previous sessions won't be saved
         * This is called here, because we need a one time per run type of thing, only one NEW file per execution
         */
        File serverLog = new File ("server","Server.log");
        try {
            serverLog.createNewFile();
            System.out.println ( "SERVER: A new SERVER.LOG has been created.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ServerThread server = new ServerThread ( 8888 );
        server.start ( );
    }
}
