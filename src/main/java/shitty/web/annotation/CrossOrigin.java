package shitty.web.annotation;

import java.lang.annotation.*;

/**
 * program: shitty
 * description: 允许跨域注解
 * author: Makise
 * create: 2019-04-04 20:00
 **/
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
    /*
    * 允许跨域的源
    * */
    String[] value() default {};

    int maxAge() default -1;
}
