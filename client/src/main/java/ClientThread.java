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
    private LinkedBlockingQueue<ClientThread> clientThreadQueue ;
    private int queueCapacity;
    ReentrantLock queueConditionLock = new ReentrantLock();
    Condition queueNotFull = queueConditionLock.newCondition();
    ReentrantLock serverPrintLock = new ReentrantLock();
    private ReentrantLock logWrittenLock = new ReentrantLock();
    private ReentrantLock messageLock = new ReentrantLock();
    private boolean amIDone;
    private boolean connected;
    private static final Semaphore serverAccess = new Semaphore(3); // Maximum of 10 threads can access the server at the same time
    public ClientThread ( int port , int id , int freq ) {
        this.port = port;
        this.id = id;
        this.freq = freq;
        this.clientThreadQueue = new LinkedBlockingQueue<>();
        this.queueCapacity = 5;
        this.amIDone = false;
        this.connected = false;
    }
    public int getID () {
        return this.id;
    }
    public boolean getConnection () {
        return this.connected;
    }
    public boolean setImDone(boolean bool){
        return this.amIDone = bool;
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
        synchronized (writing){
            try (FileWriter fw = new FileWriter("server/Server.log", true)) {
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage (){
        try {
            socket = new Socket("localhost", port);
            out = new DataOutputStream(socket.getOutputStream()); // Write to the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Write to console

            serverPrintLock.lock();
            System.out.println("WRITE YOUR MESSAGE:");
            String message = scan.nextLine();
            try {
                out.writeUTF("CLIENT "+id+": " + message);
                System.out.println("\nMessage sent sucessuflly. ");
            } finally {
                serverPrintLock.unlock();
            }
            logWrittenLock.lock();
            WriteLog(message,3);
            logWrittenLock.unlock();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean stopLiving(){
        return amIDone;
    }
    public void connectsToServer(Semaphore serverAccess) {
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
            serverAccess.acquire();

            serverPrintLock.lock();
            WriteLog("", 1); // Writing on server.log
            this.connected = true;
            serverPrintLock.unlock();

            socket = new Socket("localhost", port);
            out = new DataOutputStream(socket.getOutputStream()); // Write to the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Write to console

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
            logWrittenLock.lock();
            WriteLog("", 2);
            logWrittenLock.unlock();
        }
        try {
            this.connected=false;
            this.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
