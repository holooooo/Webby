package webby;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.config.PropertiesReader;
import webby.config.WebbyConfig;
import webby.config.WebbyLogConfig;
import webby.server.HttpServer;
import webby.web.AnnotationScanner;

/**
 * program: webby
 * description: 程序的入口，新的项目应该继承这个类，并通过这个类的run方法启动项目
 * author: Makise
 * create: 2019-04-02 22:31
 **/
@Data
public class WebbyApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebbyApplication.class);

    /**
     * Description: 修改配置，启动服务
     * Author: Makise
     * Date: 2019/4/3
     */
    public static void run(Class<?> projectClass) throws Exception {
        logger.info("webby is setting up ...");
        long startTime = System.currentTimeMillis();

        logger.info("webby is reading properties");
        PropertiesReader propertiesReader = new PropertiesReader(projectClass);
        propertiesReader.readProperties();
        WebbyConfig.loadProperties(propertiesReader);
        WebbyLogConfig.loadProperties(propertiesReader);
        logger.info("webby has read all properties");

        logger.info("webby is scanning annotation");
        AnnotationScanner ras = new AnnotationScanner();
        ras.scan();
        logger.info("webby has scanned all annotation");


        HttpServer.run(startTime);
    }

}
