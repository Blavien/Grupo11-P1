import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.List;
import java.util.Arrays;
public class FiltroThread extends Thread{
    public FiltroThread (){

    }
    public static void filtragem() {



    }
    public static ArrayList<String> lerArquivoDePalavras() {
        ArrayList<String> palavras = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(new File("server/Server.log"));
            while (scanner.hasNext()) {
                String linha = scanner.nextLine();
                String[] palavrasLinha = linha.split("\s+");
                for (String palavra : palavrasLinha) {
                    palavras.add(palavra);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado.");
        }
        return palavras;
    }

    static ArrayList<String> words = lerArquivoDePalavras();

    public static String removePalavrasRepetidas(String message) {
        String[] messageArray = message.split(" ");
        List<String> messageList = new ArrayList<>(Arrays.asList(messageArray));
        messageList.removeAll(words);
        String novaMensagem = String.join(" ", messageList);

        return novaMensagem;
    }








}