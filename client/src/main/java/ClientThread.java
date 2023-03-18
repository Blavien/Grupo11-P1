import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class ClientThread extends Thread {
    private final int port;
    private final int id;
    private final int freq;
    private DataOutputStream out;
    private BufferedReader in;
    ReentrantLock writing = new ReentrantLock();
    ReentrantLock clientQueueLock = new ReentrantLock();
    ReentrantLock varWriting = new ReentrantLock();
    private Socket socket;
    Scanner scan = new Scanner(System.in);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private ExecutorService executor;
    private LinkedBlockingQueue<ClientThread> clientThreadQueue ;
    private int queueCapacity;
    ReentrantLock queueConditionLock = new ReentrantLock();
    Condition queueNotFull = queueConditionLock.newCondition();
    private boolean amIDone;
    private static final Semaphore serverAccess = new Semaphore(10); // Maximum of 10 threads can access the server at the same time
    public ClientThread ( int port , int id , int freq ) {
        this.port = port;
        this.id = id;
        this.freq = freq;
        this.clientThreadQueue = new LinkedBlockingQueue<>();
        this.queueCapacity = 10;
        this.amIDone = false;
    }
    public int getID () {
        return this.id;
    }
    /**
     * @param msg is the msg that the client is going to write, we only use this on case 3
     * @param event are the many possible events that a client can write in the server.log
     *             <p>
     *  Still on these events, they can be 'connecting' or disconnecting from the server, 'sent a message' if the client does send a message or 'waiting' when a client is waiting to enter de server
     * The writing of these events are on the following format
     *              <timestamp> - Action : <type of action> - <Id of the client> - message
     *
     */
    public void WriteLog(String msg, int event){
        writing.lock();
        FileWriter fw = null;
        try {
            fw = new FileWriter("C:\\Users\\RP\\IdeaProjects\\Grupo11-P1\\server\\Server.log",true);
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
            out = new DataOutputStream ( socket.getOutputStream ( ) );  //Write to the server
            in = new BufferedReader ( new InputStreamReader ( socket.getInputStream ( ) ) ); //Write to console

            System.out.println("\nInsert your message:");
            String message;
            message = scan.nextLine();
            out.writeUTF ( "CLIENT ID: "+ id +" - " + message);
            WriteLog(message, 3); //Writing on server.log

            String response = message;
            System.out.println ( "\nSERVER: MESSAGE RECEIVED - CLIENT " + id + "\n");
            out.flush ( );
            amIDone = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean stopLiving(){
        return amIDone;
    }
    /**
     * We will have a queue where the threads are first put in, and then by FIFO they will advance to the server
     * @param semaphore Receives the semaphore that is used to control the number of threads inside the server
     *                  <p>LOGIC</p>
     * The executor submits our threads on the main file, and this function is called on the run(), that means that threads execute this code
     * ADICIONAR DEPOIS
     */
    public void connectsToServer(Semaphore semaphore) {
        boolean logWritten = false;
        try {
            clientQueueLock.lock();
            while (clientThreadQueue.size() == queueCapacity) {
                System.out.println("Client " + id + " cannot be added to queue. Queue is full. Waiting...");
                queueNotFull.await(); // wait until the queue is not full
            }
            clientThreadQueue.offer(this); // add current client thread to queue
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            clientQueueLock.unlock();
        }
        try {
            semaphore.acquire();
            if (logWritten == false) {
                varWriting.lock();
                WriteLog("", 1); // Writing on server.log
                logWritten = true;
                varWriting.unlock();
            }
            socket = new Socket("localhost", port);
            out = new DataOutputStream(socket.getOutputStream()); // Write to the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Write to console

            out.writeUTF("\nHello server! I'm CLIENT " + id + ".");

            System.out.println("\nSERVER: Welcome to GRUPO 11 Server!! Client " + id + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Here we have a thread/task pool and the task we want to submit is the connection to the server
     */
    public void run() {
        connectsToServer(serverAccess);
        while (!Thread.currentThread().isInterrupted() && !stopLiving()) {
            // continue running until the thread is interrupted or stopLiving() returns true
        }
        if (stopLiving()) {
            System.out.println("\nThis thread is going to finish. " + id + "\n");
            serverAccess.release();
            WriteLog("", 2);
        }
        this.interrupt();
    }
}
