package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public static Properties loadFile(File file) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
            // Properties loaded successfully
        } catch (IOException e) {
            e.printStackTrace();
            // Error occurred while loading properties
        }
        return properties;
    }
}