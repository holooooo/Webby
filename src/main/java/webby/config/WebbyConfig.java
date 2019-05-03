package webby.config;

import lombok.Data;

import java.nio.charset.Charset;
import java.util.Properties;

/**
 * program: webby
 * description: webby的配置类
 * author: Makise
 * create: 2019-04-13 17:51
 **/
public class WebbyConfig {
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
        config.setAppName(properties.getOrDefault("webby.app.name", "webby").toString());
        config.setPort(Integer.parseInt(properties.getOrDefault("webby.port", 8888).toString()));
        config.setCharset(Charset.forName(String.valueOf(properties.getOrDefault("webby.charset", "UTF-8"))));
        config.setPackageName(clazz.getPackage().getName());
        config.setDebug(Boolean.valueOf(properties.getOrDefault("webby.debug", false).toString()));
    }

    public static Config getConfig(){
        return config;
    }
}
