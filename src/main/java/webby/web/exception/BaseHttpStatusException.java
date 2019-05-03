package webby.web.exception;

import webby.web.http.HttpStatus;

/**
 * program: webby
 * description: 有http状态码的异常
 * author: Makise
 * create: 2019-04-15 23:05
 **/
public class BaseHttpStatusException extends RuntimeException {
    static HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public HttpStatus getHttpStatus(){
        return HTTP_STATUS;
    }
    public BaseHttpStatusException(){}
    public BaseHttpStatusException(String s){super(s);}
}
