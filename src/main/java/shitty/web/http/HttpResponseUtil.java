package shitty.web.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import shitty.config.ShittyConfig;
import shitty.utils.GsonUtil;

import java.io.*;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * program: shitty
 * description: http响应类，用来快速构建一个响应
 * author: Makise
 * create: 2019-04-02 16:35
 **/
public class HttpResponseUtil {
    private String content = "";
    private HttpRequest request;
    private HttpContentType contentType = HttpContentType.PLAIN;
    private HttpStatus httpStatus = HttpStatus.OK;
    //随机文件读写类
    private RandomAccessFile randomAccessFile;
    private String[] allowOrigins;
    private int maxAge;

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
    public HttpResponseUtil setCros(String[] allowOrigins, int maxAge){
        this.allowOrigins = allowOrigins;
        this.maxAge = maxAge;
        return this;
    }

    public HttpResponseUtil setCros(String[] allowOrigins){
        this.allowOrigins = allowOrigins;
        this.maxAge = -1;
        return this;
    }

    public boolean isFile() {
        return randomAccessFile != null;
    }


    /**
     * Description: 返回报错信息
     * Param: [status]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil error(HttpStatus statu){
        this.httpStatus = statu;
        this.content = "Failure: " + statu.getStatus().toString()+ "\r\n";
        return this;
    }

    /**
     * Description:  设置response中要返回的JSON内容，并且返回HttpResponse
     * Param: [content]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil putJson(Object content) {
        this.content = GsonUtil.getGson().toJson(content);
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
    public HttpResponseUtil putText(String content) {
        this.content = content;
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
        if (isFileExistAndGet(path)) {
            return this;
        }
        File html = new File(path);
        byte[] filecontent = new byte[(int) html.length()];
        try {
            FileInputStream in = new FileInputStream(html);
            in.read(filecontent);
            in.close();
            this.content = new String(filecontent, ShittyConfig.getConfig().getCharset().name());
        } catch (IOException e) {
            setStatu(HttpStatus.NOT_FOUND);
            return this;
        }
        this.contentType = HttpContentType.HTML;
        return this;
    }


    /**
     * Description: 提供一个文件地址，并且将该地址以流加入到HttpRespronse中
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/7
     */
    public HttpResponseUtil putFile(String path, HttpContentType contentType) throws IOException {
        if (isFileExistAndGet(path)) {
            return this;
        }

        //创建随机读写类
        try {
            randomAccessFile = new RandomAccessFile(new File(path), "r");
        } catch (FileNotFoundException e) {
            error(HttpStatus.NOT_FOUND);
            return this;
        }
        this.contentType = contentType;
        return this;
    }

    /**
     * Description: 传输文件，内容类型为空的情况默认为Octet-Stream
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putFile(String path) throws IOException {
        return putFile(path, HttpContentType.OCTET_STREAM);
    }

    /**
     * Description: 传输文件，内容类型为图片
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putImg(String path) throws IOException {
        return putFile(path, HttpContentType.IMG);
    }

    /**
     * Description: 传输文件，内容类型为图标
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putIcon(String path) throws IOException {
        return putFile(path, HttpContentType.ICON);
    }

    /**
     * Description: 返回请求
     * Param: [ctx, request]
     * return: void
     * Author: Makise
     * Date: 2019/4/13
     */
    public void response(ChannelHandlerContext ctx, HttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpStatus.getStatus(),
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

        //检查是否需要配置跨域信息
        if (request != null && allowOrigins != null && allowOrigins.length != 0) {
            String origin = getAllowOrigin(request.headers().get(HOST));
            if (!StringUtils.isBlank(origin)) {
                response.headers()
                        .set(ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                        .set(ACCESS_CONTROL_MAX_AGE, getMaxAge());
            }
        }

        //查看是否为keepAlive
        if (request != null && !HttpUtil.isKeepAlive(request)) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
        }
        ctx.flush();
    }

    public void response(ChannelHandlerContext ctx) {
        response(ctx, null);
    }

    /**
     * Description: 检查请求方式是否为get以及文件是否存在
     * Param: [path]
     * return: boolean
     * Author: Makise
     * Date: 2019/4/8
     */
    private boolean isFileExistAndGet(String path) {
        //如果不是以get方法请求的
        if (request.method() != HttpMethod.GET) {
            error(HttpStatus.METHOD_NOT_ALLOWED);
            return true;
        }
        File file = new File(path);
        //如果文件不存在或者是隐藏文件或者是文件夹
        if (file.isHidden() || !file.exists() || file.isDirectory()) {
            error(HttpStatus.NOT_FOUND);
            return true;
        }
        return false;
    }


    //得到允许跨的域
    public String getAllowOrigin(String uri) {
        //如果是允许所有域访问的
        if ("*".equals(allowOrigins[0])){
            return "*";
        }else {
            for (String s : allowOrigins) {
                //如果访问源是允许的域
                if (uri.substring(0, s.length()).equals(s)){
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

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public int getMaxAge() {
        return maxAge;
    }

}

