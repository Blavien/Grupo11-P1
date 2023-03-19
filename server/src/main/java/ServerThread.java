import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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

    public void run ( ) {

        while ( true ) {
            try {
                System.out.println ( "Accepting Data" );
                socket = server.accept ( );
                in = new DataInputStream ( socket.getInputStream ( ) );
                out = new PrintWriter ( socket.getOutputStream ( ) , true );
                String message = in.readUTF ( );
                FiltroThread A = new FiltroThread(message,"C:\\Users\\André\\IdeaProjects\\Grupo11-P1\\server\\filtro.txt");
                A.run();
                message=A.getFilteredMessage();
                System.out.println ( "***** " + message + " *****" );
                out.println ( message.toUpperCase ( ) );
            } catch ( IOException e ) {
                e.printStackTrace ( );
            }
        }

    }
}
