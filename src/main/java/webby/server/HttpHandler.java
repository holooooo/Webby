package webby.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.web.TransactionHandler;
import webby.web.http.HttpResponseUtil;


/**
 * program: webby
 * description: http逻辑控制器
 * author: Makise
 * create: 2019-02-26 10:35
 **/
public class HttpHandler extends BaseHttpHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    HttpHandler() {
        super();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            request = (FullHttpRequest) msg;
        }

        //接受完信息后
        if (msg instanceof LastHttpContent) {
            logRequest(logger, ctx, request);
            HttpResponseUtil responseUtil = TransactionHandler.handle(request);
            responseUtil.setRequest(request).response(ctx);
        }

    }

}
