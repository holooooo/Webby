package webby.bean.annotation;

import java.lang.annotation.*;

/**
 * program: webby
 * description: bean注解，用来标记bean
 * author: Makise
 * create: 2019-05-17 17:22
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    /**
     * Value boolean.
     *
     * @return the boolean
     */
//是否开启懒加载，默认为是
    boolean value() default true;
}
