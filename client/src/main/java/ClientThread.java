import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class ClientThread extends Thread {
    private final int port;
    private final int id;
    private final int freq;
    private int i;
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
    private boolean amIDone;
    private boolean connected;

    private  static  Semaphore serverAccess; // Maximum of 10 threads can access the server at the same time

    static {
        try {
            serverAccess = new Semaphore(ServerConfigReader.getVariable("FINAL_MAX_CLIENTS"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public ClientThread ( int port , int id , int freq ) throws IOException {
        this.port = port;
        this.id = id;
        this.freq = freq;
        this.clientThreadQueue = new LinkedBlockingQueue<>();
        this.queueCapacity = ServerConfigReader.getVariable("queue_capacity");
        this.amIDone = false;
        this.connected = false;
        this.i= 0;
    }
    public int getID () {
        return this.id;
    }
    public boolean isConnected () {
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
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void spamMessages (){
        try {
            socket = new Socket("localhost", port);
            out = new DataOutputStream(socket.getOutputStream()); // Write to the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Write to console
            serverPrintLock.lock();
            Random rand = new Random();
            int randN = rand.nextInt();
            try {

                out.writeUTF("NEW MSG: CLIENT "+id+":"+" cara teste goodbye");
                //System.out.println("\nMessage sent sucessuflly. ");
            } finally {
                serverPrintLock.unlock();
            }
            logWrittenLock.lock();
            WriteLog(Integer.toString(randN),3);
            logWrittenLock.unlock();
            socket.close();
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
            while (clientThreadQueue.size() == queueCapacity ) { //Se a queue tiver cheia
                if(stopLiving()){break;}
                System.out.println("Client " + id + " cannot be added to queue. Queue  is full. Waiting...");
                queueNotFull.await(); // wait until the queue is not full
            }
            clientThreadQueue.offer(this); // add current client thread to queue

           /* while(serverAccess.availablePermits() == 0){//Se o semafóro de acesso ao servidor estiver bloqueado (0)
                if(stopLiving()){break;}
                System.out.println("Client " + id + " cannot be added to server. Server capacity  is full. Waiting...");
                serverNotFull.await();//thread espera
            }*/
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            clientQueueLock.unlock();
        }
        try {
            if(serverAccess.availablePermits()==0){
                System.out.println("Client " + id + " cannot be added to server. Server capacity  is full. Waiting...");
            }
            serverAccess.acquire();
            socket = new Socket("localhost", port);

            out = new DataOutputStream(socket.getOutputStream()); // Write to the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Write to console

            serverPrintLock.lock();
            WriteLog("", 1); // Writing on server.log
            out.writeUTF("CLIENT "+ id+" has connected!");
            System.out.println("CLIENT " + id + " has connected to the server.");
            this.connected = true;
            serverPrintLock.unlock();

            socket.close();
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

        while (!this.isInterrupted() && !stopLiving()) {
            // continue running until the thread is interrupted or stopLiving() returns true

            //************** TESTE - MENSAGENS ********************************

            //Tirar os comentários disto para testar o paralelismo do envio das mensagens
           /* while(i != 10){//Cada thread manda 10 mensagens logo de inicio para não arrebentar o server
                spamMessages();
                i++;
            }*/
        }
        if (stopLiving()) {
            try {
                socket = new Socket("localhost", port);
                out = new DataOutputStream(socket.getOutputStream()); // Write to the server
                if(this.isConnected()){
                    serverAccess.release();//Faço release do semáforo que está dentro da funçao connectToServer()
                    out.writeUTF("CLIENT "+ id+ " was killed!");
                }

                System.out.println("CLIENT "+ id+ " has died!");

                socket.close();
                logWrittenLock.lock();
                WriteLog("", 2);
                logWrittenLock.unlock();
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        try {
            this.connected=false;
            this.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
