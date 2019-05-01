package shitty.config;

import lombok.Data;

import java.nio.charset.Charset;
import java.util.Properties;

/**
 * program: shitty
 * description: shitty的配置类
 * author: Makise
 * create: 2019-04-13 17:51
 **/
public class ShittyConfig {
    private static Config config = new Config();

    @Data
    public static class Config{
        private String appName;
        //端口号默认为8888
        private int port;
        //字符编码默认为utf-8
        private Charset charset;
        //项目包名
        private String packageName;
        private boolean debug;
    }

    /**
     * Description: 读取配置
     * Param: [properties]
     * return: void
     * Author: Makise
     * Date: 2019/4/13
     */
    public static void loadProperties(Properties properties, Class<?> clazz){
        config.setAppName(properties.getOrDefault("shitty.app.name", "Shitty").toString());
        config.setPort(Integer.parseInt(properties.getOrDefault("shitty.port", 8888).toString()));
        config.setCharset(Charset.forName(String.valueOf(properties.getOrDefault("shitty.charset", "UTF-8"))));
        config.setPackageName(clazz.getPackage().getName());
        config.setDebug(Boolean.valueOf(properties.getOrDefault("shitty.debug", false).toString()));
    }

    public static Config getConfig(){
        return config;
    }
}
