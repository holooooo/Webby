package shitty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.utils.HttpHandlerUtil;
import shitty.web.Exception.NotAllowMethodException;
import shitty.web.TransactionHandler;
import shitty.web.http.HttpResponseUtil;
import shitty.web.http.HttpStatu;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * program: shitty
 * description: http逻辑控制器
 * author: Makise
 * create: 2019-02-26 10:35
 **/
public class HttpHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    private FullHttpRequest request;

    HttpHandler() {
        super();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        HttpResponseUtil httpResponseUtil = new HttpResponseUtil();
        System.out.println(e.getClass());
        if (e.getClass() == NotAllowMethodException.class){
            httpResponseUtil.setStatu(HttpStatu.METHOD_NOT_ALLOWED).putText("Failure: " + HttpStatu.METHOD_NOT_ALLOWED.getStatus()).response(ctx, request);
        }else {
            logger.warn("{}", e);
            httpResponseUtil.setStatu(HttpStatu.INTERNAL_SERVER_ERROR).putText("Failure: " + HttpStatu.INTERNAL_SERVER_ERROR.getStatus()).response(ctx, request);
        }
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            request = (FullHttpRequest) msg;
            //如果客服端想要上传数据
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
        }

        //接受完信息后
        if (msg instanceof LastHttpContent) {
            LastHttpContent httpContent = (LastHttpContent) msg;
            HttpHandlerUtil.logRequest(logger, ctx, request);

            HttpResponseUtil responseUtil = TransactionHandler.handle(request, httpContent);
            responseUtil.response(ctx, request);
        }

    }

}
