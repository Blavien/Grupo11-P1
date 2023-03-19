import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ConcurrentHashMap<Integer, ClientThread> clients = new ConcurrentHashMap<>();
    private static final Scanner in = new Scanner(System.in);
    public static void main ( String[] args ) {
        ExecutorService executor = Executors.newFixedThreadPool(getThreadPoolSize());
        int id_counter = 0;
        boolean menu = true;
        while (menu) {
            System.out.println("✶✶✶✶✶✶✶✶✶✶✶✶✶✶ GRUPO 11 ✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶");
            System.out.println("1. Choose a client and send a message");
            System.out.println("2. Create new clients");
            System.out.println("3. End a client's life");
            System.out.println("4. Genocide");
            System.out.println("5. Exit");
            System.out.println("\nThere are " + clients.size() + " clients active right now");
            System.out.println("\nChoose an option:");
            int i = in.nextInt();
            switch (i) {
                case 1:
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now, please create some.\n");
                        break;
                    }
                    System.out.println("\nFrom which client do you want to send the message to:");
                    int m = in.nextInt();
                    if (clients.containsKey(m)) {
                        clients.get(m).sendMessage();
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
                    System.out.println("\nInput the client's id that u want to kill:");
                    int clientId = in.nextInt();
                    if (clients.containsKey(clientId)) {
                        ClientThread client = clients.get(clientId);

                        client.setImDone(true);//Vai avisar a thread para terminar
                        client.WriteLog("", 2); //
                        clients.remove(clientId);
                    } else {
                        System.out.println("\nThat client doesn't exist. Try another one.\n");
                    }
                    break;
                case 4:
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now.\n");
                        break;
                    }
                    for (int l = 0; l < clients.size(); l++) {  //L is the id of the thread
                        if(clients.get(l) != null){
                            clients.get(l).WriteLog("",2);
                            clients.get(l).setImDone(true); //Avisam para terminar estas thread
                        }else{
                            continue;
                        }
                    }
                    clients.clear();//Removes the elements that holded the threads
                case 5:
                    menu = false;
                    executor.shutdown();
                    break;
            }
        }
    }

    private static int getThreadPoolSize (){
        int threadpool_size = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("server/Server.config"));
            threadpool_size = Integer.parseInt(br.readLine());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return threadpool_size;
    }
}
