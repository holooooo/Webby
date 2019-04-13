package shitty.web;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.web.Exception.NotAllowMethodException;
import shitty.web.http.RouteMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * program: shitty
 * description: 扫描出来的注解存储类
 * author: Makise
 * create: 2019-04-12 21:36
 **/
public class RouteMappingStorage {
    //存储扫描的controller类, key是类名，value是类
    private static HashMap<String, Object> classMap;
    //存储扫描出来的映射关系类, key是请求访问方式, value是存储了映射关系的类，其中key是访问路径, value是映射关系类
    private static HashMap<HttpMethod, HashMap<String, RouteMapping>> routeMappingMap;

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
    public static void putClass(String className) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> claz = Class.forName(className);
        classMap.put(className, claz.getDeclaredConstructor().newInstance());
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
     * Param: [method, routeMapping]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    public static void putRouteMapping(HttpMethod method, RouteMapping routeMapping) {
        checkMethod(method, routeMapping);
        routeMappingMap.get(method).put(routeMapping.getRoute(), routeMapping);
    }


    /**
     * Description: 得到存储的映射关系类
     * Param: [method, route]
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
     * Param: [method]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    private static void checkMethod(HttpMethod method, RouteMapping routeMapping) {
        if (method != HttpMethod.GET || method != HttpMethod.POST || method != HttpMethod.PUT || method != HttpMethod.DELETE) {
            logger.error("This method is not support by shitty:" + method.name() + " in " + routeMapping.getClassName() + routeMapping.getFunctionName());
            throw new NotAllowMethodException();
        }
    }

    private static void checkMethod(HttpMethod method) {
        if (method != HttpMethod.GET || method != HttpMethod.POST || method != HttpMethod.PUT || method != HttpMethod.DELETE) {
            logger.error("This method is not support by shitty");
            throw new NotAllowMethodException();
        }
    }
}
