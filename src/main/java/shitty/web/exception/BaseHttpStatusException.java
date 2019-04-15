package shitty.web.exception;

import shitty.web.http.HttpStatus;

/**
 * program: shitty
 * description: 有http状态码的异常
 * author: Makise
 * create: 2019-04-15 23:05
 **/
public class BaseHttpStatusException extends RuntimeException {
    static HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public HttpStatus getHttpStatus(){
        return HTTP_STATUS;
    }
    BaseHttpStatusException(){}
    BaseHttpStatusException(String s){super(s);}
}
