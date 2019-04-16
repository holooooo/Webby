package shitty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.utils.GsonUtil;
import shitty.utils.RequestParser;
import shitty.web.exception.BaseHttpStatusException;
import shitty.web.http.HttpResponseUtil;
import shitty.web.http.HttpStatus;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;

/**
 * program: shitty
 * description: httpHandler的基础类
 * author: Makise
 * create: 2019-04-15 17:42
 **/
public abstract class BaseHttpHandler<I> extends SimpleChannelInboundHandler<I> {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    FullHttpRequest request;

    BaseHttpHandler() {
        super();
    }

    BaseHttpHandler(boolean b) {
        super(b);
    }

    /**
     * Description: 捕捉到异常就返回异常的状态码
     * Param: [ctx, e]
     * return: void
     * Author: Makise
     * Date: 2019/4/15
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        HttpResponseUtil httpResponseUtil = new HttpResponseUtil();
        logger.warn("{}", e);
        if (BaseHttpStatusException.class.isAssignableFrom(e.getClass())) {
            BaseHttpStatusException be = (BaseHttpStatusException) e;
            httpResponseUtil.setStatu(be.getHttpStatus())
                    .putText("Failure: " + be.getHttpStatus().getStatus())
                    .response(ctx, request);
        } else {
            httpResponseUtil.setStatu(HttpStatus.INTERNAL_SERVER_ERROR)
                    .putText("Failure: " + HttpStatus.INTERNAL_SERVER_ERROR.getStatus())
                    .response(ctx, request);
        }
        ctx.close();
    }

    /**
     * Description: 记录当前请求的信息
     * Param: [logger, ctx, request]
     * return: void
     * Author: Makise
     * Date: 2019/4/15
     */
    protected void logRequest(Logger logger, ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        logger.info("Currency Request[ Ip:{}, URI:{} , Method:{},\nUser-Agent:{},\nTimeStamp:{},\nContent:{}]",
                ctx.channel().remoteAddress().toString(),
                request.uri(),
                request.method(),
                request.headers().get(USER_AGENT),
                Long.toString(System.currentTimeMillis() / 1000),
                GsonUtil.getGson().toJson(new RequestParser(request).parse()));
    }
}