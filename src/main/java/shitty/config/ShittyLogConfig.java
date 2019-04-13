package shitty.config;

import ch.qos.logback.classic.Level;

/**
 * program: shitty
 * description: 日志配置器
 * author: Makise
 * create: 2019-04-13 18:12
 **/
public class ShittyLogConfig {
    //默认的日志输出级别
    private Level level = Level.INFO;
    //默认的日志输出目标
    private String appender = "console";
}
