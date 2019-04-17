package shitty.web;

import io.netty.handler.codec.http.FullHttpRequest;
import shitty.utils.RequestParser;
import shitty.web.exception.NotFoundException;
import shitty.web.http.HttpResponseUtil;
import shitty.web.http.RouteMapping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * program: shitty
 * description: 事务处理器
 * author: Makise
 * create: 2019-04-08 22:18
 **/
public class TransactionHandler {
    /**
     * Description: 通过分析request，找到用户所定义的处理方法
     * Param: [request]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/17
     */
    public static HttpResponseUtil handle(FullHttpRequest request) throws IOException, InvocationTargetException, IllegalAccessException {
        RouteMapping routeMapping = RouteMappingStorage.getRouteMapping(request);
        if (routeMapping == null) throw new NotFoundException();

        //得到request中包含的全部参数
        Map<String, String> params = new RequestParser(request).parse();
        String uri = request.uri(), route = routeMapping.getRoute();
        String[] uriPath = uri.substring(1).split("/"),
                routePath = route.substring(1).split("/");
        //得到url中的参数，如/user/{id}/info中的id
        for (int i = 0; i < routePath.length; i++) {
            if (routePath[i].startsWith("{") && routePath[i].endsWith("}")) {
                params.put(routePath[i].substring(1, routePath[i].length() - 1), uriPath[i]);
            }
        }

        //将参数转换成正确的类型
        Object[] args = new Object[params.size()];
        AtomicInteger paramsNums = new AtomicInteger();
        routeMapping.getParams().forEach((k, v) -> {
            args[paramsNums.get()] = v.cast(params.get(k));
            paramsNums.addAndGet(1);
        });

        return (HttpResponseUtil) routeMapping.getMethod().invoke(RouteMappingStorage.getClass(routeMapping.getClazz()), args);
    }
}
