package webby.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.config.WebbyConfig;

/**
 * program: webby
 * description: Http服务器
 * author: Makise
 * create: 2019-02-26 12:14
 *
 * @author Amadeus
 */
public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static long startTime;

    private static final int PORT = WebbyConfig.getConfig().getPort();

    private static EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static void open() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new HttpInitializer());

        Channel channel = serverBootstrap.bind(PORT).sync().channel();
        long endTime = System.currentTimeMillis();
        logger.info("webby has set up, it takes {} millisecond", (endTime - startTime));
        logger.info(banner);
        channel.closeFuture().sync();
    }

    public static void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * Description: 启动服务器
     * Param: []
     * return: void
     * Author: Makise
     * Date: 2019/4/3
     */
    public static void run(long stTime) {
        startTime = stTime;
        try {
            HttpServer.open();
        } catch (InterruptedException e) {
            HttpServer.close();
        }
    }

    /**
     * Description: 停止服务器
     * Param: []
     * return: void
     * Author: Makise
     * Date: 2019/4/3
     */
    public static void stop() {
        HttpServer.close();
    }


    private static final String banner = "\n" +
            "██╗    ██╗███████╗██████╗ ██████╗ ██╗   ██╗\n" +
            "██║    ██║██╔════╝██╔══██╗██╔══██╗╚██╗ ██╔╝\n" +
            "██║ █╗ ██║█████╗  ██████╔╝██████╔╝ ╚████╔╝ \n" +
            "██║███╗██║██╔══╝  ██╔══██╗██╔══██╗  ╚██╔╝  \n" +
            "╚███╔███╔╝███████╗██████╔╝██████╔╝   ██║   \n" +
            " ╚══╝╚══╝ ╚══════╝╚═════╝ ╚═════╝    ╚═╝   \n";
}
