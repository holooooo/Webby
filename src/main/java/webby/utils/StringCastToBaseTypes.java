package webby.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webby.web.exception.BadRequestException;

/**
 * program: webby
 * description: 将String转换陈基本类型
 * author: Makise
 * create: 2019-04-17 17:22
 **/
public class StringCastToBaseTypes {
    private static final Logger logger = LoggerFactory.getLogger(StringCastToBaseTypes.class);

    public static Object cast(Class<?> type, String param){
        if (StringUtils.isBlank(param)){
            return null;
        }
        try {
            if (type == String.class){
                return param;
            }else if (type.isPrimitive()){
                if (type.equals(int.class) || type.equals(Integer.class)){
                    return Integer.valueOf(param);
                }else if (type.equals(double.class) || type.equals(Double.class)){
                    return Double.valueOf(param);
                }else if (type.equals(float.class) || type.equals(Float.class)){
                    return Float.valueOf(param);
                }else if (type.equals(char.class) || type.equals(Character.class)){
                    return param.charAt(0);
                }else if (type.equals(byte.class) || type.equals(Byte.class)){
                    return param.getBytes();
                }else if (type.equals(short.class) || type.equals(Short.class)){
                    return Short.valueOf(param);
                }else if (type.equals(long.class) || type.equals(Long.class)){
                    return Long.valueOf(param);
                }else if (type.equals(boolean.class) || type.equals(Boolean.class)){
                    return Boolean.valueOf(param);
                }
            }
        }catch (Exception e){
            throw new BadRequestException();
        }

        return null;
    }
}
