package webby.web.annotation;

import java.lang.annotation.*;

/**
 * program: webby
 * description: http的delete方法
 * author: Makise
 * create: 2019-04-04 19:59
 **/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
@HttpMethodAnnotation
public @interface Delete {
    /*
    * 该方法映射的默认路由地址
    * */
    String value() default "";
    String method() default "DELETE" ;
}
