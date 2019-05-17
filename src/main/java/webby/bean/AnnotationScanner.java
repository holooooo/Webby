package webby.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.bean.annotation.Bean;
import webby.config.WebbyConfig;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * program: webby
 * description: 映射关系扫描器
 * author: Makise
 * create: 2019-04-12 21:35
 **/
public class AnnotationScanner {
    private String packageName;
    private File packageRoot;
    private static final Logger logger = LoggerFactory.getLogger(AnnotationScanner.class);

    public AnnotationScanner() {
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
        BeanStorage.init();
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
            String clazzName = path + "." + fileName.replace(".class", "");
            if (file.isDirectory()) {
                getAllController(clazzName, file);
                continue;
            }
            try {
                Class<?> clazz = Class.forName(clazzName);
                for (Annotation annotation : clazz.getAnnotations()) {
                    boolean isTypeBean = annotation.annotationType().isAnnotationPresent(Bean.class),
                            isLazyload;
                    String type = null;
                    if (annotation instanceof Bean || isTypeBean) {
                        if (isTypeBean) {
                            isLazyload = annotation.annotationType().getAnnotation(Bean.class).value();
                            type = annotation.annotationType().getName();
                        } else {
                            isLazyload = ((Bean) annotation).value();
                        }
                        BeanStorage.putBean(clazzName, isLazyload, type);

                    }

                }
            } catch (ClassNotFoundException e) {
                logger.warn("{}", e);
            }
        }
    }


}
