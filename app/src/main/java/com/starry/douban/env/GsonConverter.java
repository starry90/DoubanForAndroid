package com.starry.douban.env;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starry.http.interfaces.HttpConverter;
import com.starry.http.error.BIZException;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

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
    public <T> T convert(Class<?> cbClass, ResponseBody responseBody) throws Exception {
        T result;
        try {
            // 1. get string
            String json = responseBody.string();
            // 2. check result code
            JSONObject jsonObject = new JSONObject(json);
            int responseCode = jsonObject.optInt("code");
            String responseMsg = jsonObject.optString("msg");
            checkResultCode(responseCode, responseMsg, json);
            // 3. string to T
            Type tType = getType(cbClass);
            if (tType == new TypeToken<String>(){}.getType()) {
                result = (T) json;
            } else {
                result = gson.fromJson(json, tType);
            }
        } finally {
            responseBody.close(); //To avoid leaking resources
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
    private  <T> Type getType(Class<?> getClass) {
        Type type = ((ParameterizedType) getClass.getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class) {//如果是Object直接返回
            return type;
        } else {//如果是集合，获取集合的类型map或list
            return new TypeToken<T>() {
            }.getType();
        }
    }

}
