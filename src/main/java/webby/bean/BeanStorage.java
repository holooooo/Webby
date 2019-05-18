package webby.bean;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.bean.annotation.Autowired;
import webby.server.HttpServer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * program: webby
 * description: 存储管理bean对象们
 * author: Makise
 * create: 2019-05-17 20:16
 **/
public class BeanStorage {
    private static final Logger logger = LoggerFactory.getLogger(BeanStorage.class);

    //用来存储对象
    private static Map<String, Object> beansMapping = new HashMap<>(16);
    //存储bean子类与其对应的对象集合
    private static Map<String, List<String>> typeBeansMapping = new HashMap<>(16);
    //需要进行lazyload的bean们
    private static List<String> notLazyList = new ArrayList<>(16);

    /**
     * @return java.lang.Object
     * @Description: 通过类名来得到bean
     * @Author: Makise
     * @CreateDate: 2019/5/17 20:18
     * @params [key]
     */
    public static Object getBean(String key) {
        if (!beansMapping.containsKey(key)) {
            logger.warn("Bean '{}' is not exist", key);
            return null;
        }
        Object bean = beansMapping.get(key);
        if (bean == null) {
            bean = initBean(key);
        }
        return bean;
    }


    /**
     * @return java.lang.Object[]
     * @Description: 得到某一种类型的全部bean
     * @Author: Makise
     * @CreateDate: 2019/5/17 20:19
     * @params [typeName]
     */
    public static Object[] getBeansByType(String typeName) {
        if (!typeBeansMapping.containsKey(typeName)) {
            return null;
        }
        List<String> beanNames = typeBeansMapping.get(typeName);
        Object[] beans = new Object[beanNames.size()];
        for (int i = 0; i < beanNames.size(); i++) {
            beans[i] = getBean(beanNames.get(i));
        }
        return beans;
    }


    /**
     * @return Object
     * @Description: 生成一个bean的实例，会递归调用，直到保证对象内每一个autowired都加载上了
     * @Author: Makise
     * @CreateDate: 2019/5/17 20:42
     * @params [clazzName]
     */
    private static Object initBean(String clazzName) {
        try {
            Class<?> clazz = Class.forName(clazzName);
            Object bean = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(bean, getBean(field.getType().getName()));
                }
            }
            beansMapping.put(clazzName, bean);
            return bean;
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            logger.error("{}", e);
            e.printStackTrace();
        }
        HttpServer.close();
        return null;
    }

    /**
     * @return void
     * @Description: 初始化需要lazyload的bean
     * @Author: Makise
     * @CreateDate: 2019/5/17 20:43
     * @params []
     */
    static void init() {
        for (String name : notLazyList) {
            getBean(name);
        }
        logger.info("beans map is {}",beansMapping.toString());
        logger.info("not Lazy List is {}",notLazyList.toString());
    }

    /**
     * @return void
     * @Description: 将一个bean存储在该类中
     * @Author: Makise
     * @CreateDate: 2019/5/17 20:31
     * @params [beanContent]
     */
    public static void putBean(String clazzName, boolean isLazyload, String type) {
        beansMapping.put(clazzName, null);
        if (!isLazyload) {
            notLazyList.add(clazzName);
        }
        if (!StringUtils.isBlank(type)) {
            List<String> list = typeBeansMapping.getOrDefault(type, new ArrayList<>(16));
            list.add(clazzName);
            typeBeansMapping.put(type, list);
        }
    }

}
