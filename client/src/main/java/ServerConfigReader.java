import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public interface ServerConfigReader {



    static Properties getProperties() throws IOException {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream("C:\\Users\\André\\IdeaProjects\\Grupo11-P1\\server\\Server.config");
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
            return properties;
        }



     static int getVariable(String variable) throws IOException {
        return Integer.parseInt(getProperties().getProperty(variable));
    }
     static void setVariable(String variable, String value) throws IOException {
        Properties properties = getProperties();
        properties.setProperty(variable, value);
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\André\\IdeaProjects\\Grupo11-P1\\server\\Server.config");
        try {
            properties.store(outputStream,variable+"="+value);
        } finally {
            outputStream.close();
        }
    }

}
