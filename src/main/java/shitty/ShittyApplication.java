package shitty;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.server.HttpServer;

/**
 * program: shitty
 * description: 程序的入口，新的项目应该继承这个类，并通过这个类的run方法启动项目
 * author: Makise
 * create: 2019-04-02 22:31
 **/
@Data
public class ShittyApplication {
    private static final Logger logger = LoggerFactory.getLogger(ShittyApplication.class);

    /**
     * Description: 修改配置，启动服务
     * Author: Makise
     * Date: 2019/4/3
     */
    public static void run(Class<?> projectClass){
        logger.info("Shitty is setting up ...");
        long startTime = System.currentTimeMillis();
        //todo 读取设置


        //todo 扫描注解

        HttpServer.run();


        long endTime = System.currentTimeMillis();
        logger.info("Shitty has set up, it take %b millisecond", (endTime - startTime));
    }

}
