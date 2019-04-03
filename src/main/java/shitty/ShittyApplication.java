package shitty;

import lombok.Data;

import java.nio.charset.Charset;

/**
 * program: shitty
 * description: 程序的入口，新的项目应该继承这个类，并通过这个类的run方法启动项目
 * author: Makise
 * create: 2019-04-02 22:31
 **/
@Data
public  class ShittyApplication {
    //端口号默认为8888
    private static int port = 8888;
    //字符编码默认为utf-8
    private static Charset stringDecoder = Charset.forName("UTF-8");
    //controller包
    private static String controllerPackage;


    public void run(int port, String charset, String controllerPackage){

    }
}
