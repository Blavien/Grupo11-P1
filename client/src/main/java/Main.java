import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main ( String[] args ) {
        ExecutorService executor = Executors.newFixedThreadPool(getThreadPoolSize()); //

        ArrayList<ClientThread> clients = new ArrayList<ClientThread>(); //Saves the reference to the threads
        int aliveThreads = 0;
        for (ClientThread client : clients) {
            if(!(client.isInterrupted())){
            aliveThreads++;
            }
        }

        Scanner in = new Scanner(System.in);
        int i,n,m;
        int id_counter = 0;
        int index = 0;
        boolean menu = true;
        while(menu) {
            System.out.println("Threads still alive: " + aliveThreads);
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
                    String clientList = "";
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now, please create some.\n");
                        break;
                    }
                    clientList += "Available clients: \n";
                    for (ClientThread clientThread : clients) {
                        clientList += "| ";
                        clientList += clientThread.getID() + " ";

                    }
                    System.out.println("\n" + clientList + "\n");
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
                    }
                    if((clients.size()-1)<m){
                        System.out.println("\nThat client isn't available right now. Try another one.\n");
                    }

                }
                case 2 -> {
                    System.out.println("\nHow many clients do you want created:");
                    n = in.nextInt();
                    for (int k = 0; k < n; k++) {
                        clients.add(new ClientThread(8888, id_counter + k, 2000));
                        clients.get(index + k).start(); //Starts the threads
                        clients.get(index + k).WriteLog("", 1); //Writing on the server.log
                    }
                    id_counter = id_counter + n;
                    index +=n;
                }
                case 3 -> {
                    boolean clientExists2 = false;
                    System.out.println("\nInput the client's id that u want to kill:");
                    m = in.nextInt();
                    for (ClientThread clientThread : clients) {
                        if (clientThread.getID() == m) {
                            clientExists2 = true;

                        }
                    }

                    if(clientExists2){
                        for (int l = 0; l < clients.size(); l++) {
                            try {
                                if (clients.get(l).getID() == m) {
                                    clients.get(l).WriteLog("", 2);
                                    clients.get(l).join();
                                    clients.remove(l);
                                    index--;
                                    break;
                                }

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                     else {
                        System.out.println("\nThe client you tried to delete isn't created, please try to delete an existing client\n");
                        break;
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
