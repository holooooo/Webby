package shitty.web.annotation;

import java.lang.annotation.*;

/**
 * program: shitty
 * description: http的put方法
 * author: Makise
 * create: 2019-04-04 20:00
 **/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface Put {
    /*
     * 该方法映射的默认路由地址
     * */
    String value() default "";
}