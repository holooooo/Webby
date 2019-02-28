package shitty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.Http2Headers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * program: shitty
 * description: http逻辑控制器
 * author: Makise
 * create: 2019-02-26 10:35
 **/
public class HttpHandler extends SimpleChannelInboundHandler<Object> {
    private boolean keepAlive;

    public HttpHandler() {
        super();
        System.out.printf("控制器 %s 被创建.\n", this.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.printf("控制器 %s 读取一个包.\n", this.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.printf("控制器 %s 出现异常.\n", this.toString());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("控制器 %s 销毁.\n", this.toString());
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            System.out.printf("该请求的类型是%s\n", request.method());
            System.out.printf("该请求的地址是%s\n", request.uri());

            //如果客服端想要上传数据
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            keepAlive = HttpUtil.isKeepAlive(request);
        }

        //接受完信息后
        if (msg instanceof LastHttpContent) {
            //此处应该进行事务
            //计划用工厂模式进行事务的处理


            //返回信息
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("你好".getBytes(StandardCharsets.UTF_8)));
            response.headers().set(CONTENT_TYPE, "text/plain")
                    .set(CONTENT_LENGTH, response.content().readableBytes())
                    .set(CONTENT_TYPE, "text/plain;charset=utf-8");
            //如果不是keepAlive就关闭监听
            if (!keepAlive) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);

            }
        }
    }
}
