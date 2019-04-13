package shitty.utils;

import java.io.*;
import java.util.Properties;

/**
 * program: shitty
 * description: 配置文件读取器
 * author: Makise
 * create: 2019-04-13 18:20
 **/
public class PropertiesReader {
    private static Properties properties = new Properties();

    public static Properties readProperties(Class<?> clazz) throws IOException {
        InputStream in = new BufferedInputStream(clazz.getResourceAsStream("/shitty.properties"));
        properties.load(in);
        return properties;
    }

    public static Properties getProperties() {
        return properties;
    }
}
