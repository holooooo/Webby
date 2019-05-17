package webby.web.annotation;

import webby.bean.annotation.Bean;

import java.lang.annotation.*;

/**
 * program: webby
 * description: 控制器注解类
 * author: Makise
 * create: 2019-04-04 19:57
 **/
@Bean(false)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface Controller {
    /*
    * 该控制器的默认映射的地址
    * */
    String value() default "/";
}
