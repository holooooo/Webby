package shitty.utils;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import shitty.web.exception.NotAllowMethodException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * program: shitty
 * description: 请求解析类
 * author: Makise
 * create: 2019-04-13 23:18
 **/
public class RequestParser {
    private FullHttpRequest fullReq;
    public RequestParser(FullHttpRequest req) {
        this.fullReq = req;
    }

    /**
     * Description: 解析请求
     * Param: []
     * return: java.util.Map<java.lang.String,java.lang.String>
     * Author: Makise
     * Date: 2019/4/13
     */
    public Map<String, String> parse() throws IOException {
        HttpMethod method = fullReq.method();

        Map<String, String> parmMap = new HashMap<>(16);

        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().entrySet().forEach( entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(entry.getKey(), entry.getValue().get(0));
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
            throw new NotAllowMethodException();
        }

        return parmMap;
    }
}
