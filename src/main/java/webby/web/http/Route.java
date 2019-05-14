package webby.web.http;

import com.google.gson.internal.LinkedHashTreeMap;
import lombok.Data;
import lombok.ToString;
import webby.web.annotation.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;


/**
 * program: webby
 * description: 路由映射状况
 * author: Makise
 * create: 2019-04-08 22:19
 **/
@Data
@ToString
public class Route {
    //允许使用的请求方法
    private String httpMethod;
    //请求路径
    private String route;
    //所属的类
    private Class<?> clazz;
    //方法
    private Method method;
    //参数名称及类型, key是参数名，value是参数类型
    private Map<String, Class<?>> params;
    //是否允许跨域
    private String[] allowOrigins;
    //跨域资源缓存时长
    private int maxAge;

    public void setParams(Parameter[] params) {
        if (params != null && params.length > 0) {
            this.params = new LinkedHashTreeMap<>();
            for (Parameter param : params) {
                if (!param.isAnnotationPresent(Param.class)) {
                    continue;
                }
                this.params.put(param.getAnnotation(Param.class).value(), param.getType());
            }
        }
    }


}
