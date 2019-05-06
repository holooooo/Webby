package webby.web.http;

import webby.config.WebbyConfig;

public enum HttpContentType {
    //未知格式的文件
    OCTET_STREAM("application/octet-stream;charset="),
    HTML("text/html;charset="),
    ICON("image/x-icon;charset="),
    PLAIN("text/plain;charset="),
    TIF("image/tiff;charset="),
    JSON("application/json;charset="),
    XML("text/xml;charset="),
    CSS("text/css;charset="),
    JPEG("image/jpeg;charset="),
    MP3("audio/mp3;charset="),
    MPEG("video/x-mpeg;charset="),
    IMG("application/x-img;charset="),
    ;
    private String value;

    private HttpContentType(String value){
        this.value = value;
    }

    public String getValue() {
        return value + WebbyConfig.getConfig().getCharset().name();
    }

    @Override
    public String toString(){
        return getValue();
    }
}
