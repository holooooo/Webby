package shitty;

import lombok.Data;
import shitty.server.HttpServer;

import java.nio.charset.Charset;

/**
 * program: shitty
 * description: 程序的入口，新的项目应该继承这个类，并通过这个类的run方法启动项目
 * author: Makise
 * create: 2019-04-02 22:31
 **/
@Data
public class ShittyApplication {
    public final static ShittyConfig config = new ShittyConfig();

    /**
     * Description: 修改配置，启动服务
     * Param:
     * return:
     * Author: Makise
     * Date: 2019/4/3
     */
    public static void run(Class<?> projectClass, int port, String charset){
        //修改配置
        config.setPackageName(projectClass.getPackage().getName());
        config.setPort(port);
        config.setStringDecoder(Charset.forName(charset));

        //todo 扫描注解

        HttpServer.run();
    }

    public static void run(Class<?> projectClass, String charset){
        run(projectClass, 8888, charset);
    }


    public static void run(Class<?> projectClass, int port){
        run(projectClass, port, "UTF-8");
    }

    public static void run(Class<?> projectClass){
        run(projectClass, 8888);
    }


    @Data
    public static class ShittyConfig{
        //端口号默认为8888
        private int port;
        //字符编码默认为utf-8
        private Charset stringDecoder;
        //项目包名
        private String packageName;

    }
}
