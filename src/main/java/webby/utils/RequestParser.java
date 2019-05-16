package webby.utils;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import webby.web.exception.MethodNotAllowException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * program: webby
 * description: 请求解析类
 * author: Makise
 * create: 2019-04-13 23:18
 **/
public class RequestParser {
    /**
     * Description: 解析请求
     * Param: []
     * return: java.util.Map<java.lang.String,java.lang.String>
     * Author: Makise
     * Date: 2019/4/13
     */
    public static Map<String, String> parse(FullHttpRequest fullReq) throws IOException {
        HttpMethod method = fullReq.method();

        Map<String, String> parmMap = new HashMap<>(16);

        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().forEach((key, value) -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(key, value.get(0));
            });
        } else if (HttpMethod.POST == method || HttpMethod.PUT == method || HttpMethod.DELETE == method) {
            // 是POST请求
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
            decoder.offer(fullReq);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                parmMap.put(data.getName(), data.getValue());
            }

        } else {
            // 不支持其它方法
            throw new MethodNotAllowException();
        }

        return parmMap;
    }
}
