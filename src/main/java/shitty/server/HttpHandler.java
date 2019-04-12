package shitty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import shitty.web.http.HttpResponseUtil;
import shitty.web.http.HttpStatu;
import shitty.web.TransactionHandler;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * program: shitty
 * description: http逻辑控制器
 * author: Makise
 * create: 2019-02-26 10:35
 **/
public class HttpHandler extends SimpleChannelInboundHandler<Object> {
    //是否是keepalive
    private boolean keepAlive;

    public HttpHandler() {
        super();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpStatu.INTERNAL_SERVER_ERROR.getStatus(),
                    Unpooled.copiedBuffer("Failure: " + HttpStatu.INTERNAL_SERVER_ERROR.getStatus()+ "\r\n", CharsetUtil.UTF_8));
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            //使用ctx对象写出并且刷新到SocketChannel中去 并主动关闭连接(这里是指关闭处理发送数据的线程连接)

            //todo 用logger输出错误堆栈信息
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            //todo 用logger输出请求的ip，访问地址，请求类型，body

            //如果客服端想要上传数据
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            keepAlive = HttpUtil.isKeepAlive(request);
        }

        //接受完信息后
        if (msg instanceof LastHttpContent && msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            //此处应该进行事务
            //todo 计划用工厂模式进行事务的处理
            HttpResponseUtil responseUtil = TransactionHandler.handle(request);

            FullHttpResponse response;
            //如果是传输文件
            if (responseUtil.isFile()){
                response = new DefaultFullHttpResponse(HTTP_1_1, responseUtil.getHttpStatu().getStatus());
                response.headers()
                        .set(CONTENT_LENGTH, responseUtil.getRandomAccessFile().length())
                        .set(CONTENT_TYPE, responseUtil.getContentType());
                ctx.write(response);

                //构造发送文件线程，将文件写入到Chunked缓冲区中
                ChannelFuture sendFileFuture =
                        ctx.write(new ChunkedFile(responseUtil.getRandomAccessFile(), 0, responseUtil.getRandomAccessFile().length(), 8192), ctx.newProgressivePromise());

                //如果使用Chunked编码，最后则需要发送一个编码结束的看空消息体，进行标记，表示所有消息体已经成功发送完成。
                ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                //如果当前连接请求非Keep-Alive ，最后一包消息发送完成后 服务器主动关闭连接
                if (!HttpUtil.isKeepAlive(request)) {
                    lastContentFuture.addListener(ChannelFutureListener.CLOSE);
                }
            }else {
                response = new DefaultFullHttpResponse(HTTP_1_1, responseUtil.getHttpStatu().getStatus(),
                        Unpooled.copiedBuffer(responseUtil.getContent(), CharsetUtil.UTF_8));
                response.headers()
                        .set(CONTENT_TYPE, "text/plain; charset=UTF-8")
                        .set(CONTENT_LENGTH, response.content().readableBytes());
            }

            //判断是否允许跨域
            if (responseUtil.isCors()){
                String origin = responseUtil.getAllowOrigin(request.uri());
                if (!StringUtils.isBlank(origin)){
                    response.headers()
                            .set(ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                            .set(ACCESS_CONTROL_MAX_AGE, responseUtil.getMaxAge());
                }
            }

            //返回信息
            //如果不是keepAlive就关闭监听,否则添加keep alive
            if (!keepAlive) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);
            }

        }

    }

}
