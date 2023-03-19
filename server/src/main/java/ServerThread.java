import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private final int port;
    private DataInputStream in;
    private PrintWriter out;
    private ServerSocket server;
    private Socket socket;


    public ServerThread ( int port ) {
        this.port = port;
        try {
            server = new ServerSocket ( this.port );
        } catch ( IOException e ) {
            e.printStackTrace ( );
        }
    }

    /**
     *
     * @return threadpool_size Reads from Server.config the size of our thread/task pool
     */


    public void run ( ) {

        while ( true ) {
            try {
                System.out.println ( "Accepting Data" );
                socket = server.accept ( );

                in = new DataInputStream ( socket.getInputStream ( ) );
                out = new PrintWriter ( socket.getOutputStream ( ) , true );

                String message = in.readUTF ( );

                System.out.println ( "***** " + message + " *****" );
                out.println ( message.toUpperCase ( ) );

            } catch ( IOException e ) {
                e.printStackTrace ( );
            }
        }

    }
}
