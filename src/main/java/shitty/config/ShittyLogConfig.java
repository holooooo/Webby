package shitty.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * program: shitty
 * description: 日志配置器
 * author: Makise
 * create: 2019-04-13 18:12
 **/
public class ShittyLogConfig {
    private static LogConfig config;
    private static Date date = new Date();
    private static SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Description: 读取配置，并给logback进行配置
     * Param: [properties]
     * return: void
     * Author: Makise
     * Date: 2019/4/13
     */
    public static void loadProperties(Properties properties){
        config = new LogConfig();
        //从配置文件中读取配置
        config.setLevel(String.valueOf(properties.getOrDefault("shitty.log.level", "INFO")));
        config.setConsolePattern(String.valueOf(properties.get("shitty.log.append.console.pattern")));
        config.setFileName(String.valueOf(properties.get("shitty.log.append.file.name")));
        config.setFilePattern(String.valueOf(properties.get("shitty.log.append.file.pattern")));


    }

    private static void startLog(){
        //设置输出等级
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger("shitty");

        //设置输出在控制台的格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setPattern(formart(config.getConsolePattern()));
        encoder.setImmediateFlush(true);
        encoder.setContext(lc);

        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
        ca.setContext(lc);
        ca.setName("console");
        ca.setEncoder(encoder);

        encoder.start();
        ca.start();
        logger.addAppender(ca);


        //如果要在文件中输出，就设置输出器
        if (StringUtils.isBlank(config.getFileName())){
            return;
        }else {
            encoder.setPattern(formart(config.getFilePattern()));
            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setEncoder(encoder);
            fileAppender.setFile(formart(config.getFileName()));
            fileAppender.setName("file");
            fileAppender.setAppend(false);
            fileAppender.setContext(lc);

            encoder.start();
            fileAppender.start();
            logger.addAppender(fileAppender);
        }
        logger.setLevel(Level.toLevel(config.getLevel()));
        lc.getLogger("io.netty").setLevel(Level.toLevel(config.getLevel()));
    }

    private static String formart(String s){
        s = s.replace("{app.name}", ShittyConfig.getConfig().getAppName());
        s = s.replace("{date}", spf.format(date));
        return s;
    }

    @Data
    private static class LogConfig {
        private String level;
        private String consolePattern;
        private String fileName;
        private String filePattern;
    }

}
