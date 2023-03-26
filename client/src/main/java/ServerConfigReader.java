import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public interface ServerConfigReader {



    static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream("C:\\Users\\André\\IdeaProjects\\Grupo11-P1\\server\\Server.config");
            properties.load(inputStream);
            inputStream.close();
        return properties;
    }

     static int getVariable(String variable) throws IOException {
        return Integer.parseInt(getProperties().getProperty(variable));
    }


}
