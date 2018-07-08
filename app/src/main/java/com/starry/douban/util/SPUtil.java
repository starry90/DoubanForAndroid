package com.starry.douban.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.starry.douban.base.BaseApp;

/**
 * SharedPreferences 本地文件存储（形式：键值对）工具类
 * Create by Starry Jerry on 2016-7-5 21:51:16
 */
@SuppressWarnings("unused")
public class SPUtil {

    private static final String SP_DEFAULT = "sp_default";

    /**
     * 保存字段到本地默认文件 默认保留到XXX文件下
     *
     * @param key   保存的key值
     * @param value 保存的value值
     */
    public static void putString(String key, String value) {
        putString(key, value, SP_DEFAULT);
    }

    /**
     * 保存字段到本地默认文件 默认保留到XXX文件下
     *
     * @param key   保存的key值
     * @param value 保存的value值
     */
    public static void putInt(String key, int value) {
        putInt(key, value, SP_DEFAULT);
    }

    /**
     * 保存字段到本地默认文件 默认保留到XXX文件下
     *
     * @param key   保存的key值
     * @param value 保存的value值
     */
    public static void putFloat(String key, float value) {
        putFloat(key, value, SP_DEFAULT);
    }

    /**
     * 保存字段到本地默认文件 默认保留到XXX文件下
     *
     * @param key   保存的key值
     * @param value 保存的value值
     */
    public static void putLong(String key, long value) {
        putLong(key, value, SP_DEFAULT);
    }

    /**
     * 保存字段到本地默认文件 默认保留到XXX文件下
     *
     * @param key   保存的key值
     * @param value 保存的value值
     */
    public static void putBoolean(String key, boolean value) {
        putBoolean(key, value, SP_DEFAULT);
    }

    /**
     * 获取保存的字段值
     *
     * @param key 保存的key值
     * @return String 保存的value值
     */
    public static String getString(String key) {
        return getString(key, SP_DEFAULT);
    }

    /**
     * 获取保存的字段值
     *
     * @param key 保存的key值
     * @return int 保存的value值
     */
    public static int getInt(String key) {
        return getInt(key, SP_DEFAULT);
    }

    /**
     * 获取保存的字段值
     *
     * @param key 保存的key值
     * @return float 保存的value值
     */
    public static float getFloat(String key) {
        return getFloat(key, SP_DEFAULT);
    }

    /**
     * 获取保存的字段值
     *
     * @param key 保存的key值
     * @return long 保存的value值
     */
    public static long getLong(String key) {
        return getLong(key, SP_DEFAULT);
    }

    /**
     * 获取保存的字段值
     *
     * @param key 保存的key值
     * @return boolean 保存的value值
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, SP_DEFAULT);
    }

    /********************************************************************************/

    /**
     * 获取SharedPreferences对象
     *
     * @param fileName preferences file name
     * @return SharedPreferences
     */
    private static SharedPreferences getSharedPreferences(String fileName) {
        return BaseApp.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 保存字段到指定文件
     *
     * @param key      保存的key值
     * @param obj      保存的obj对象
     * @param fileName 保存的文件名称
     */
    public static void putObject(String key, Object obj, String fileName) {
        putString(key, new Gson().toJson(obj), fileName);
    }

    /**
     * 保存字段到指定文件
     *
     * @param key      保存的key值
     * @param value    保存的value值
     * @param fileName 保存的文件名称
     */
    public static void putString(String key, String value, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存字段到指定文件
     *
     * @param key      保存的key值
     * @param value    保存的value值
     * @param fileName 保存的文件名称
     */
    public static void putInt(String key, int value, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存字段到指定文件
     *
     * @param key      保存的key值
     * @param value    保存的value值
     * @param fileName 保存的文件名称
     */
    public static void putFloat(String key, float value, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 保存字段到指定文件
     *
     * @param key      保存的key值
     * @param value    保存的value值
     * @param fileName 保存的文件名称
     */
    public static void putLong(String key, long value, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 保存字段到指定文件
     *
     * @param key      保存的key值
     * @param value    保存的value值
     * @param fileName 保存的文件名称
     */
    public static void putBoolean(String key, boolean value, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取指定文件下的保存的字段值
     *
     * @param key      保存的key值
     * @param fileName 保存的文件名称
     * @return String 保存的value值
     */
    public static <T> T getObject(String key, String fileName, Class<T> tClass) {
        String string = getString(key, fileName);
        try {
            return new Gson().fromJson(string, tClass);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定文件下的保存的字段值
     *
     * @param key      保存的key值
     * @param fileName 保存的文件名称
     * @return String 保存的value值
     */
    public static String getString(String key, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        return sp.getString(key, null);
    }

    /**
     * 获取指定文件下的保存的字段值
     *
     * @param key      保存的key值
     * @param fileName 保存的文件名称
     * @return int 保存的value值
     */
    public static int getInt(String key, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        return sp.getInt(key, 0);
    }

    /**
     * 获取指定文件下的保存的字段值
     *
     * @param key      保存的key值
     * @param fileName 保存的文件名称
     * @return float 保存的value值
     */
    public static float getFloat(String key, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        return sp.getFloat(key, 0);
    }

    /**
     * 获取指定文件下的保存的字段值
     *
     * @param key      保存的key值
     * @param fileName 保存的文件名称
     * @return long 保存的value值
     */
    public static long getLong(String key, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        return sp.getLong(key, 0);
    }

    /**
     * 获取指定文件下的保存的字段值
     *
     * @param key      保存的key值
     * @param fileName 保存的文件名称
     * @return boolean 保存的value值
     */
    public static boolean getBoolean(String key, String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        return sp.getBoolean(key, false);
    }

    /**
     * 删除指定文件夹下的所有字段
     *
     * @param fileName 文件名称
     * @return boolean 操作是否成功
     */
    public static boolean clearFile(String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName);
        return sp.edit().clear().commit();
    }

}
