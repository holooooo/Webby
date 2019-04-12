package shitty.web.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import shitty.ShittyApplication;
import shitty.utils.GsonUtil;

import java.io.*;

/**
 * program: shitty
 * description: http响应类，用来快速构建一个响应
 * author: Makise
 * create: 2019-04-02 16:35
 **/
public class HttpResponseUtil {
    private String content = "";
    private FullHttpRequest request;
    private HttpContentType contentType = HttpContentType.PLAIN;
    private HttpStatu httpStatu = HttpStatu.OK;
    //随机文件读写类
    private RandomAccessFile randomAccessFile;
    private String[] allowOrigins;
    private int maxAge;


    public HttpResponseUtil(FullHttpRequest request) {
        this.request = request;
    }


    /**
     * Description: 设置response返回的内容类型，并且返回HttpResponse
     * Param: [contentType]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil setContentType(HttpContentType contentType) {
        this.contentType = contentType;
        return this;
    }


    public boolean isFile() {
        return randomAccessFile != null;
    }

    /**
     * Description: 通过状态码设置response的状态，并且返回HttpResponse
     * Param: [statu]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil setStatu(int statu) {
        this.httpStatu = HttpStatu.getByCode(statu);
        return this;
    }

    /**
     * Description: 通过状态枚举设置response的状态，并且返回HttpResponse
     * Param: [statu]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil setStatu(HttpStatu statu) {
        this.httpStatu = statu;
        return this;
    }

    /**
     * Description: 设置该请求是否允许跨域
     * Param: [allowOrigins, maxAge]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/12
     */
    public HttpResponseUtil setCros(String[] allowOrigins, int maxAge){
        this.allowOrigins = allowOrigins;
        this.maxAge = maxAge;
        return this;
    }

    public HttpResponseUtil setCros(String[] allowOrigins){
        this.allowOrigins = allowOrigins;
        this.maxAge = -1;
        return this;
    }

    /**
     * Description: 返回报错信息
     * Param: [statu]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil error(HttpStatu statu){
        this.httpStatu = statu;
        this.content = "Failure: " + statu.getStatus().toString()+ "\r\n";
        return this;
    }

    /**
     * Description:  设置response中要返回的JSON内容，并且返回HttpResponse
     * Param: [content]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil putJson(Object content) {
        this.content = GsonUtil.getGson().toJson(content);
        this.contentType = HttpContentType.JSON;
        return this;
    }

    /**
     * Description:  设置response中要返回的内容，并且返回HttpResponse
     * Param: [content]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/2
     */
    public HttpResponseUtil putText(String content) {
        this.content = content;
        return this;
    }

    /**
     * Description: 返回一个从本地读取的html网页
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putHtml(String path) {
        if (isFileExistAndGet(path)) {
            return this;
        }
        File html = new File(path);
        byte[] filecontent = new byte[(int) html.length()];
        try {
            FileInputStream in = new FileInputStream(html);
            in.read(filecontent);
            in.close();
            this.content = new String(filecontent, ShittyApplication.config.getStringDecoder().name());
        } catch (IOException e) {
            setStatu(HttpStatu.NOT_FOUND);
            return this;
        }
        this.contentType = HttpContentType.HTML;
        return this;
    }


    /**
     * Description: 提供一个文件地址，并且将该地址以流加入到HttpRespronse中
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/7
     */
    public HttpResponseUtil putFile(String path, HttpContentType contentType) throws IOException {
        if (isFileExistAndGet(path)) {
            return this;
        }

        //创建随机读写类
        try {
            randomAccessFile = new RandomAccessFile(new File(path), "r");
        } catch (FileNotFoundException e) {
            error(HttpStatu.NOT_FOUND);
            return this;
        }
        this.contentType = contentType;
        return this;
    }

    /**
     * Description: 传输文件，内容类型为空的情况默认为Octet-Stream
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putFile(String path) throws IOException {
        return putFile(path, HttpContentType.OCTET_STREAM);
    }

    /**
     * Description: 传输文件，内容类型为图片
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putImg(String path) throws IOException {
        return putFile(path, HttpContentType.IMG);
    }

    /**
     * Description: 传输文件，内容类型为图标
     * Param: [path]
     * return: shitty.web.http.HttpResponseUtil
     * Author: Makise
     * Date: 2019/4/8
     */
    public HttpResponseUtil putIcon(String path) throws IOException {
        return putFile(path, HttpContentType.ICON);
    }

    /**
     * Description: 检查请求方式是否为get以及文件是否存在
     * Param: [path]
     * return: boolean
     * Author: Makise
     * Date: 2019/4/8
     */
    private boolean isFileExistAndGet(String path) {
        //如果不是以get方法请求的
        if (request.method() != HttpMethod.GET) {
            error(HttpStatu.METHOD_NOT_ALLOWED);
            return true;
        }
        File file = new File(path);
        //如果文件不存在或者是隐藏文件或者是文件夹
        if (file.isHidden() || !file.exists() || file.isDirectory()) {
            error(HttpStatu.NOT_FOUND);
            return true;
        }
        return false;
    }


    public String getContent() {
        return content;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public HttpContentType getContentType() {
        return contentType;
    }

    public HttpStatu getHttpStatu() {
        return httpStatu;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public boolean isCors() {
        return allowOrigins.length != 0;
    }

    public String getAllowOrigin(String uri) {
        //如果是允许所有域访问的
        if ("*".equals(allowOrigins[0])){
            return "*";
        }else {
            for (String s : allowOrigins) {
                //如果访问源是允许的域
                if (uri.substring(0, s.length()).equals(s)){
                    return s;
                }
            }
        }
        return "";
    }

    public int getMaxAge() {
        return maxAge;
    }
}

