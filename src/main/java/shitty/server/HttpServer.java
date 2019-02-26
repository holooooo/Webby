package shitty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * program: shitty
 * description: Http服务器
 * author: Makise
 * create: 2019-02-26 12:14
 **/
public class HttpServer {
    private static final int port = 8888;
    private ServerBootstrap serverBootstrap;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private void open() throws InterruptedException{
        serverBootstrap  = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG,1024)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new HttpInitializer());

        Channel channel = serverBootstrap.bind(port).sync().channel();
        System.out.println("开始监听");
        channel.closeFuture().sync();
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        try {
            httpServer.open();
        }catch (InterruptedException e){
            httpServer.close();
        }
    }

}
