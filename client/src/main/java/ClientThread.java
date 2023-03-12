import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class ClientThread extends Thread {
    private final int port;
    private final int id;
    private final int freq;
    private DataOutputStream out;
    private BufferedReader in;
    Lock l = new ReentrantLock();
    Lock writing = new ReentrantLock();
    private Socket socket;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ClientThread ( int port , int id , int freq ) {
        this.port = port;
        this.id = id;
        this.freq = freq;
    }

    /**
     * @param msg is the msg that the client is going to write, we only use this on case 3
     * @param event are the many possible events that a client can write in the server.log
     *              <p>
     *  Still on these events, they can be 'connecting' or disconnecting from the server, 'sent a message' if the client does send a message or 'waiting' when a client is waiting to enter de server
     *              <p>
     * The writing of these events are on the following format
     *              <timestamp> - Action : <type of action> - <Id of the client> - message
     *
     */
    public void WriteLog(String msg, int event) throws IOException {
        writing.lock();
        FileWriter fw = new FileWriter("C:\\Users\\User\\Desktop\\LEI 3 ANO\\2 semestre\\PA\\Projecto 1\\server\\Server.log",true);
        switch (event) {
            case 1 -> { //Connected to the server
                System.out.println(id + " WRITING ON SERVER.LOG");
                fw.append(timestamp + " - Action : CONNECTED - " + id + "\n");
            }
            case 2 -> { //Disconnected from the server
                System.out.println(id + " WRITING ON SERVER.LOG");
                fw.append(timestamp + " - Action : DISCONNECTED - " + id + "\n");
            }
            case 3 -> { //Sent a message
                System.out.println(id + " WRITING ON SERVER.LOG");
                fw.append(timestamp + " - Action : MESSAGE - " + id + " - " + msg + "\n");
            }
            case 4 -> { //Waiting to enter the server
                System.out.println(id + " WRITING ON SERVER.LOG");
                fw.append(timestamp + " - Action : WAITING - " + id + "\n");
            }
        }
        fw.close();
        writing.unlock();
    }
    public void run ( ) {
        //try {
        int i = 0;
        while (true) {
            System.out.println ( "Sending Data" );
            try {
                // if(sem.tryAcquire(1, TimeUnit.SECONDS)) {
                socket = new Socket ( "localhost" , port );
                //EXEMPLO DA ESCRITA NO SERVER.LOG, TRINCO PARA PROTEÇÃO
                l.lock();
                WriteLog( "0", 1);
                l.unlock();
                out = new DataOutputStream ( socket.getOutputStream ( ) );
                in = new BufferedReader ( new InputStreamReader ( socket.getInputStream ( ) ) );
                out.writeUTF ( "My message number " + i + " to the server " + "I'm " + id );
                String response;
                response = in.readLine ( );
                System.out.println ( "From Server " + response );
                out.flush ( );
                socket.close ( );
                sleep ( freq );
                i++;
            } catch ( IOException | InterruptedException e ) {
                e.printStackTrace ( );
            }
        }
    }
}
