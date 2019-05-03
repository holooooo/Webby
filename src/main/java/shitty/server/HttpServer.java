package shitty.server;

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
import shitty.config.ShittyConfig;

/**
 * program: shitty
 * description: Http服务器
 * author: Makise
 * create: 2019-02-26 12:14
 *
 * @author Amadeus*/
public class HttpServer {
    private static HttpServer httpServer;
    private static final int PORT = ShittyConfig.getConfig().getPort();
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static long startTime;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private void open() throws InterruptedException{
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG,1024)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new HttpInitializer());

        Channel channel = serverBootstrap.bind(PORT).sync().channel();
        long endTime = System.currentTimeMillis();
        logger.info("Shitty has set up, it take {} millisecond", (endTime - startTime));
        if (!ShittyConfig.getConfig().isDebug()){
            logger.info(egg);
        }
        channel.closeFuture().sync();
    }

    public void close() {
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
    public static void run(long stTime){
        startTime = stTime;
        httpServer = new HttpServer();
        try {
            httpServer.open();
        }catch (InterruptedException e){
            httpServer.close();
        }
    }

    /**
     * Description: 停止服务器
     * Param: []
     * return: void
     * Author: Makise
     * Date: 2019/4/3
     */
    public static void stop(){
        httpServer.close();
    }


    private static final String egg = "\n////////////////////////////////////////////////////////////////////\n" +
            "//\t\t\t\t\t\t\t_ooOoo_\t\t\t\t\t\t\t\t  //\n" +
            "//\t\t\t\t\t\t   o8888888o\t\t\t\t\t\t\t  //\t\n" +
            "//\t\t\t\t\t\t   88\" . \"88\t\t\t\t\t\t\t  //\t\n" +
            "//\t\t\t\t\t\t   (| ^_^ |)\t\t\t\t\t\t\t  //\t\n" +
            "//\t\t\t\t\t\t   O\\  =  /O\t\t\t\t\t\t\t  //\n" +
            "//\t\t\t\t\t\t____/`---'\\____\t\t\t\t\t\t\t  //\t\t\t\t\t\t\n" +
            "//\t\t\t\t\t  .'  \\\\|     |//  `.\t\t\t\t\t\t  //\n" +
            "//\t\t\t\t\t /  \\\\|||  :  |||//  \\\t\t\t\t\t\t  //\t\n" +
            "//\t\t\t\t    /  _||||| -:- |||||-  \\\t\t\t\t\t\t  //\n" +
            "//\t\t\t\t    |   | \\\\\\  -  /// |   |\t\t\t\t\t\t  //\n" +
            "//\t\t\t\t\t| \\_|  ''\\---/''  |   |\t\t\t\t\t\t  //\t\t\n" +
            "//\t\t\t\t\t\\  .-\\__  `-`  ___/-. /\t\t\t\t\t\t  //\t\t\n" +
            "//\t\t\t\t  ___`. .'  /--.--\\  `. . ___\t\t\t\t\t  //\t\n" +
            "//\t\t\t\t.\"\" '<  `.___\\_<|>_/___.'  >'\"\".\t\t\t\t  //\n" +
            "//\t\t\t  | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |\t\t\t\t  //\t\n" +
            "//\t\t\t  \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /                 //\n" +
            "//\t\t========`-.____`-.___\\_____/___.-`____.-'========\t\t  //\t\n" +
            "//\t\t\t\t             `=---='                              //\n" +
            "//\t\t^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //\n" +
            "//                     佛祖保佑       永无BUG\t\t\t\t\t\t  //\n" +
            "////////////////////////////////////////////////////////////////////";
}
