package webby.web.annotation;

import java.lang.annotation.*;

/**
 * Description: 用来获取方法的参数信息
 * Author: Makise
 * Date: 2019/4/16
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String value();
}
