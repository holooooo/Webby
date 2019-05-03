package webby.web;

import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.web.exception.MethodNotAllowException;
import webby.web.http.RouteMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * program: webby
 * description: 扫描出来的注解存储类
 * author: Makise
 * create: 2019-04-12 21:36
 **/
public class RouteMappingStorage {
    //存储扫描的controller类, key是类，value是实例
    private static HashMap<Class<?>, Object> classMap;
    //存储扫描出来的映射关系类, key是请求访问方式, value是存储了<存储映射关系的类>的桶，<存储映射关系的类>中key是访问路径, value是映射关系类
    private static Map<String, Map<Integer, Map<String, RouteMapping>>> routeMappingMap;

    private static final Logger logger = LoggerFactory.getLogger(RouteMappingStorage.class);

    // 初始化routeMappingMap
    static {
        classMap = new HashMap<>(16);
        routeMappingMap = new HashMap<>(4);
        routeMappingMap.put("GET", new HashMap<>(16));
        routeMappingMap.put("POST", new HashMap<>(16));
        routeMappingMap.put("PUT", new HashMap<>(16));
        routeMappingMap.put("DELETE", new HashMap<>(16));
    }

    /**
     * Description: 添加一个新的类实体
     * Param: [className]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    static void putClass(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        classMap.put(clazz, clazz.getDeclaredConstructor().newInstance());
    }

    /**
     * Description: 得到一个类实体
     * Param: [clazz]
     * return: java.lang.Object
     * Author: Makise
     * Date: 2019/4/12
     */
    static Object getClass(Class<?> clazz) {
        return classMap.get(clazz);
    }

    /**
     * Description: 存储扫描出来的映射关系
     * Param: [httpMethod, routeMapping]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    static void putRouteMapping(RouteMapping routeMapping) {
        checkMethod(routeMapping.getHttpMethod());
        int barrelCount = routeMapping.getRoute().substring(1).split("/").length;
        Map<Integer, Map<String, RouteMapping>> methodMap = routeMappingMap.get(routeMapping.getHttpMethod());
        if (!methodMap.containsKey(barrelCount)) {
            methodMap.put(barrelCount, new HashMap<>(16));
        }
        methodMap.get(barrelCount).put(routeMapping.getRoute(), routeMapping);
    }


    /**
     * Description: 得到存储的映射关系类,uri需要去除url参数
     * Param: [httpMethod, route]
     * return: webby.web.http.RouteMapping
     * Author: Makise
     * Date: 2019/4/12
     */
    static RouteMapping getRouteMapping(FullHttpRequest request) {
        checkMethod(request.method().name());
        String[] uriParts = request.uri().substring(1).split("/"), routeParts;
        Map<String, RouteMapping> tempRouteMappingMap = routeMappingMap.get(request.method().name()).get(uriParts.length);
        if (tempRouteMappingMap == null) return null;
        for (String route : tempRouteMappingMap.keySet()) {
            routeParts = route.substring(1).split("/");
            for (int i = 0; i < uriParts.length; i++) {
                boolean isCustomParam = routeParts[i].startsWith("{") && routeParts[i].endsWith("}");
                if (isCustomParam && i < uriParts.length - 1) {
                    continue;
                } else if (!routeParts[i].equals(uriParts[i]) && !isCustomParam) {
                    break;
                }
                if (i == uriParts.length - 1) {
                    return tempRouteMappingMap.get(route);
                }
            }
        }
        return null;
    }


    /**
     * Description: 检查当前该映射的http请求方法是否被支持
     * Param: [httpMethod]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    private static void checkMethod(String method) {
        if (method.equals("GET") &&
                method.equals("POST") &&
                method.equals("PUT") &&
                method.equals("DELETE")) {
            throw new MethodNotAllowException();
        }
    }
}
