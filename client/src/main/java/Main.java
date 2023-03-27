import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main implements ServerConfigReader {
    private static final ReentrantLock lock = new ReentrantLock();

    private static final String file = "C:\\Users\\André\\IdeaProjects\\Grupo11-P1\\server\\filtro.txt";
    private static final ConcurrentHashMap<Integer, ClientThread> clients = new ConcurrentHashMap<>();
    private static final Scanner in = new Scanner(System.in);

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
            System.out.println("5. Change the file Server.config");
            System.out.println("6. Add Words from filtro.txt");
            System.out.println("7. Remove Words from filtro.txt");
            System.out.println("Change ");
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
                    System.out.println("\nInput the client's id that u want to kill:");
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

                case 5:
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
                case 6:
                  break;
            }
        }
    }
}
