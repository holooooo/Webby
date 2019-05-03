package webby.web.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import webby.web.exception.MethodNotAllowException;

//http状态码
public enum HttpStatus {
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
    METHOD_NOT_ALLOWED(405, HttpResponseStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR(500, HttpResponseStatus.INTERNAL_SERVER_ERROR),
    ;
    private int code;
    private HttpResponseStatus status;

    HttpStatus(int code, HttpResponseStatus status) {
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
    public static HttpStatus getByCode(int code) {
        for (HttpStatus statu : HttpStatus.values()) {
            if (statu.code == code) {
                return statu;
            }
        }
        throw new MethodNotAllowException();
    }

    public HttpResponseStatus getStatus() {
        return status;
    }
}