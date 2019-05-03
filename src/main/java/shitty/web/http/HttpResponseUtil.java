package shitty.web.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.utils.GsonUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * program: shitty
 * description: http响应类，用来快速构建一个响应
 * author: Makise
 * create: 2019-04-02 16:35
 **/
public class HttpResponseUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseUtil.class);

    private String content = "";
    private HttpRequest request;
    private HttpContentType contentType = HttpContentType.PLAIN;
    private HttpStatus httpStatus = HttpStatus.OK;
    private File file;
    private String[] allowOrigins;
    private int maxAge;
    private HttpResponse response;

    public HttpResponseUtil() {
    }

    public HttpResponseUtil(HttpRequest request) {
        this.request = request;
    }

    /**
     * Description: 设置response返回的内容类型，并且返回HttpResponse
     * Param: [contentType]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil setContentType(HttpContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Description: 通过状态码设置response的状态，并且返回HttpResponse
     * Param: [status]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil setStatu(int statu) {
        this.httpStatus = HttpStatus.getByCode(statu);
        return this;
    }

    /**
     * Description: 通过状态枚举设置response的状态，并且返回HttpResponse
     * Param: [status]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil setStatu(HttpStatus statu) {
        this.httpStatus = statu;
        return this;
    }

    /**
     * Description: 设置该请求是否允许跨域
     * Param: [allowOrigins, maxAge]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/12
     */
    public HttpResponseUtil setCros(String[] allowOrigins, int maxAge) {
        this.allowOrigins = allowOrigins;
        this.maxAge = maxAge;
        return this;
    }

    public HttpResponseUtil setCros(String[] allowOrigins) {
        this.allowOrigins = allowOrigins;
        this.maxAge = -1;
        return this;
    }

    public boolean isFile() {
        return httpStatus == HttpStatus.OK && file != null;
    }


    /**
     * Description: 返回报错信息
     * Param: [ctx, status]
     * return: void
     * Author: Makise
     * Date: 2019/4/8
     */
    public void error(ChannelHandlerContext ctx, HttpStatus statu) {
        setStatu(statu).putText("Failure: " + statu.getStatus().toString());
        response(ctx);
    }

    /**
     * Description:  设置response中要返回的JSON内容，并且返回HttpResponse
     * Param: [content]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil putJson(Object content) {
        this.content = GsonUtil.toJson(content);
        this.contentType = HttpContentType.JSON;
        return this;
    }

    public HttpResponseUtil putJson(Object... contents) {
        this.content = GsonUtil.toJson(contents);
        this.contentType = HttpContentType.JSON;
        return this;
    }

    /**
     * Description:  设置response中要返回的内容，并且返回HttpResponse
     * Param: [content]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil putText(Object content) {
        this.content += content.toString();
        return this;
    }

    /**
     * Description: 返回一个从本地读取的html网页
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putHtml(String path) {
        this.contentType = HttpContentType.HTML;
        return putFile(path);
    }


    /**
     * Description: 提供一个文件地址，并且将该地址以流加入到HttpRespronse中
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/7
     */
    public HttpResponseUtil putFile(String path) {
        //创建随机读写类
        file = new File(path);
        return this;
    }



    /**
     * Description: 用来发送文件
     * Param: [ctx]
     * return: void
     * Author: Makise
     * Date: 2019/5/1
     */
    private void responseFile(ChannelHandlerContext ctx) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

            long fileLength = randomAccessFile.length();
            response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
            //设置响应信息
            HttpUtil.setContentLength(response, fileLength);
            //设置响应头
            if (this.contentType == HttpContentType.PLAIN){
                MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
            }

            //进行写出
            ctx.write(response);
            ChannelFuture sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0,
                    fileLength, 8192), ctx.newProgressivePromise());

            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    logger.info("file {} transfer complete.", file.getName());
                    randomAccessFile.close();
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
            ChannelFuture lastContentFuture = ctx
                    .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!HttpUtil.isKeepAlive(request)) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }

        } catch (FileNotFoundException e) {
            error(ctx, HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            error(ctx, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Description: 将响应返回
     * Param: [ctx]
     * return: void
     * Author: Makise
     * Date: 2019/5/1
     */
    public void response(ChannelHandlerContext ctx) {
        if (isFile()) {
            responseFile(ctx);
            return;
        }

        if (StringUtils.isBlank(content)) {
            response = new DefaultFullHttpResponse(HTTP_1_1, httpStatus.getStatus());
        } else {
            response = new DefaultFullHttpResponse(HTTP_1_1, httpStatus.getStatus(),
                    Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        }

        //如果一直保持连接则设置响应头信息为：HttpHeaders.Values.KEEP_ALIVE
        if (request != null && HttpUtil.isKeepAlive(request)) {
            HttpUtil.setKeepAlive(response, true);
        }
        //检查是否需要配置跨域信息
        if (request != null && allowOrigins != null && allowOrigins.length != 0) {
            String origin = getAllowOrigin(request.headers().get(HOST));
            if (!StringUtils.isBlank(origin)) {
                response.headers()
                        .set(ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                        .set(ACCESS_CONTROL_MAX_AGE, getMaxAge());
            }
        }

        ctx.write(response);
        ChannelFuture lastContentFuture = ctx
                .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    }


    //得到允许跨的域
    public String getAllowOrigin(String uri) {
        //如果是允许所有域访问的
        if ("*".equals(allowOrigins[0])) {
            return "*";
        } else {
            for (String s : allowOrigins) {
                //如果访问源是允许的域
                if (uri.substring(0, s.length()).equals(s)) {
                    return s;
                }
            }
        }
        return "";
    }


    public String getContent() {
        return content;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpContentType getContentType() {
        return contentType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


    public int getMaxAge() {
        return maxAge;
    }

    public HttpResponseUtil setRequest(FullHttpRequest request) {
        this.request = request;
        return this;
    }
}

