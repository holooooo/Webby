package shitty.web.exception;

import shitty.web.http.HttpStatus;

/**
 * program: shitty
 * description:
 * author: Makise
 * create: 2019-04-15 22:46
 **/
public class BadRequestException extends BaseHttpStatusException {
    public BadRequestException(){
        HTTP_STATUS = HttpStatus.BAD_REQUEST;
    }
}
