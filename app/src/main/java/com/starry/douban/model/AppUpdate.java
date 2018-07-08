package com.starry.douban.model;

import com.starry.douban.constant.PreferencesName;
import com.starry.douban.util.SPUtil;

/**
 * @author Starry Jerry
 * @since 2018/7/2.
 */

public class AppUpdate extends BaseModel {

    /**
     * 文件长度
     */
    private long contentLength;

    /**
     * 版本号
     */
    private int versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 1为强制更新
     */
    private int force;

    /**
     * apk下载地址
     */
    private String apkUrl;
    /**
     * 其它信息
     */
    private String other;

    public long getContentLength() {
        return contentLength;
    }

    public AppUpdate setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public AppUpdate setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public AppUpdate setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public int getForce() {
        return force;
    }

    public AppUpdate setForce(int force) {
        this.force = force;
        return this;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public AppUpdate setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
        return this;
    }

    public String getOther() {
        return other;
    }

    public AppUpdate setOther(String other) {
        this.other = other;
        return this;
    }

    public void savePreferences() {
        SPUtil.putString(PreferencesName.APP_UPDATE_MESSAGE, getMsg());
        SPUtil.putLong(PreferencesName.APP_UPDATE_CONTENT_LENGTH, contentLength);
        SPUtil.putInt(PreferencesName.APP_UPDATE_VERSION_CODE, versionCode);
        SPUtil.putString(PreferencesName.APP_UPDATE_VERSION_NAME, versionName);
        SPUtil.putInt(PreferencesName.APP_UPDATE_FORCE, force);
        SPUtil.putString(PreferencesName.APP_UPDATE_APK_URL, apkUrl);
        SPUtil.putString(PreferencesName.APP_UPDATE_OTHER, other);
    }

}
