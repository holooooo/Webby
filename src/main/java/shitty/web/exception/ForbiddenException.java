package shitty.web.exception;

import shitty.web.http.HttpStatus;

/**
 * program: shitty
 * description:
 * author: Makise
 * create: 2019-04-15 22:51
 **/
public class ForbiddenException extends BaseHttpStatusException {
    ForbiddenException(){
        HTTP_STATUS = HttpStatus.FORBIDDEN;
    }
    ForbiddenException(String s) {
        super(s);
    }
}
