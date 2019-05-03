package webby.web.exception;

import webby.web.http.HttpStatus;

/**
 * program: webby
 * description:
 * author: Makise
 * create: 2019-04-15 22:51
 **/
public class ForbiddenException extends BaseHttpStatusException {
    public ForbiddenException(){
        HTTP_STATUS = HttpStatus.FORBIDDEN;
    }
}
