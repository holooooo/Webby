package shitty.web.Exception;

/**
 * program: shitty
 * description: 如果目标http状态码不存在或者没有实现就返回该异常
 * author: Makise
 * create: 2019-04-02 21:57
 **/
public class HttpStatusNotExist extends RuntimeException {
    public HttpStatusNotExist(){}
    public HttpStatusNotExist(String s){super(s);}
}