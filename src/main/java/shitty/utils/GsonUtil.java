package shitty.utils;

import com.google.gson.Gson;

/**
 * program: shitty
 * description: gson的单例
 * author: Makise
 * create: 2019-04-08 21:12
 **/
public class GsonUtil {
    private static  Gson gson = new Gson();

    public static Gson getGson(){
        return gson;
    }

    public static String toJson(Object o){
        return gson.toJson(o);
    }
}
