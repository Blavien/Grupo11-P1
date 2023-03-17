import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;



public class ClientThread extends Thread {
    private final int port;
    private final int id;
    private final int freq;
    private DataOutputStream out;
    private BufferedReader in;
    ReentrantLock writing = new ReentrantLock();
    private Socket socket;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ClientThread ( int port , int id , int freq ) {
        this.port = port;
        this.id = id;
        this.freq = freq;
    }
    public int getID () {
        return this.id;
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
    public void WriteLog(String msg, int event){
        writing.lock();
        FileWriter fw = null;
        try {
            fw = new FileWriter("C:\\Users\\User\\Desktop\\LEI 3 ANO\\2 semestre\\PA\\Projecto 1\\server\\Server.log",true);
            switch (event) {
                case 1 -> { //Connected to the server
                    fw.append(timestamp + " - Action : CONNECTED - CLIENT ID:" + id + "\n");
                }
                case 2 -> { //Disconnected from the server
                    fw.append(timestamp + " - Action : DISCONNECTED - CLIENT ID:" + id + "\n");
                }
                case 3 -> { //Sent a message
                    fw.append(timestamp + " - Action : MESSAGE - CLIENT ID:" + id + " - " + msg + "\n");
                }
                case 4 -> { //Waiting to enter the server
                    fw.append(timestamp + " - Action : WAITING - CLIENT ID:" + id + "\n");
                }
            }
            fw.close();
            writing.unlock();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage (){
        try {
            Scanner scan = new Scanner(System.in);
            socket = new Socket ( "localhost" , port );
            out = new DataOutputStream ( socket.getOutputStream ( ) );  //Write to the server
            in = new BufferedReader ( new InputStreamReader ( socket.getInputStream ( ) ) ); //Write to console
            System.out.println("\nInsert your message:");
            String message;
            message = scan.nextLine();
            out.writeUTF ( "CLIENT ID: "+ id+" - " + message);
            WriteLog(message, 3); //Writing on server.log

            //Não sei para que é isto
            String response = message;
            System.out.println ( "\nSERVER: MESSAGE RECEIVED - " + response + "\n");
            out.flush ( );
            socket.close ( );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void run ( ) {
        /*int i = 0;
        while (true) {
            try {
                // if(sem.tryAcquire(1, TimeUnit.SECONDS)) {
                socket = new Socket ( "localhost" , port );
                //EXEMPLO DA ESCRITA NO SERVER.LOG, TRINCO PARA PROTEÇÃO
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
        }*/
    }
}
