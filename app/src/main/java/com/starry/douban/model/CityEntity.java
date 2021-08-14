package com.starry.douban.model;

import java.util.List;

/**
 * Created by xmuSistone on 2017/5/12.
 */

public class CityEntity {

    private Integer code;
    private String msg;
    private List<ResultBean> result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String country;
        private String temperature;
        private String coverImageUrl;
        private String address;
        private String description;
        private String time;
        private String mapImageUrl;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getCoverImageUrl() {
            return coverImageUrl;
        }

        public void setCoverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMapImageUrl() {
            return mapImageUrl;
        }

        public void setMapImageUrl(String mapImageUrl) {
            this.mapImageUrl = mapImageUrl;
        }
    }
}
