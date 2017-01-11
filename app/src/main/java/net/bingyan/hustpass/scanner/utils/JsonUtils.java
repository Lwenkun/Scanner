package net.bingyan.hustpass.scanner.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;


/**
 * Created by lwenkun on 2016/12/17.
 */

public class JsonUtils {

    private Gson gson;
    private static JsonUtils instance;

    private JsonUtils() {
        gson = new Gson();
    }

    public static JsonUtils getInstance() {

        if (instance == null) {
            synchronized (JsonUtils.class) {
                if (instance == null) {
                    instance = new JsonUtils();
                }
            }
        }

        return instance;

    }

    /**
     * convert json string to a model.
     * @param json json string
     * @param cls class of this model
     * @param <T> type parameter of this class
     * @return model
     */
    public <T> T json2Bean(String json,  Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    /**
     * convert json string to a model.
     * @param json json string
     * @param typeOfT type of this model.
     * @param <T>
     * @return model
     */
    public <T> T json2Bean(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public String bean2Json(Object bean) {
        return gson.toJson(bean);
    }

}
