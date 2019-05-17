package webby.bean.annotation;

import java.lang.annotation.*;

/**
 * program: webby
 * description: Autowired，被标记的对象可以自动引入bean中的对象
 * author: Makise
 * create: 2019-05-17 17:23
 **/
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
