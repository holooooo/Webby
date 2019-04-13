package shitty.config;

import lombok.Data;

import java.nio.charset.Charset;

/**
 * program: shitty
 * description: shitty的配置类
 * author: Makise
 * create: 2019-04-13 17:51
 **/
public class ShittyConfig {
    private static Config config;

    @Data
    public static class Config{
        //端口号默认为8888
        private int port = 8888;
        //字符编码默认为utf-8
        private Charset charset = Charset.forName("UTF-8");
        //项目包名
        private String packageName;
    }

    public static Config getConfig(){
        return config;
    }
}
