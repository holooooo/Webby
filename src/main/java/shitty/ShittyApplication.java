package shitty;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.config.ShittyConfig;
import shitty.config.ShittyLogConfig;
import shitty.server.HttpServer;
import shitty.config.PropertiesReader;
import shitty.web.RouteAnnotationScanner;

import java.util.Properties;

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
    public static void run(Class<?> projectClass) throws Exception {
        logger.info("Shitty is setting up ...");
        long startTime = System.currentTimeMillis();

        logger.info("Shitty is reading properties");
        Properties properties = PropertiesReader.readProperties(projectClass);
        ShittyConfig.loadProperties(properties, projectClass);
        ShittyLogConfig.loadProperties(properties);
        logger.info("Shitty has read all properties");

        //todo 扫描注解
        RouteAnnotationScanner ras = new RouteAnnotationScanner();
        ras.scan();

        long endTime = System.currentTimeMillis();
        logger.info("Shitty has set up, it take {} millisecond", (endTime - startTime));
        HttpServer.run();
    }

}
