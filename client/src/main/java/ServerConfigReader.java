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

    default int getServerCapacity() throws IOException {
        return Integer.parseInt(getProperties().getProperty("server_capacity"));
    }

    static int getQueueCapacity() throws IOException {
        return Integer.parseInt(getProperties().getProperty("queue_capacity"));
    }

    static int getThreadWorkers() throws IOException {
        return Integer.parseInt(getProperties().getProperty("n_thread_workers"));
    }
}


