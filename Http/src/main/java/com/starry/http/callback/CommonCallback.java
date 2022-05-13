package com.starry.http.callback;


import com.starry.http.error.ErrorModel;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 网络请求回调类
 * <p>
 * 调用顺序如下：
 * <li>成功： {@link #parseResponse} -> {@link #onAfter} -> {@link #onSuccess}</li>
 * <li>上传： {@link #inProgress} -> {@link #parseResponse} -> {@link #onAfter} -> {@link #onSuccess}</li>
 * <li>失败： {@link #onAfter} -> {@link #onFailure}</li>
 * <li>取消： {@link #onAfter}</li>
 *
 * @param <T> 解析的对象
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public abstract class CommonCallback<T> {

    public final static CommonCallback<String> NO_CALLBACK = new StringCallback<String>() {
        @Override
        public void onSuccess(String response, Object... obj) {

        }

        @Override
        public void onFailure(ErrorModel errorModel) {

        }
    };

    /**
     * parse {@link Response}
     *
     * @param responseBody {@link ResponseBody}
     * @throws Exception 解析异常信息
     */
    public abstract T parseResponse(ResponseBody responseBody) throws Exception;

    /**
     * @param response 返回的对象
     * @param obj      可扩展参数
     */
    public abstract void onSuccess(T response, Object... obj);

    /**
     * @param errorModel ErrorModel
     */
    public abstract void onFailure(ErrorModel errorModel);

    /**
     * 网络请求结束
     *
     * @param success 请求状态标记，true表示请求成功结果正确
     */
    public void onAfter(boolean success) {
    }

    /**
     * @param progress 进度
     * @param total    总长度
     */
    public void inProgress(float progress, long total) {
    }

    /**/
    //获取Json对象的类型，因为数据可能是Json数组也可能是Json对象
    /*
    public Type getType() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class) {//如果是Object直接返回
            return type;
        } else if (type instanceof ParameterizedType) { // 泛型
            String rawType = ((ParameterizedType) type).getRawType().toString();
            String baseName = BaseModel.class.getName();
            if (rawType.contains(baseName)) { //自定义泛型 BaseModel<App>, BaseModel<Map<String,String>>
                return type;
            }
        }
        //如果是集合，获取集合的类型map或list
        return new TypeToken<T>() {}.getType();
    }*/

}