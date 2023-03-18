import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main ( String[] args ) {
        ExecutorService executor = Executors.newFixedThreadPool(getThreadPoolSize()); //

        ArrayList<ClientThread> clients = new ArrayList<ClientThread>(); //Saves the reference to the threads
        Scanner in = new Scanner(System.in);
        int i,n,m;
        int id_counter = 0;
        boolean menu = true;
        while(menu) {
            System.out.println("✶✶✶✶✶✶✶✶✶✶✶✶✶✶ GRUPO 11 ✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶");
            System.out.println("1. Choose a client and send a message");
            System.out.println("2. Create new clients");
            System.out.println("3. End a client's life");
            System.out.println("4. Genocide");
            System.out.println("5. Exit");
            System.out.println("\nThere are " + clients.size() + " clients active right now");
            System.out.println("\nChoose an option:");
            i = in.nextInt();
            switch (i) {
                case 1 -> {
                    boolean clientExists = false;
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now, please create some.\n");
                        break;
                    }
                    System.out.println("\nFrom which client do you want to send the message to:");
                    m = in.nextInt();
                    for (ClientThread clientThread : clients) {
                        if (clientThread.getID() == m) {
                            clientExists = true;
                            break;
                        }
                    }
                    if (clientExists) {
                        clients.get(m).sendMessage();
                    } else {
                        System.out.println("\nThat client isn't available right now. Try another one.\n");
                    }
                }
                case 2 -> {
                    System.out.println("\nHow many clients do you want created:");
                    n = in.nextInt();
                    for (int k = 0; k < n; k++) {
                        clients.add(new ClientThread(8888, id_counter + k, 2000));
                        clients.get(id_counter + k).WriteLog("", 4); //Enters wait
                    }
                    id_counter = id_counter + n;
                    //Coloca todas as threads no executor -- Depois tratamos no run
                    for (ClientThread client : clients) {
                        executor.submit(client);
                    }
                }
                case 3 -> {
                    System.out.println("\nInput the client's id that u want to kill:");
                    m = in.nextInt();
                    for (int l = 0; l < clients.size(); l++) {
                        try {
                            if (clients.get(l).getID() == m) {
                                clients.get(l).WriteLog("", 2);
                                clients.get(l).join();
                                //clients.remove(l);
                                break;
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                case 4 -> {
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now, please create some.\n");
                        break;
                    }
                    for (ClientThread client : clients) {
                        try {
                            client.WriteLog("", 2);
                            client.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    clients.clear();//Removes the elements that holded the threads
                }
                case 5 -> {
                    menu = false;
                    executor.shutdown();
                    break;
                }
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
