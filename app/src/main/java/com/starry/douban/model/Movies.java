package com.starry.douban.model;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/13.
 */
public class Movies extends BaseModel {

    private List<MovieBean> subjects;

    public List<MovieBean> getSubjects() {
        return subjects;
    }

    public Movies setSubjects(List<MovieBean> subjects) {
        this.subjects = subjects;
        return this;
    }
}
