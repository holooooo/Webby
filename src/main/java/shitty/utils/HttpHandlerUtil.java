package shitty.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;

/**
 * program: shitty
 * description: 在handler中使用的一些小工具
 * author: Makise
 * create: 2019-04-13 23:51
 **/
public class HttpHandlerUtil {
    /**
     * Description: 记录当前请求的信息
     * Param: [logger, ctx, request]
     * return: void
     * Author: Makise
     * Date: 2019/4/15
     */
    public static void logRequest(Logger logger, ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        logger.info("Currency Request[ Ip:{}, URI:{} , Method:{},\nUser-Agent:{},\nTimeStamp:{},\nContent:{}]",
                ctx.channel().remoteAddress().toString(),
                request.uri(),
                request.method(),
                request.headers().get(USER_AGENT),
                Long.toString(System.currentTimeMillis() / 1000),
                GsonUtil.getGson().toJson(new RequestParser(request).parse()));
    }
}
