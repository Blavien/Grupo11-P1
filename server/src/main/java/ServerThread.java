import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread of the server
 */
public class ServerThread extends Thread {
    private final int port;
    private DataInputStream in;
    private PrintWriter out;
    private ServerSocket server;
    private Socket socket;


    /**
     * used to inicialize new server threads
     * @param port the port that is going to be connected with
     */
    public ServerThread ( int port ) {
        this.port = port;
        try {
            server = new ServerSocket ( this.port );
        } catch ( IOException e ) {
            e.printStackTrace ( );
        }
    }
    public void run() {
        while (true) {
            try {
                System.out.println("Accepting Data");
                Socket socket = server.accept();
                in = new DataInputStream(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                Thread thread = new Thread(new RequestHandler(socket, in,out));
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

        private static  BlockingQueue producer = new LinkedBlockingQueue();;

        public RequestHandler(Socket socket, DataInputStream in, PrintWriter out) {
            this.socket = socket;
            this.in = in;
            this.out= out;
        }

        @Override
        public void run() {
            try{
                String message = in.readUTF();//Gets the messsage from the client


                producer.put(message);//Puts in on test.txt queue

                FiltroThread consumer = new FiltroThread(producer,"server/filtro.txt");//It's gonna consume that message
                consumer.run();

                out.println(consumer);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
