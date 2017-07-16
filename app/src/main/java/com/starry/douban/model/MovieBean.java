package com.starry.douban.model;

import java.util.List;

/**
 *
 * 电影
 * @author Starry Jerry
 * @since 2016/12/13.
 */
public class MovieBean {
    /**
     * max : 10
     * average : 8.6
     * stars : 45
     * min : 0
     */

    private RatingBean rating;
    /**
     * rating : {"max":10,"average":8.6,"stars":"45","min":0}
     * genres : ["剧情","爱情","动画"]
     * title : 你的名字。
     * casts : [{"alt":"https://movie.douban.com/celebrity/1185637/","avatars":{"small":"http://img3.doubanio.com/img/celebrity/small/13768.jpg","large":"http://img3.doubanio.com/img/celebrity/large/13768.jpg","medium":"http://img3.doubanio.com/img/celebrity/medium/13768.jpg"},"name":"神木隆之介","id":"1185637"},{"alt":"https://movie.douban.com/celebrity/1316660/","avatars":{"small":"http://img3.doubanio.com/img/celebrity/small/1445093052.07.jpg","large":"http://img3.doubanio.com/img/celebrity/large/1445093052.07.jpg","medium":"http://img3.doubanio.com/img/celebrity/medium/1445093052.07.jpg"},"name":"上白石萌音","id":"1316660"},{"alt":"https://movie.douban.com/celebrity/1018667/","avatars":{"small":"http://img7.doubanio.com/img/celebrity/small/36735.jpg","large":"http://img7.doubanio.com/img/celebrity/large/36735.jpg","medium":"http://img7.doubanio.com/img/celebrity/medium/36735.jpg"},"name":"长泽雅美","id":"1018667"}]
     * collect_count : 255268
     * original_title : 君の名は。
     * subtype : movie
     * directors : [{"alt":"https://movie.douban.com/celebrity/1005177/","avatars":{"small":"http://img3.doubanio.com/img/celebrity/small/39258.jpg","large":"http://img3.doubanio.com/img/celebrity/large/39258.jpg","medium":"http://img3.doubanio.com/img/celebrity/medium/39258.jpg"},"name":"新海诚","id":"1005177"}]
     * year : 2016
     * images : {"small":"http://img3.doubanio.com/view/movie_poster_cover/ipst/public/p2395733377.jpg","large":"http://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2395733377.jpg","medium":"http://img3.doubanio.com/view/movie_poster_cover/spst/public/p2395733377.jpg"}
     * alt : https://movie.douban.com/subject/26683290/
     * id : 26683290
     */

    private String title;
    private int collect_count;
    private String original_title;
    private String subtype;
    private String year;
    /**
     * small : http://img3.doubanio.com/view/movie_poster_cover/ipst/public/p2395733377.jpg
     * large : http://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2395733377.jpg
     * medium : http://img3.doubanio.com/view/movie_poster_cover/spst/public/p2395733377.jpg
     */

    private ImagesBean images;
    private String alt;
    private String id;
    private List<String> genres;
    /**
     * alt : https://movie.douban.com/celebrity/1185637/
     * avatars : {"small":"http://img3.doubanio.com/img/celebrity/small/13768.jpg","large":"http://img3.doubanio.com/img/celebrity/large/13768.jpg","medium":"http://img3.doubanio.com/img/celebrity/medium/13768.jpg"}
     * name : 神木隆之介
     * id : 1185637
     */

    private List<CastsBean> casts;
    /**
     * alt : https://movie.douban.com/celebrity/1005177/
     * avatars : {"small":"http://img3.doubanio.com/img/celebrity/small/39258.jpg","large":"http://img3.doubanio.com/img/celebrity/large/39258.jpg","medium":"http://img3.doubanio.com/img/celebrity/medium/39258.jpg"}
     * name : 新海诚
     * id : 1005177
     */

    private List<DirectorsBean> directors;

    public RatingBean getRating() {
        return rating;
    }

    public void setRating(RatingBean rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(int collect_count) {
        this.collect_count = collect_count;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ImagesBean getImages() {
        return images;
    }

    public void setImages(ImagesBean images) {
        this.images = images;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<CastsBean> getCasts() {
        return casts;
    }

    public void setCasts(List<CastsBean> casts) {
        this.casts = casts;
    }

    public List<DirectorsBean> getDirectors() {
        return directors;
    }

    public void setDirectors(List<DirectorsBean> directors) {
        this.directors = directors;
    }

    public static class RatingBean {
        private int max;
        private double average;
        private String stars;
        private int min;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public String getStars() {
            return stars;
        }

        public void setStars(String stars) {
            this.stars = stars;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }
    }

    public static class ImagesBean {
        private String small;
        private String large;
        private String medium;

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }
    }

    public static class CastsBean {
        private String alt;
        /**
         * small : http://img3.doubanio.com/img/celebrity/small/13768.jpg
         * large : http://img3.doubanio.com/img/celebrity/large/13768.jpg
         * medium : http://img3.doubanio.com/img/celebrity/medium/13768.jpg
         */

        private AvatarsBean avatars;
        private String name;
        private String id;

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public AvatarsBean getAvatars() {
            return avatars;
        }

        public void setAvatars(AvatarsBean avatars) {
            this.avatars = avatars;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class AvatarsBean {
            private String small;
            private String large;
            private String medium;

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }

            public String getLarge() {
                return large;
            }

            public void setLarge(String large) {
                this.large = large;
            }

            public String getMedium() {
                return medium;
            }

            public void setMedium(String medium) {
                this.medium = medium;
            }
        }
    }

    public static class DirectorsBean {
        private String alt;
        /**
         * small : http://img3.doubanio.com/img/celebrity/small/39258.jpg
         * large : http://img3.doubanio.com/img/celebrity/large/39258.jpg
         * medium : http://img3.doubanio.com/img/celebrity/medium/39258.jpg
         */

        private AvatarsBean avatars;
        private String name;
        private String id;

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public AvatarsBean getAvatars() {
            return avatars;
        }

        public void setAvatars(AvatarsBean avatars) {
            this.avatars = avatars;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class AvatarsBean {
            private String small;
            private String large;
            private String medium;

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }

            public String getLarge() {
                return large;
            }

            public void setLarge(String large) {
                this.large = large;
            }

            public String getMedium() {
                return medium;
            }

            public void setMedium(String medium) {
                this.medium = medium;
            }
        }
    }
}
