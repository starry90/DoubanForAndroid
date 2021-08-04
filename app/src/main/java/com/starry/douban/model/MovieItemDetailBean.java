package com.starry.douban.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2021/6/29.
 */

public class MovieItemDetailBean {


    @SerializedName("@context")
    private String _$Context40; // FIXME check this code
    private String name;
    private String url;
    private String image;
    private String datePublished;
    private String duration;
    private String description;
    @SerializedName("@type")
    private String _$Type110; // FIXME check this code
    private AggregateRatingBean aggregateRating;
    private List<PersonBean> director;
    private List<PersonBean> author;
    private List<PersonBean> actor;
    private List<String> genre;

    public String get_$Context40() {
        return _$Context40;
    }

    public void set_$Context40(String _$Context40) {
        this._$Context40 = _$Context40;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String get_$Type110() {
        return _$Type110;
    }

    public void set_$Type110(String _$Type110) {
        this._$Type110 = _$Type110;
    }

    public AggregateRatingBean getAggregateRating() {
        return aggregateRating;
    }

    public void setAggregateRating(AggregateRatingBean aggregateRating) {
        this.aggregateRating = aggregateRating;
    }

    public List<PersonBean> getDirector() {
        return director;
    }

    public void setDirector(List<PersonBean> director) {
        this.director = director;
    }

    public List<PersonBean> getAuthor() {
        return author;
    }

    public void setAuthor(List<PersonBean> author) {
        this.author = author;
    }

    public List<PersonBean> getActor() {
        return actor;
    }

    public void setActor(List<PersonBean> actor) {
        this.actor = actor;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public static class AggregateRatingBean {
        @SerializedName("@type")
        private String _$Type188; // FIXME check this code
        private String ratingCount;
        private String bestRating;
        private String worstRating;
        private String ratingValue;

        public String get_$Type188() {
            return _$Type188;
        }

        public void set_$Type188(String _$Type188) {
            this._$Type188 = _$Type188;
        }

        public String getRatingCount() {
            return ratingCount;
        }

        public void setRatingCount(String ratingCount) {
            this.ratingCount = ratingCount;
        }

        public String getBestRating() {
            return bestRating;
        }

        public void setBestRating(String bestRating) {
            this.bestRating = bestRating;
        }

        public String getWorstRating() {
            return worstRating;
        }

        public void setWorstRating(String worstRating) {
            this.worstRating = worstRating;
        }

        public String getRatingValue() {
            return ratingValue;
        }

        public void setRatingValue(String ratingValue) {
            this.ratingValue = ratingValue;
        }
    }

    public static class PersonBean extends PhotoModel implements Parcelable {
        @SerializedName("@type")
        private String _$Type201; // FIXME check this code
        private String url;
        private String name;

        /**
         * 头像
         */
        private transient String avatarUrl;
        /**
         * 饰演 角色
         */
        private transient String role = "";

        @Override
        public String getPhotoTitle() {
            return name;
        }

        @Override
        public String getPhotoUrl() {
            return avatarUrl;
        }

        public String get_$Type201() {
            return _$Type201;
        }

        public void set_$Type201(String _$Type201) {
            this._$Type201 = _$Type201;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.avatarUrl);
        }

        public PersonBean() {
            super();
        }

        protected PersonBean(Parcel in) {
            super(in);
            this.name = in.readString();
            this.avatarUrl = in.readString();
        }

        public static final Creator<PersonBean> CREATOR = new Creator<PersonBean>() {
            @Override
            public PersonBean createFromParcel(Parcel source) {
                return new PersonBean(source);
            }

            @Override
            public PersonBean[] newArray(int size) {
                return new PersonBean[size];
            }
        };
    }


    /*

{
    "@context": "http://schema.org",
    "name": "肖申克的救赎 The Shawshank Redemption",
    "url": "/subject/1292052/",
    "image": "https://img2.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg",
    "director": [
        {
            "@type": "Person",
            "url": "/celebrity/1047973/",
            "name": "弗兰克·德拉邦特 Frank Darabont"
        }
    ],
    "author": [
        {
            "@type": "Person",
            "url": "/celebrity/1047973/",
            "name": "弗兰克·德拉邦特 Frank Darabont"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1049547/",
            "name": "斯蒂芬·金 Stephen King"
        }
    ],
    "actor": [
        {
            "@type": "Person",
            "url": "/celebrity/1054521/",
            "name": "蒂姆·罗宾斯 Tim Robbins"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1054534/",
            "name": "摩根·弗里曼 Morgan Freeman"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1041179/",
            "name": "鲍勃·冈顿 Bob Gunton"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1000095/",
            "name": "威廉姆·赛德勒 William Sadler"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1013817/",
            "name": "克兰西·布朗 Clancy Brown"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1010612/",
            "name": "吉尔·贝罗斯 Gil Bellows"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1054892/",
            "name": "马克·罗斯顿 Mark Rolston"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1027897/",
            "name": "詹姆斯·惠特摩 James Whitmore"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1087302/",
            "name": "杰弗里·德曼 Jeffrey DeMunn"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1074035/",
            "name": "拉里·布兰登伯格 Larry Brandenburg"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1099030/",
            "name": "尼尔·吉恩托利 Neil Giuntoli"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1343305/",
            "name": "布赖恩·利比 Brian Libby"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1048222/",
            "name": "大卫·普罗瓦尔 David Proval"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1343306/",
            "name": "约瑟夫·劳格诺 Joseph Ragno"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1315528/",
            "name": "祖德·塞克利拉 Jude Ciccolella"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1014040/",
            "name": "保罗·麦克兰尼 Paul McCrane"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1390795/",
            "name": "芮妮·布莱恩 Renee Blaine"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1083603/",
            "name": "阿方索·弗里曼 Alfonso Freeman"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1330490/",
            "name": "V·J·福斯特 V.J. Foster"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1000635/",
            "name": "弗兰克·梅德拉诺 Frank Medrano"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1390797/",
            "name": "马克·迈尔斯 Mack Miles"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1150160/",
            "name": "尼尔·萨默斯 Neil Summers"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1048233/",
            "name": "耐德·巴拉米 Ned Bellamy"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1000721/",
            "name": "布赖恩·戴拉特 Brian Delate"
        },
        {
            "@type": "Person",
            "url": "/celebrity/1333685/",
            "name": "唐·麦克马纳斯 Don McManus"
        }
    ],
    "datePublished": "1994-09-10",
    "genre": [
        "\u72af\u7f6a",
        "\u5267\u60c5"
    ],
    "duration": "PT2H22M",
    "description": "一场谋杀案使银行家安迪（蒂姆•罗宾斯 Tim Robbins 饰）蒙冤入狱，谋杀妻子及其情人的指控将囚禁他终生。在肖申克监狱的首次现身就让监狱“大哥”瑞德（摩根•弗里曼 Morgan Freeman ...",
    "@type": "Movie",
    "aggregateRating": {
        "@type": "AggregateRating",
        "ratingCount": "2381911",
        "bestRating": "10",
        "worstRating": "2",
        "ratingValue": "9.7"
    }
}

     */
}
