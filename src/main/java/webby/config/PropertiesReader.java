package webby.config;

import java.io.*;
import java.util.Properties;

/**
 * program: webby
 * description: 配置文件读取器
 * author: Makise
 * create: 2019-04-13 18:20
 **/
public class PropertiesReader {
    private static Properties properties = new Properties();

    public static Properties readProperties(Class<?> clazz) throws IOException {
        InputStream in = new BufferedInputStream(clazz.getResourceAsStream("/webby.properties"));
        properties.load(in);
        return properties;
    }

    public static Properties getProperties() {
        return properties;
    }
}
