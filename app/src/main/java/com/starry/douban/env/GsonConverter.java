package com.starry.douban.env;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starry.douban.model.BaseModel;
import com.starry.douban.model.GankBaseModel;
import com.starry.http.error.BIZException;
import com.starry.http.interfaces.HttpConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Starry Jerry
 * @since 18-8-7.
 */
public class GsonConverter implements HttpConverter {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverter create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static GsonConverter create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new GsonConverter(gson);
    }

    private final Gson gson;

    private GsonConverter(Gson gson) {
        this.gson = gson;
    }


    @Override
    public RequestBody convert(Object value) {
        String content = "";
        try {
            if (value instanceof String) {
                content = (String) value;
            } else {
                content = gson.toJson(value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RequestBody.create(MEDIA_TYPE, content);
    }

    @Override
    public <T> T convert(Class<?> cbClass, Response response) throws Exception {
        T result;
        try {
            // 1. get string
            String json = response.body().string();
            // 2. string to T
            Type tType = getType(cbClass);
            if (tType == new TypeToken<String>() {
            }.getType()) {
                result = (T) json;
            } else {
                result = gson.fromJson(json, tType);
            }
            // 3. check result code
            if (result instanceof BaseModel) {
                BaseModel baseModel = (BaseModel) result;
                checkResultCode(baseModel.getCode(), baseModel.getMsg(), json);
            }
        } finally {
            response.close(); //To avoid leaking resources
        }
        return result;
    }

    /**
     * 检查返回结果code
     *
     * @param code    错误码
     * @param message 错误信息
     * @throws BIZException 业务异常
     */
    private void checkResultCode(int code, String message, String response) throws BIZException {
        if (code != 0) {// 服务返回结果code不等于0，请求得到的数据有问题
            throw new BIZException(code, message).setResponse(response);
        }
    }

    /**
     * 获取Json对象的类型，因为数据可能是Json数组也可能是Json对象
     */
    private <T> Type getType(Class<?> getClass) {
        Type type = ((ParameterizedType) getClass.getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class) {//如果是Object直接返回
            return type;
        } else if (type instanceof ParameterizedType) { // 泛型
            String rawType = ((ParameterizedType) type).getRawType().toString();
            String baseName = GankBaseModel.class.getName();
            if (rawType.contains(baseName)) { //自定义泛型 GankBaseModel<App>
                return type;
            }
        }
        //如果是集合，获取集合的类型map或list
        return new TypeToken<T>() {
        }.getType();
    }

}
