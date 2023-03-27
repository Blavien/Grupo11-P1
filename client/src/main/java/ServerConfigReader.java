import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.FileOutputStream;

public interface ServerConfigReader {
    /**
     * This method return the properties , is necessary to read the file
     * @return properties
     * @throws IOException
     */
    static Properties getProperties() throws IOException {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream("C:\\Users\\RP\\IdeaProjects\\Grupo11-P1\\server\\Server.config");

                properties.load(inputStream);

                inputStream.close();

            return properties;
        }

    /**
     * This method return the  value of the variable to be read
     * @param variable
     * @return Int
     * @throws IOException
     */

     static int getVariable(String variable) throws IOException {
        return Integer.parseInt(getProperties().getProperty(variable));
    }

    /**
     * This method is use to change  the value of the variable
     * @param variable
     * @param value
     * @throws IOException
     */
     static void setVariable(String variable, String value) throws IOException {
        Properties properties = getProperties();
        properties.setProperty(variable, value);
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\RP\\IdeaProjects\\Grupo11-P1\\server\\Server.config");
        try {
            properties.store(outputStream,value);
        } finally {
            outputStream.close();
        }
    }


}
