package shitty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import shitty.config.ShittyConfig;


/**
 * program: shitty
 * description:对收到的包进行处理
 * author: Makise
 * create: 2019-02-26 12:12
 **/
public class HttpInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 获取通道
        ChannelPipeline p = channel.pipeline();
        p.addLast("http-decoder", new HttpRequestDecoder());
        //把编码设置成用户所定义的编码
        p.addLast(new StringDecoder(ShittyConfig.getConfig().getCharset()));
        //开启http聚合
        p.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
        p.addLast("http-encoder",new HttpResponseEncoder());
        // 新增ChunkedHandler，主要作用是支持异步发送大的码流（例如大文件传输），但是不占用过多的内存，防止发生java内存溢出错误
        p.addLast("http-chunked",new ChunkedWriteHandler());
        // 添加自定义的handler组件
        p.addLast(new HttpHandler());
        //添加gzip压缩
        p.addLast("compressor", new HttpContentCompressor());
    }
}