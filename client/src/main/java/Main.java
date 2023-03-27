import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.Files;

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
     *
     * @param args It do be an argument
     */
    public static void main(String[] args) throws IOException {
        int threadWorkers = ServerConfigReader.getVariable("n_thread_workers");
        ExecutorService executor = Executors.newFixedThreadPool(threadWorkers); //penso que deve ter o mesmo tamanho que o server_capacity
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
            System.out.println("5. Remove words of the filter");
            System.out.println("6. Add words to the filter");
            System.out.println("7. Change the file Server.config");
            System.out.println("\nClients alive: " + clients.size());
            System.out.println("\n\nChoose an option:");
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
                        if (clients.get(m).isConnected()) {
                            clients.get(m).sendMessage();
                        } else {
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
                        if (clients.get(clientId).isConnected()) {
                            ClientThread client = clients.get(clientId);
                            client.setImDone(true);//Vai avisar a thread para terminar
                            clients.remove(clientId); //remove deste array
                        } else {
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
                        if (clients.get(l) != null) {
                            clients.get(l).setImDone(true); //Avisam para terminar estas thread
                        } else {
                            continue;
                        }
                    }
                    clients.clear();//Removes the elements that hold the threads
                    break;
                case 5:
                    removePalavrasFiltro();
                    break;
                case 6:
                    adicionaPalavrasFiltro();
                    break;
                case 7:
                    Scanner scanner = new Scanner(System.in);

                    while (true) {
                        System.out.println("Digite o nome da variável que deseja alterar: ");
                        System.out.println("Para sair escreva:sair ");
                        System.out.print(":");
                        String variable = scanner.nextLine();
                        if (variable.equals("FINAL_MAX_CLIENTS")) {
                            System.out.println("A variável FINAL_MAX_CLIENTS não pode ser alterada.");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                        if (variable.equals("n_thread_workers") || variable.equals("queue_capacity")) {
                            System.out.print("\nDigite um novo valor inteiro " + variable + ": ");


                            while (true) {
                                if (scanner.hasNextInt()) {
                                    int value = scanner.nextInt();
                                    try {
                                        String valueS = Integer.toString(value);
                                        ServerConfigReader.setVariable(variable, valueS);
                                        System.out.println("O valor da variável " + variable + " foi atualizado para " + value + " com sucesso!");
                                    } catch (IOException e) {
                                        System.out.println("ERROR " + e.getMessage());
                                    }
                                    break;
                                } else {
                                    System.out.println("Insira um numero ");
                                    scanner.next();
                                }

                            }
                            break;
                        }
                        //variable = scanner.nextLine().toLowerCase().replaceAll("\\s", "");
                        if(variable.equals("sair")){

                            System.out.println("\n Saiu desta opcção");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                        else {
                            System.out.println("\n Insira um nome valido ");

                        }
                    }
                    break;

            }
        }
    }

    public static void removePalavrasFiltro() {
        try {
            // Lê o conteúdo do arquivo "filtro.txt" e guarda as palavras em um array
            Scanner scanner = new Scanner(new File("server/filtro.txt"));
            String content = scanner.useDelimiter("\\Z").next();
            String[] words = content.split("\\s+");

            // Pede ao usuário que insira as palavras a serem filtradas
            Scanner input = new Scanner(System.in);
            System.out.println("Insert the words you want to remove separated by spaces (example: dog cat meow hello)");
            String wordsToRemove = input.nextLine();

            // Separa as palavras fornecidas pelo usuário e remove as palavras do array que são iguais a elas
            String[] wordsToRemoveArray = wordsToRemove.split("\\s+");
            List<String> filteredWords = new ArrayList<String>();
            for (String word : words) {
                if (!Arrays.asList(wordsToRemoveArray).contains(word)) {
                    filteredWords.add(word);
                }
            }

            // Reescreve as palavras restantes no arquivo "filtro.txt"
            FileWriter writer = new FileWriter("server/filtro.txt");
            for (String word : filteredWords) {
                writer.write(word + " ");
            }
            writer.close();

            System.out.println("Operation completed.");
        } catch (FileNotFoundException e) {
            System.out.println("filtro.txt's path couldn't be found");
        } catch (IOException e) {
            System.out.println("Error trying to write in the file");
        }
    }

    public static void adicionaPalavrasFiltro() {
        try {
            // Pede ao usuário que insira as palavras a serem adicionadas
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insert the words that you want to add to the filter, they should be separated by spaces (example: dog cat meow): ");
            String input = scanner.nextLine();

            // Lê o conteúdo atual do arquivo "filtro.txt"
            String content = new String(Files.readAllBytes(Paths.get("server/filtro.txt")));

            // Adiciona as novas palavras ao conteúdo existente
            content += " " + input;

            // Grava o novo conteúdo no arquivo "filtro.txt"
            FileWriter writer = new FileWriter("server/filtro.txt");
            writer.write(content);
            writer.close();

            System.out.println("The words were added to the filter with success");
        } catch (IOException e) {
            System.out.println("Error while trying to add words in the filter file");
        }
    }
}






