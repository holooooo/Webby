package webby.web.exception;

import webby.web.http.HttpStatus;

/**
 * program: webby
 * description: 该框架只适用于Restful的规则，也就是说出现Put，Get,Post,Delete以外的请求方法是会报错
 * author: Makise
 * create: 2019-04-12 22:10
 **/
public class MethodNotAllowException extends BaseHttpStatusException {
    public MethodNotAllowException(){
        HTTP_STATUS = HttpStatus.METHOD_NOT_ALLOWED;
    }
}
