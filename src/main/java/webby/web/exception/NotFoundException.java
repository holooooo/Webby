package webby.web.exception;

import webby.web.http.HttpStatus;

/**
 * program: webby
 * description:
 * author: Makise
 * create: 2019-04-15 22:49
 **/
public class NotFoundException extends BaseHttpStatusException {
    public NotFoundException(){
        HTTP_STATUS = HttpStatus.NOT_FOUND;
    }
}