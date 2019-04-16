package shitty.web.http;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;

import java.lang.reflect.Parameter;


/**
 * program: shitty
 * description: 路由映射状况
 * author: Makise
 * create: 2019-04-08 22:19
 **/
@Data
public class RouteMapping {
    //允许使用的请求方法
    private HttpMethod method;
    //请求路径
    private String route;
    //所属的类名
    private String className;
    //方法名
    private String methodName;
    //参数名称及类型, key是参数名，value是参数类型
    private Parameter[] params;
    //是否允许跨域
    private String[] allowOrigin;
    //跨域资源缓存时长
    private int maxAge;
}
