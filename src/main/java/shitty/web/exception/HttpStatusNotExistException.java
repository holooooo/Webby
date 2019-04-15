package shitty.web.exception;

/**
 * program: shitty
 * description: 如果目标http状态码不存在或者没有实现就返回该异常
 * author: Makise
 * create: 2019-04-02 21:57
 **/
public class HttpStatusNotExistException extends RuntimeException {
    public HttpStatusNotExistException(){}
    public HttpStatusNotExistException(String s){super(s);}
}
