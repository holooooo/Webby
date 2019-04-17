package shitty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.web.TransactionHandler;
import shitty.web.http.HttpResponseUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * program: shitty
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
            //如果客服端想要上传数据
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
        }

        //接受完信息后
        if (msg instanceof LastHttpContent) {
            logRequest(logger, ctx, request);

            HttpResponseUtil responseUtil = TransactionHandler.handle(request);
            responseUtil.response(ctx, request);
        }

    }

}
