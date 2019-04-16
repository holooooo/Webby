package shitty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.web.http.HttpResponseUtil;
import shitty.web.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * program: shitty
 * description: 上传文件处理器
 * author: Makise
 * create: 2019-04-13 22:40
 **/
public class HttpUploadHandler extends BaseHttpHandler<HttpObject> {
    private static final Logger logger = LoggerFactory.getLogger(HttpUploadHandler.class);

    public HttpUploadHandler() {
        super(false);
    }

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);
    private static final String FILE_UPLOAD = "/data/";
    private static final String URI = "/upload";
    private HttpPostRequestDecoder httpDecoder;
    private FullHttpRequest request;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject httpObject)
            throws Exception {
        if (httpObject instanceof FullHttpRequest) {
            request = (FullHttpRequest) httpObject;
            if (!request.uri().startsWith(URI) || !request.method().equals(HttpMethod.POST)) {
                //传递给下一个Handler
                ctx.fireChannelRead(httpObject);
            }
            httpDecoder = new HttpPostRequestDecoder(factory, request);
            httpDecoder.setDiscardThreshold(0);

            logRequest(logger, ctx, request);
        }
        if (httpObject instanceof HttpContent) {
            if (httpDecoder == null) {
                ctx.fireChannelRead(httpObject);
            }
            final HttpContent chunk = (HttpContent) httpObject;
            httpDecoder.offer(chunk);
            if (chunk instanceof LastHttpContent) {
                writeChunk(ctx);
                //关闭httpDecoder
                httpDecoder.destroy();
                httpDecoder = null;
            }
            ReferenceCountUtil.release(httpObject);
        }

    }

    private void writeChunk(ChannelHandlerContext ctx) throws IOException {
        while (httpDecoder.hasNext()) {
            InterfaceHttpData data = httpDecoder.next();
            if (data != null && InterfaceHttpData.HttpDataType.FileUpload.equals(data.getHttpDataType())) {
                final FileUpload fileUpload = (FileUpload) data;
                final File file = new File(FILE_UPLOAD + fileUpload.getFilename());
                logger.info("upload file: {}", file);
                try (FileChannel inputChannel = new FileInputStream(fileUpload.getFile()).getChannel();
                     FileChannel outputChannel = new FileOutputStream(file).getChannel()) {
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    HttpResponseUtil httpResponseUtil = new HttpResponseUtil();
                    httpResponseUtil.setStatu(HttpStatus.OK).response(ctx, request);
                }
            }
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (httpDecoder != null) {
            httpDecoder.cleanFiles();
        }
    }

}