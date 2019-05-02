package shitty.web;

import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shitty.utils.RequestParser;
import shitty.utils.StringCastToBaseTypes;
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
    private static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);

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
        String route = routeMapping.getRoute(),
                uri = request.uri();
        //如果请求中带有url参数，就删除他们
        int urlParamIndex = uri.indexOf("?");
        if (urlParamIndex != -1){
            uri = uri.substring(0, urlParamIndex);
        }

        //得到url内部的参数，如/user/{id}/info中的id
        int customParamStart = route.indexOf("{");
        if (customParamStart != -1){
            String[] uriPath = uri.substring(customParamStart).split("/"),
                    routePath = route.substring(customParamStart).split("/");
            for (int i = 0; i < routePath.length; i++) {
                if (routePath[i].startsWith("{") && routePath[i].endsWith("}")) {
                    params.put(new String(routePath[i].toCharArray(), 1, routePath[i].length() - 2), uriPath[i]);
                }
            }
        }

        //将参数转换成正确的类型
        Object[] args = new Object[routeMapping.getParams().size()];
        AtomicInteger paramsNums = new AtomicInteger();
        routeMapping.getParams().forEach((k, v) -> args[paramsNums.getAndAdd(1)] = StringCastToBaseTypes.cast(v, params.get(k)));

        return (HttpResponseUtil) routeMapping.getMethod().invoke(RouteMappingStorage.getClass(routeMapping.getClazz()), args);
    }
}
