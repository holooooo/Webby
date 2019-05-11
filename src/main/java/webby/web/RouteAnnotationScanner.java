package webby.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.config.WebbyConfig;
import webby.web.annotation.*;
import webby.web.http.RouteMapping;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * program: webby
 * description: 映射关系扫描器
 * author: Makise
 * create: 2019-04-12 21:35
 **/
public class RouteAnnotationScanner {
    private String packageName;
    private File packageRoot;
    private static final Logger logger = LoggerFactory.getLogger(RouteAnnotationScanner.class);

    public RouteAnnotationScanner() {
        packageName = WebbyConfig.getConfig().getPackageName();
        try {
            packageRoot = new File(ClassLoader.getSystemResource(packageName).toURI());
        } catch (URISyntaxException e) {
            logger.warn("{}", e);
        }
    }

    /**
     * Description: 开始扫描注解, 先从项目根开始遍历
     * Param: []
     * return: void
     * Author: Makise
     * Date: 2019/4/16
     */
    public void scan() {
        getAllController(packageName, this.packageRoot);
    }


    /**
     * Description: 得到当前目录下的所有class文件，找到其中的controller，并存储
     * Param: [path, currentFile]
     * return: void
     * Author: Makise
     * Date: 2019/4/16
     */
    private void getAllController(String path, File currentFile) {
        //只保留当前文件夹下的文件夹和类文件
        File[] files = currentFile.listFiles(pathName -> {
            if (pathName.isDirectory()) {
                return true;
            }
            return pathName.getName().endsWith(".class");
        });

        for (File file : Objects.requireNonNull(files)) {
            String fileName = file.getName();
            String className = path + "." + fileName.replace(".class", "");
            if (file.isDirectory()) {
                getAllController(className, file);
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    RouteMappingStorage.putClass(clazz);
                    getRouteMapping(clazz);
                    logger.debug("Controller {} is loaded", clazz.getName());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                logger.warn("{}", e);
            }
        }
    }

    /**
     * Description: 得到该类下的全部路由映射，并存储
     * Param: [clazz]
     * return: void
     * Author: Makise
     * Date: 2019/4/16
     */
    private void getRouteMapping(Class<?> clazz) {

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            methodHandle(clazz, method);
        }
    }

    /**
     * Description: 获取方法的各种信息，并存储为映射
     * Param: [method]
     * return: void
     * Author: Makise
     * Date: 2019/4/16
     */
    private void methodHandle(Class<?> clazz, Method method) {
        RouteMapping routeMapping = new RouteMapping();

        //得到路由使用的http方法以及地址
        if (method.isAnnotationPresent(Get.class)) {
            routeMapping.setHttpMethod("GET");
            routeMapping.setRoute(method.getAnnotation(Get.class).value());
        } else if (method.isAnnotationPresent(Post.class)) {
            routeMapping.setHttpMethod("POST");
            routeMapping.setRoute(method.getAnnotation(Post.class).value());
        } else if (method.isAnnotationPresent(Put.class)) {
            routeMapping.setHttpMethod("PUT");
            routeMapping.setRoute(method.getAnnotation(Put.class).value());
        } else if (method.isAnnotationPresent(Delete.class)) {
            routeMapping.setHttpMethod("DELETE");
            routeMapping.setRoute(method.getAnnotation(Delete.class).value());
        } else {
            //如果不是路由映射的方法就不处理
            return;
        }
        //先看看是否为需要控制器路由修饰
        if (!routeMapping.getRoute().startsWith("/")) {
            routeMapping.setRoute(clazz.getAnnotation(Controller.class).value() + routeMapping.getRoute());
        }
        //再补全缺失的“/”
        if (!routeMapping.getRoute().startsWith("/")) {
            routeMapping.setRoute("/" + routeMapping.getRoute());
        }
        if (routeMapping.getRoute().endsWith("/")) {
            routeMapping.setRoute(routeMapping.getRoute().substring(0, routeMapping.getRoute().length() - 1));
        }

        //存储类，方法的信息，以及参数的名字
        routeMapping.setClazz(clazz);
        routeMapping.setMethod(method);
        Parameter[] params = method.getParameters();
        routeMapping.setParams(params);

        //得到跨域相关的信息
        String[] allowOrigins = null;
        int maxAge = -1;
        //优先看方法所定义的跨域规则，再看类所定义的跨域规则
        if (method.isAnnotationPresent(CrossOrigin.class)) {
            allowOrigins = method.getAnnotation(CrossOrigin.class).value();
            maxAge = method.getAnnotation(CrossOrigin.class).maxAge();
        } else if (method.getClass().isAnnotationPresent(CrossOrigin.class)) {
            allowOrigins = method.getClass().getAnnotation(CrossOrigin.class).value();
            maxAge = method.getClass().getAnnotation(CrossOrigin.class).maxAge();
        }
        routeMapping.setAllowOrigins(allowOrigins);
        routeMapping.setMaxAge(maxAge);

        RouteMappingStorage.putRouteMapping(routeMapping);
    }
}
