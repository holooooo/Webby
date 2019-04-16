package shitty.web.exception;

import shitty.web.http.HttpStatus;

/**
 * program: shitty
 * description:
 * author: Makise
 * create: 2019-04-15 22:46
 **/
public class BadRequestExceptionBase extends BaseHttpStatusException {
    public BadRequestExceptionBase(){
        HTTP_STATUS = HttpStatus.BAD_REQUEST;
    }
}
