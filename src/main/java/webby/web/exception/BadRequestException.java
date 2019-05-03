package webby.web.exception;

import webby.web.http.HttpStatus;

/**
 * program: webby
 * description:
 * author: Makise
 * create: 2019-04-15 22:46
 **/
public class BadRequestException extends BaseHttpStatusException {
    public BadRequestException(){
        HTTP_STATUS = HttpStatus.BAD_REQUEST;
    }
}
