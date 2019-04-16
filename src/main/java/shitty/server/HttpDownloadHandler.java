package shitty.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.web.exception.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * program: shitty
 * description: 下载文件处理器
 * author: Makise
 * create: 2019-04-13 22:56
 **/
public class HttpDownloadHandler extends BaseHttpHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpDownloadHandler.class);

    public HttpDownloadHandler() {
        super(false);
    }

    private String filePath = "/data/body.csv";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        String uri = request.uri();
        if (!uri.startsWith("/download") || !request.method().equals(HttpMethod.GET)) {
            ctx.fireChannelRead(request);
        }

        logRequest(logger, ctx, request);
        //todo 通过TransactionHandler得到要下载的文件
        File file = new File(filePath);
        try {
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long fileLength = raf.length();
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
            ctx.write(response);
            ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    logger.info("file {} transfer complete.", file.getName());
                    raf.close();
                }

                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) throws Exception {
                    if (total < 0) {
                        logger.warn("file {} transfer progress: {}", file.getName(), progress);
                    } else {
                        logger.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
                    }
                }
            });
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } catch (FileNotFoundException e) {
            throw new NotFoundException();
        } catch (IOException e) {
            throw e;
        }

    }

}