package shitty.web.exception;

import shitty.web.http.HttpStatus;

/**
 * program: shitty
 * description:
 * author: Makise
 * create: 2019-04-15 22:49
 **/
public class NotFoundException extends BaseHttpStatusException {
    public NotFoundException(){
        HTTP_STATUS = HttpStatus.NOT_FOUND;
    }
}