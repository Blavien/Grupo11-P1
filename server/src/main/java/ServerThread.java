import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

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


    public void run() {
        while (true) {
            try {
                System.out.println("Accepting Data");
                Socket socket = server.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                Thread thread = new Thread(new RequestHandler(socket, in));
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class RequestHandler implements Runnable {
        private Socket socket;
        private DataInputStream in;
        private PrintWriter out;

        public RequestHandler(Socket socket, DataInputStream in) {
            this.socket = socket;
            this.in = in;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);

                String message = in.readUTF();
                FiltroThread filteredMessage = new FiltroThread(message,"server/filtro.txt");
                filteredMessage.run();
                out.println(message.toUpperCase());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
