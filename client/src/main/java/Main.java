import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import java.util.*;

/**
 * Main Class that provides a menu to control the client threads
 */
public class Main implements ServerConfigReader {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ConcurrentHashMap<Integer, ClientThread> clients = new ConcurrentHashMap<>();
    private static final Scanner in = new Scanner(System.in);

    /**
     * The main is the menu of the client, it is where you can choose the options that lets you interact with the
     * server
     * @param args It do be an argument
     */
    public static void main ( String[] args ) throws IOException {
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
            System.out.println("5. Remove one word from the filter");
            System.out.println("\nClients alive: "+ clients.size());
            System.out.println("\n\nChoose an option:");
            int i = in.nextInt();
            switch (i) {
                case 1:
                    if (clients.isEmpty()) {
                        System.out.println("\nWe don't have any active clients right now, please create some.\n");
                        break;
                    }
                    System.out.println("\nFrom which client do you want to send the message to:");
                    String msg = " | ";
                    for (int l = 0; l < clients.size(); l++) {  //L is the id of the thread
                        if (clients.containsKey(clients.get(l).getID())) {
                            msg += clients.get(l).getID() + " | ";
                        }
                    }
                    System.out.println("Available users: " + msg);
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
                    System.out.println("\nInput the client's id that u want to kill:\n");
                    System.out.println("Available users: " + message2);
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
                    break;
                case 5:
                    removePalavrasFiltro();
            }
        }
    }

    public static void removePalavrasFiltro(){
        try {
            // Lê o conteúdo do arquivo "filtro.txt" e guarda as palavras em um array
            Scanner scanner = new Scanner(new File("server/filtro.txt"));
            String content = scanner.useDelimiter("\\Z").next();
            String[] words = content.split("\\s+");

            // Pede ao usuário que insira uma palavra
            Scanner input = new Scanner(System.in);
            System.out.println("Insira uma palavra:");
            String wordToRemove = input.nextLine();

            // Remove as palavras do array que são iguais à palavra fornecida pelo usuário
            List<String> filteredWords = new ArrayList<String>();
            for (String word : words) {
                if (!word.equals(wordToRemove)) {
                    filteredWords.add(word);
                }
            }

            // Reescreve as palavras restantes no arquivo "filtro.txt"
            FileWriter writer = new FileWriter("server/filtro.txt");
            for (String word : filteredWords) {
                writer.write(word + " ");
            }
            writer.close();

            System.out.println("Operação concluída com sucesso.");
        } catch (FileNotFoundException e) {
            System.out.println("O arquivo 'filtro.txt' não foi encontrado.");
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao tentar escrever no arquivo 'filtro.txt'.");
        }
    }
    }

