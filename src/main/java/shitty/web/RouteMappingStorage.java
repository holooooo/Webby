package shitty.web;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.web.exception.MethodNotAllowException;
import shitty.web.http.RouteMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * program: shitty
 * description: 扫描出来的注解存储类
 * author: Makise
 * create: 2019-04-12 21:36
 **/
public class RouteMappingStorage {
    //存储扫描的controller类, key是类，value是实例
    private static HashMap<Class<?>, Object> classMap;
    //存储扫描出来的映射关系类, key是请求访问方式, value是存储了映射关系的类，其中key是访问路径, value是映射关系类
    private static Map<HttpMethod, Map<String, RouteMapping>> routeMappingMap;

    private static final Logger logger = LoggerFactory.getLogger(RouteMappingStorage.class);

    // 初始化routeMappingMap
    static {
        routeMappingMap = new HashMap<>(4);
        routeMappingMap.put(HttpMethod.GET, new HashMap<>(16));
        routeMappingMap.put(HttpMethod.POST, new HashMap<>(16));
        routeMappingMap.put(HttpMethod.PUT, new HashMap<>(16));
        routeMappingMap.put(HttpMethod.DELETE, new HashMap<>(16));
    }

    /**
     * Description: 添加一个新的类实体
     * Param: [className]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    static void putClass(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        classMap.put(clazz, clazz.getConstructor().newInstance());
    }

    /**
     * Description: 得到一个类实体
     * Param: [className]
     * return: java.lang.Object
     * Author: Makise
     * Date: 2019/4/12
     */
    public static Object getClass(String className) {
        return classMap.get(className);
    }

    /**
     * Description: 存储扫描出来的映射关系
     * Param: [httpMethod, routeMapping]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    public static void putRouteMapping(RouteMapping routeMapping) {
        checkMethod(routeMapping);
        routeMappingMap.get(routeMapping.getHttpMethod()).put(routeMapping.getRoute(), routeMapping);
    }


    /**
     * Description: 得到存储的映射关系类
     * Param: [httpMethod, route]
     * return: shitty.web.http.RouteMapping
     * Author: Makise
     * Date: 2019/4/12
     */
    public static RouteMapping getRouteMapping(HttpMethod method, String route) {
        checkMethod(method);
        return routeMappingMap.get(method).get(route);
    }

    /**
     * Description: 检查当前该映射的http请求方法是否被支持
     * Param: [httpMethod]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    private static void checkMethod(RouteMapping routeMapping) {
        if (routeMapping.getHttpMethod() != HttpMethod.GET ||
                routeMapping.getHttpMethod() != HttpMethod.POST ||
                routeMapping.getHttpMethod() != HttpMethod.PUT ||
                routeMapping.getHttpMethod() != HttpMethod.DELETE) {
            logger.error("This httpMethod is not support by shitty:{} in {}",
                    routeMapping.getHttpMethod().name(),
                    routeMapping.getClazz().getName() + routeMapping.getMethod().getName());
            throw new MethodNotAllowException();
        }
    }

    private static void checkMethod(HttpMethod method) {
        if (method != HttpMethod.GET || method != HttpMethod.POST || method != HttpMethod.PUT || method != HttpMethod.DELETE) {
            logger.error("This httpMethod is not support by shitty");
            throw new MethodNotAllowException();
        }
    }
}
