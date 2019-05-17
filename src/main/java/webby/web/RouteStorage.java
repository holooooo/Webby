package webby.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.bean.BeanStorage;
import webby.web.annotation.*;
import webby.web.http.Route;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    //存储扫描出来的映射关系类, key是请求访问方式, value是存储了<存储映射关系的类>的桶，<存储映射关系的类>中key是访问路径, value是映射关系类
    private static Map<String, Map> routeMappingMap;

    // 初始化routeMappingMap
    static {
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
     * Description: 存储扫描出来的映射关系
     * Param: [httpMethod, route]
     * return: void
     * Author: Makise
     * Date: 2019/4/12
     */
    public static void putRouteMapping(Route route) {
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
    public static Route initRouteMapping(String uri, String method) {
        String[] uriParts = uri.substring(1).split("/");
        //得到根目录下的路径存储表
        Map<String, Object> tempRouteMap = routeMappingMap.get(method);
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


    public static void init(){
        Object[] beans = BeanStorage.getBeansByType(Controller.class.getName());
        for (Object bean: beans){
            for (Method method: bean.getClass().getMethods()){
                methodHandle(bean, method);
            }
            logger.debug("Controller {} is loaded", bean.getClass().getName());
        }
    }

    /**
     * Description: 得到该类下的全部路由映射，并存储
     * Param: [clazz]
     * return: void
     * Author: Makise
     * Date: 2019/4/16
     */
    public static void initRouteMapping(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            methodHandle(clazz, method);
        }
    }

    /**
     * Description: 获取方法的各种信息，并存储为映射
     * Param: [method]
     * return: void
     * Author: Makise
     * Date: 2019/4/16
     */
    public static void methodHandle(Object bean, Method method) {
        Route route = new Route();

        //得到路由使用的http方法以及地址
        if (method.isAnnotationPresent(Get.class)) {
            route.setHttpMethod("GET");
            route.setRoute(method.getAnnotation(Get.class).value());
        } else if (method.isAnnotationPresent(Post.class)) {
            route.setHttpMethod("POST");
            route.setRoute(method.getAnnotation(Post.class).value());
        } else if (method.isAnnotationPresent(Put.class)) {
            route.setHttpMethod("PUT");
            route.setRoute(method.getAnnotation(Put.class).value());
        } else if (method.isAnnotationPresent(Delete.class)) {
            route.setHttpMethod("DELETE");
            route.setRoute(method.getAnnotation(Delete.class).value());
        } else {
            //如果不是路由映射的方法就不处理
            return;
        }
        //先看看是否为需要控制器路由修饰
        if (!route.getRoute().startsWith("/")) {
            String frontRoute = bean.getClass().getAnnotation(Controller.class).value();
            frontRoute = frontRoute.endsWith("/") ? frontRoute : frontRoute + "/";
            route.setRoute(frontRoute + route.getRoute());
        }
        //再补全缺失的“/”
        if (!route.getRoute().startsWith("/")) {
            route.setRoute("/" + route.getRoute());
        }
        if (route.getRoute().endsWith("/")) {
            route.setRoute(route.getRoute().substring(0, route.getRoute().length() - 1));
        }

        //存储类，方法的信息，以及参数的名字
        route.setInstance(bean);
        route.setMethod(method);
        Parameter[] params = method.getParameters();
        route.setParams(params);

        //得到跨域相关的信息
        String[] allowOrigins = null;
        int maxAge = -1;
        //优先看方法所定义的跨域规则，再看类所定义的跨域规则
        if (method.isAnnotationPresent(CrossOrigin.class)) {
            allowOrigins = method.getAnnotation(CrossOrigin.class).value();
            maxAge = method.getAnnotation(CrossOrigin.class).maxAge();
        } else if (method.getClass().isAnnotationPresent(CrossOrigin.class)) {
            allowOrigins = method.getClass().getAnnotation(CrossOrigin.class).value();
            maxAge = method.getClass().getAnnotation(CrossOrigin.class).maxAge();
        }
        route.setAllowOrigins(allowOrigins);
        route.setMaxAge(maxAge);

        RouteStorage.putRouteMapping(route);
    }
}
