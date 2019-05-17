package webby.server.Hanlder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.config.WebbyConfig;
import webby.web.exception.BaseHttpStatusException;
import webby.web.http.HttpResponseUtil;
import webby.web.http.HttpStatus;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;

/**
 * program: webby
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws IOException {
        HttpResponseUtil httpResponseUtil = new HttpResponseUtil();
        if (BaseHttpStatusException.class.isAssignableFrom(e.getClass())) {
            logger.warn("{}", e.toString());
            BaseHttpStatusException be = (BaseHttpStatusException) e;
            httpResponseUtil.setStatu(be.getHttpStatus())
                    .putText("Failure: " + be.getHttpStatus().getStatus());
        } else {
            logger.error("{}", e);
            httpResponseUtil.setStatu(HttpStatus.INTERNAL_SERVER_ERROR)
                    .putText("Failure: " + HttpStatus.INTERNAL_SERVER_ERROR.getStatus() + "\n");
            if (WebbyConfig.getConfig().isDebug()){
                StringBuilder errInfo = new StringBuilder(e.getClass().getName()).append(e.getLocalizedMessage()).append("\n");
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    errInfo.append("    at ").append(stackTraceElement).append("\n");
                }
                httpResponseUtil.putText(errInfo.toString());
            }
        }
        httpResponseUtil.response(ctx);
        ctx.close();
    }

    /**
     * Description: 记录当前请求的信息
     * Param: [logger, ctx, request]
     * return: void
     * Author: Makise
     * Date: 2019/4/15
     */
    void logRequest(Logger logger, ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        logger.debug("Currency Request[ Ip:{}, URI:{} , Method:{}, User-Agent:{}, TimeStamp:{}]",
                ctx.channel().remoteAddress().toString(),
                request.uri(),
                request.method(),
                request.headers().get(USER_AGENT),
                Long.toString(System.currentTimeMillis() / 1000));
    }
}
