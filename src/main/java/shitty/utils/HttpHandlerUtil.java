package shitty.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import shitty.web.http.HttpResponseUtil;
import shitty.web.http.HttpStatu;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;

/**
 * program: shitty
 * description: 在handler中使用的一些小工具
 * author: Makise
 * create: 2019-04-13 23:51
 **/
public class HttpHandlerUtil {
    public static void logRequest(Logger logger, ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        logger.info("Currency Request[ Ip:{}, URI:{} , Method:{},\n User-Agent:{},\n TimeStamp:{},\n Content:{}]",
                ctx.channel().remoteAddress().toString(),
                request.uri(),
                request.method(),
                request.headers().get(USER_AGENT),
                Long.toString(System.currentTimeMillis() / 1000),
                GsonUtil.getGson().toJson(new RequestParser(request).parse()));
    }

    public static void logExceptionCaught(ChannelHandlerContext ctx, Throwable e, Logger logger) {
        HttpResponseUtil httpResponseUtil = new HttpResponseUtil();
        System.out.println(e.getClass());
        logger.warn("{}", e);
        httpResponseUtil.setStatu(HttpStatu.INTERNAL_SERVER_ERROR).putText("Failure: " + HttpStatu.INTERNAL_SERVER_ERROR.getStatus()).response(ctx);
        ctx.close();
    }
}
