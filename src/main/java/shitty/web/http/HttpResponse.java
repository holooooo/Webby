package shitty.web.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.lang3.StringUtils;
import shitty.web.Exception.HttpStatusNotExist;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * program: shitty
 * description: http响应类，用来快速构建一个响应
 * author: Makise
 * create: 2019-04-02 16:35
 **/
public class HttpResponse {
    private int statu;
    private String content;
    private String contentType;
    private HttpResponseStatus httpStatu;

    public HttpResponse(int statu, String content, String contentType) {
        this.statu = statu;
        this.content = content;
        this.contentType = contentType;
        this.httpStatu = HttpStatu.getByCode(statu);
    }

    public HttpResponse(String content, String contentType) {
        this(200, content, contentType);
    }

    public HttpResponse(String content) {
        this(200, content, "text/plain;charset=utf-8");
    }

    public HttpResponse(int statu) {
        this(statu, "", "text/plain;charset=utf-8");
    }

    public HttpResponse(int statu, String content) {
        this(statu, content, "text/plain;charset=utf-8");
    }

    /**
     * Description: 通过状态码设置response的状态，并且返回response
     * Param: [statu]
     * return: shitty.web.http.HttpResponse
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponse setStatu(int statu){
        this.statu = statu;
        return this;
    }

    /**
     * Description: 通过状态枚举设置response的状态，并且返回response
     * Param: [statu]
     * return: shitty.web.http.HttpResponse
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponse setStatu(HttpStatu statu){
        this.statu = statu.code;
        this.httpStatu = statu.status;
        return this;
    }

    /**
     * Description:  设置response的内容，并且返回response
     * Param: [content]
     * return: shitty.web.http.HttpResponse
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponse setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Description: 设置response返回的内容类型，并且返回response
     * Param: [contentType]
     * return: shitty.web.http.HttpResponse
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Description: 完成编辑，得到返回的response
     * Param: []
     * return: io.netty.handler.codec.http.FullHttpResponse
     * Author: Makise
     * Date: 2019/4/2
     */
    public FullHttpResponse done() {
        FullHttpResponse response ;
        if (StringUtils.isBlank(this.content)){
            response = new DefaultFullHttpResponse(HTTP_1_1, httpStatu);
        }else {
            response = new DefaultFullHttpResponse(HTTP_1_1, httpStatu,
                    Unpooled.wrappedBuffer(this.content.getBytes(StandardCharsets.UTF_8)));
        }

        response.headers()
                .set(CONTENT_LENGTH, response.content().readableBytes())
                .set(CONTENT_TYPE, this.contentType);
        return response;
    }

    //http状态码
    private static enum HttpStatu {
        //ok
        OK(200, HttpResponseStatus.OK),
        //错误的请求，服务器未能理解请求或是请求参数有误。
        BAD_REQUEST(400, HttpResponseStatus.BAD_REQUEST),
        //被请求的页面需要用户名和密码。
        UNAUTHORIZED(401, HttpResponseStatus.UNAUTHORIZED),
        //对被请求页面的访问被禁止。
        FORBIDDEN(403, HttpResponseStatus.FORBIDDEN),
        //服务器无法找到被请求的页面。
        NOT_FOUND(404, HttpResponseStatus.NOT_FOUND),
        //请求中指定的方法不被允许。
        METHOD_NOT_ALLOWED(404, HttpResponseStatus.METHOD_NOT_ALLOWED),
        ;
        private int code;
        private HttpResponseStatus status;

        HttpStatu(int code, HttpResponseStatus status) {
            this.code = code;
            this.status = status;
        }

        /**
         * Description: 得到code状态码所对应的http状态
         * Param: [code]
         * return: io.netty.handler.codec.http.HttpResponseStatus
         * Author: Makise
         * Date: 2019/4/2
         */
        public static HttpResponseStatus getByCode(int code) {
            for (HttpStatu statu : HttpStatu.values()) {
                if (statu.code == code) {
                    return statu.status;
                }
            }
            throw new HttpStatusNotExist();
        }
    }
}

