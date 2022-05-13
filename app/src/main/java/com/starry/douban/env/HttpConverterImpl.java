package com.starry.douban.env;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.starry.douban.model.BaseModel;
import com.starry.douban.model.GankBaseModel;
import com.starry.http.error.BIZException;
import com.starry.http.error.ErrorModel;
import com.starry.http.error.HttpStatusException;
import com.starry.http.interfaces.HttpConverter;
import com.starry.log.Logger;

import org.json.JSONException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

/**
 * @author Starry Jerry
 * @since 18-8-7.
 */
public class HttpConverterImpl implements HttpConverter {

    private static final String TAG = "HttpConverterImpl";

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static HttpConverterImpl create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static HttpConverterImpl create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new HttpConverterImpl(gson);
    }

    private final Gson gson;

    private HttpConverterImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> T responseBodyConverter(Class<?> cbClass, String bodyString) throws Exception {
        T result;
        // string to T
        Type tType = getType(cbClass);
        if (tType == new TypeToken<String>() {
        }.getType()) {
            result = (T) bodyString;
        } else {
            result = gson.fromJson(bodyString, tType);
        }
        // check result code
        if (result instanceof BaseModel) {
            BaseModel baseModel = (BaseModel) result;
            checkResultCode(baseModel.getCode(), baseModel.getMsg(), bodyString);
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

    @Override
    public ErrorModel responseErrorConverter(Exception why, String url) {
        ErrorModel errorModel = new ErrorModel();
        if (why instanceof BIZException) { //业务异常
            BIZException cloneWhy = (BIZException) why;
            errorModel.setCode(cloneWhy.getErrorCode())
                    .setMessage(cloneWhy.getErrorMessage())
                    .setResponse(cloneWhy.getResponse());
            errorModel.setResponse(cloneWhy.getResponse());
            Logger.e(TAG, String.format("%s %s %s", url, errorModel.getResponse(), errorModel.getMessage()));
            return handleBIZ(errorModel);
        } else if (why instanceof HttpStatusException) { //网络状态码错误
            return errorModel.setCode(10001).setMessage("网络状态码错误");
        } else if (why instanceof JsonParseException || why instanceof JSONException) { //JSON报文错误
            return errorModel.setCode(10002).setMessage("JSON报文错误");
        } else if (why instanceof SocketTimeoutException) { //超时提示
            return errorModel.setCode(10003).setMessage("超时提示");
        } else if (!AppWrapper.getInstance().networkAvailable()) { //无网提示
            return errorModel.setCode(10004).setMessage("无网提示");
        } else { //其它错误提示
            return errorModel.setCode(10005).setMessage("未知错误");
        }
    }

    /**
     * 处理错误的业务
     *
     * @param errorModel ErrorModel
     * @return ErrorModel
     */
    private ErrorModel handleBIZ(ErrorModel errorModel) {
        return errorModel;
    }

}
