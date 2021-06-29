package com.starry.douban.model;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/13.
 */
public class Movies extends BaseModel {

    private List<MovieItemBean> subjects;

    public List<MovieItemBean> getSubjects() {
        return subjects;
    }

    public Movies setSubjects(List<MovieItemBean> subjects) {
        this.subjects = subjects;
        return this;
    }
}
