import org.junit.jupiter.api.DisplayName;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientThreadTest {

    @org.junit.jupiter.api.Test

    void getID() {
        ClientThread clientThread = new ClientThread(8888, 15 , 10 );
        assertEquals(15,clientThread.getID());
    }

    @org.junit.jupiter.api.Test
    void setImDone() {
        ClientThread clientThread = new ClientThread(8888, 1 , 10 );

        assertEquals(true,clientThread.setImDone(true));
    }

    @org.junit.jupiter.api.Test
    void stopLiving() {
        ClientThread clientThread = new ClientThread(8888, 2 , 10 );
        assertEquals(false,clientThread.stopLiving());
    }

    public String leitorUltimaLinha() throws IOException {
        BufferedReader leitor = new BufferedReader(new FileReader("C:\\Users\\RP\\IdeaProjects\\Grupo11-P1\\server\\Server.log"));
        String linhaAtual = leitor.readLine();
        String ultimaLinha = null;
        while (linhaAtual != null) {
            ultimaLinha = linhaAtual;
            linhaAtual = leitor.readLine();
        }
        leitor.close();
        return ultimaLinha;
    }
    @org.junit.jupiter.api.Test
    @DisplayName("Test writeLog method and its possibilities")
    void writeLog() throws IOException {
        ClientThread clientThread = new ClientThread(8888, 3 , 10 );
        clientThread.WriteLog("bla",1);
        Timestamp timestamp = clientThread.getTimeStamp();
        assertEquals(timestamp + " - Action : CONNECTED - CLIENT ID:" + clientThread.getID() + "\n",leitorUltimaLinha() + "\n");
        clientThread.WriteLog("bla",2);
        Timestamp timestamp2 = clientThread.getTimeStamp();
        assertEquals(timestamp2 + " - Action : DISCONNECTED - CLIENT ID:" + clientThread.getID() + "\n",leitorUltimaLinha() + "\n");
        clientThread.WriteLog("bla",3);
        Timestamp timestamp3 = clientThread.getTimeStamp();
        assertEquals(timestamp3 + " - Action : MESSAGE - CLIENT ID:" + clientThread.getID() + " - " + "bla" + "\n",leitorUltimaLinha() + "\n");
        clientThread.WriteLog("bla",4);
        Timestamp timestamp4 = clientThread.getTimeStamp();
        assertEquals(timestamp4 + " - Action : WAITING - CLIENT ID:" + clientThread.getID() + "\n",leitorUltimaLinha() + "\n");
    }
    @org.junit.jupiter.api.Test
    void sendMessage() {
        ClientThread clientThread = new ClientThread(8888, 6 , 10 );
        clientThread.sendMessage();
    }

    @org.junit.jupiter.api.Test
    void spamMessages() throws IOException {
        ClientThread clientThread = new ClientThread(8888, 7 , 10 );
        clientThread.spamMessages();
        Timestamp timestamp = clientThread.getTimeStamp();
        assertEquals(timestamp + " - Action : MESSAGE - CLIENT ID:"+clientThread.getID()+" - "+ clientThread.getRandNum(),leitorUltimaLinha());
    }
    @org.junit.jupiter.api.Test
    void setGetRandNum() {
        ClientThread clientThread = new ClientThread(8888, 8 , 10 );
        clientThread.setRandNum(4);
        assertEquals(4,clientThread.getRandNum());
        clientThread.setRandNum(7);
        assertEquals(7,clientThread.getRandNum());
    }








}