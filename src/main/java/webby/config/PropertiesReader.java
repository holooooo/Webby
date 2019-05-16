package webby.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * program: webby
 * description: 配置文件读取器
 * author: Makise
 * create: 2019-04-13 18:20
 **/
public class PropertiesReader {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesReader.class);
    private Properties properties = new Properties();
    private Class<?> clazz;

    public PropertiesReader(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void readProperties() {
        try {
            InputStream in = new BufferedInputStream(clazz.getResourceAsStream("/webby.properties"));
            properties.load(in);
        }catch (Exception e){
            logger.info("检测到没有配置文件，使用默认配置");
        }

    }

    public Class<?> getClazz(){
        return clazz;
    }

    public Object getOrDefault(String key, Object defaultValue){
        if (properties == null){
            return defaultValue;
        }else {
            return properties.getOrDefault(key, defaultValue);
        }
    }

    public Object get(String key){
        return getOrDefault(key, null);
    }
}
