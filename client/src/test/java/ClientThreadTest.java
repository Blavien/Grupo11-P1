import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;


class ClientThreadTest {
/*
    private ClientThread clientThread;
    @BeforeEach
    void setup() throws IOException {
        this.clientThread = new ClientThread(8888, 80 , 200 );
    }

    @Test
    @DisplayName("ID getter")
    void getID(){

        assertEquals(80,clientThread.getID());
    }

    @Test
    @DisplayName("amIDone setter")
    void setImDone(){


        assertEquals(true,clientThread.setImDone(true));
    }

    @Test
    @DisplayName("amIDone getter")
    void stopLiving() {
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
    @Test
    @DisplayName("Test writeLog method and its possibilities")
    void writeLog() throws IOException {
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


    @Test
    @DisplayName("test of the spamming messages method")
    void spamMessages() throws IOException {
        clientThread.spamMessages();
        Timestamp timestamp = clientThread.getTimeStamp();
        assertEquals(timestamp + " - Action : MESSAGE - CLIENT ID:"+clientThread.getID()+" - "+ clientThread.getRandNum(),leitorUltimaLinha());
    }
    @Test
    @DisplayName("Testing getter and setter of the RandNum variable")
    void setGetRandNum() throws IOException {
        clientThread.setRandNum(4);
        assertEquals(4,clientThread.getRandNum());
        clientThread.setRandNum(7);
        assertEquals(7,clientThread.getRandNum());
    }







*/
}