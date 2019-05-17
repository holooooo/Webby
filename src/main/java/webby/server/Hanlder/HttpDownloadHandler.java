package webby.server.Hanlder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.web.http.HttpResponseUtil;

import java.io.IOException;

/**
 * program: webby
 * description: 下载文件处理器
 * author: Makise
 * create: 2019-04-13 22:56
 **/
public class HttpDownloadHandler extends BaseHttpHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpDownloadHandler.class);

    public HttpDownloadHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        String uri = request.uri();
        if (!uri.startsWith("/download") || !request.method().equals(HttpMethod.GET)) {
            ctx.fireChannelRead(request);
            return;
        }

        logRequest(logger, ctx, request);
        //去掉download部分得到文件名称
        new HttpResponseUtil().setRequest(request).putFile(uri.replace("/download/","data/")).response(ctx);

    }



}