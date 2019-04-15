package shitty.web.exception;

/**
 * program: shitty
 * description: 如果目标http状态码不存在或者没有实现就返回该异常
 * author: Makise
 * create: 2019-04-02 21:57
 **/
public class HttpStatusNotSupportOrExistException extends RuntimeException {
    public HttpStatusNotSupportOrExistException(){}
    public HttpStatusNotSupportOrExistException(String s){
        super(s);}
}
