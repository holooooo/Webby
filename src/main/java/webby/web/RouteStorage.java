package webby.web;

import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.web.exception.MethodNotAllowException;
import webby.web.http.Route;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * program: webby
 * description: 扫描出来的注解存储类
 * author: Makise
 * create: 2019-04-12 21:36
 **/
public class RouteStorage {
    private static final Logger logger = LoggerFactory.getLogger(RouteStorage.class);

    //存储扫描的controller类, key是类，value是实例
    private static HashMap<Class<?>, Object> classMap;
    //存储扫描出来的映射关系类, key是请求访问方式, value是存储了<存储映射关系的类>的桶，<存储映射关系的类>中key是访问路径, value是映射关系类
    private static Map<String, Map> routeMappingMap;

    // 初始化routeMappingMap
    static {
        classMap = new HashMap<>(16);
        routeMappingMap = new HashMap<>(4);
        routeMappingMap.put("GET", new HashMap<String, Object>(16));
        routeMappingMap.put("POST", new HashMap<String, Object>(16));
        routeMappingMap.put("PUT", new HashMap<String, Object>(16));
        routeMappingMap.put("DELETE", new HashMap<String, Object>(16));
        routeMappingMap.forEach((k, v) -> {
            v.put("{fullUri}", new HashMap<String, Route>(16));
        });
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
     * Param: [httpMethod, route]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    static void putRouteMapping(Route route) {
        checkMethod(route.getHttpMethod());
        String[] uriParts = route.getRoute().substring(1).split("/");
        //得到根目录下的路径存储表
        Map<String, Object> tempRouteMap = routeMappingMap.get(route.getHttpMethod());
        for (int i = 0; i < uriParts.length; i++) {
            //当前路径部分为用户自定义参数就将暂存名改为{uriAttr}，否则使用当前路径部分的名字
            String tempName = uriParts[i].startsWith("{") && uriParts[i].endsWith("}") ? "{uriAttr}" : uriParts[i];
            if (i == uriParts.length - 1) {
                ((Map<String, Route>) tempRouteMap.get("{fullUri}")).put(tempName, route);
                break;
            }

            //如果当前路径部分不是最后一个部分，就进去下一级路径
            tempName = "{uriAttr}".equals(tempName) ? "{uriAttrMap}" : tempName;
            //进入下一级路径时，先看看该路径是否存在，如果不存在就创建一个
            if (!tempRouteMap.containsKey(tempName)) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("{fullUri}", new HashMap<String, Route>(16));
                tempRouteMap.put(tempName, map);
            }
            tempRouteMap = (Map<String, Object>) tempRouteMap.get(tempName);

        }
    }


    /**
     * Description: 得到存储的映射关系类,uri需要去除url参数
     * Param: [httpMethod, route]
     * return: webby.web.http.Route
     * Author: Makise
     * Date: 2019/4/12
     */
    static Route getRouteMapping(FullHttpRequest request) {
        checkMethod(request.method().name());
        String uri = request.uri();
        int urlParamIndex = uri.indexOf("?");
        if (urlParamIndex != -1) {
            uri = uri.substring(0, urlParamIndex);
        }
        String[] uriParts = uri.substring(1).split("/");
        //得到根目录下的路径存储表
        Map<String, Object> tempRouteMap = routeMappingMap.get(request.method().name());
        for (int i = 0; i < uriParts.length && tempRouteMap != null; i++) {
            if (i == uriParts.length - 1) {
                Map tempMap = ((Map<String, Route>) tempRouteMap.get("{fullUri}"));
                if (tempMap.containsKey(uriParts[i])) {
                    return (Route) tempMap.get(uriParts[i]);
                }
                return (Route) tempMap.get("{uriAttr}");
            }
            if (!tempRouteMap.containsKey(uriParts[i])) {
                tempRouteMap = (Map<String, Object>) tempRouteMap.get("{uriAttrMap}");
            } else {
                tempRouteMap = (Map<String, Object>) tempRouteMap.get(uriParts[i]);
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
        if (!method.equals("GET") &&
                !method.equals("POST") &&
                !method.equals("PUT") &&
                !method.equals("DELETE")) {
            throw new MethodNotAllowException();
        }
    }
}
