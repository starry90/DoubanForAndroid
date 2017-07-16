package com.starry.douban.model;

import java.io.Serializable;

/**
 * 所有model 基类。
 */
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 5268625605268545266L;

    private int result;

    private String msg;

    /**
     * count : 10
     * start : 0
     * total : 979
     */
    private int count;
    private int start;
    private int total;


    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
