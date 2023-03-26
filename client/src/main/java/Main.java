import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ConcurrentHashMap<Integer, ClientThread> clients = new ConcurrentHashMap<>();
    private static final Scanner in = new Scanner(System.in);

    /**
     * The main is the menu of the client, it is where you can choose the options that lets you interact with the
     * server
     * @param args
     */
    public static void main ( String[] args ) {
        ExecutorService executor = Executors.newFixedThreadPool(3); //penso que deve ter o mesmo tamanho que o server_capacity
        int id_counter = 0;
        boolean menu = true;
        while (menu) {
            System.out.print("\033[H\033[2J"); // clear console
            System.out.flush();
            System.out.println("✶✶✶✶✶✶✶✶✶✶✶✶✶✶ GRUPO 11 ✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶");
            System.out.println("1. Choose a client and send a message");
            System.out.println("2. Create new clients");
            System.out.println("3. End a client's life");
            System.out.println("4. Genocide - Connected or Waiting");
            System.out.println("\nClients alive: "+ clients.size());
            System.out.println("\n\nChoose an option:");
            int i = in.nextInt();
            switch (i) {
                case 1:
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now, please create some.\n");
                        break;
                    }
                    System.out.println("\nFrom which client do you want to send the message to:\n");
                    String msg = "| ";
                    for (int l = 0; l < clients.size(); l++) {  //L is the id of the thread
                        msg+= clients.get(l).getID() + " | ";
                    }
                    System.out.println("Available users : \n" + msg);
                    int m = in.nextInt();
                    if (clients.containsKey(m)) {
                        if(clients.get(m).isConnected()){
                            clients.get(m).sendMessage();
                        }else{
                            System.out.println("\nU think u can send a message from an offline client?");
                        }

                    } else {
                        System.out.println("\nThat client isn't available right now. Try another one.\n");
                    }
                    break;
                case 2:
                    System.out.println("\nHow many clients do you want created:");
                    int n = in.nextInt();
                    for (int k = 0; k < n; k++) {
                        int clientId = id_counter + k;
                        ClientThread client = new ClientThread(8888, clientId, 2000);
                        clients.put(clientId, client);

                        client.WriteLog("", 4); //Enters wait

                        executor.submit(client);
                    }
                    id_counter += n;
                    break;
                case 3:
                    String message = " | ";
                    for (int l = 0; l < clients.size(); l++) {  //L is the id of the thread
                        message+= clients.get(l).getID() + " | ";
                    }
                    System.out.println("\nInput the client's id that u want to kill:\n");
                    System.out.println("Available users: " + message);
                    int clientId = in.nextInt();
                    if (clients.containsKey(clientId)) {
                        if(clients.get(clientId).isConnected()){
                            ClientThread client = clients.get(clientId);
                            client.setImDone(true);//Vai avisar a thread para terminar
                            clients.remove(clientId); //remove deste array
                        }else{
                            System.out.println("\nThat client isn't connected to the server.\n");
                        }
                    } else {
                        System.out.println("\nThat client doesn't exist. Try another one.\n");
                    }
                    break;
                case 4:
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any waiting/connected clients right now.\n");
                        break;
                    }
                    for (int l = 0; l < clients.size(); l++) {  //L is the id of the thread
                        if(clients.get(l) != null){
                            clients.get(l).setImDone(true); //Avisam para terminar estas thread
                        }else{
                            continue;
                        }
                    }
                    clients.clear();//Removes the elements that hold the threads
            }
        }
    }
}
