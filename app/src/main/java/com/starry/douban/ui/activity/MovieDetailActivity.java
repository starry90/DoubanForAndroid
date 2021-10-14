package com.starry.douban.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.starry.douban.adapter.CommentAdapter;
import com.starry.douban.adapter.MoviePhotoAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityMovieDetailBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.CommentBean;
import com.starry.douban.model.MovieItemDetailBean;
import com.starry.douban.model.PhotoModel;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.RegexHelper;
import com.starry.douban.util.StringUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.log.Logger;
import com.starry.rx.RxManager;
import com.starry.rx.RxTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 电影详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class MovieDetailActivity extends BaseActivity<ActivityMovieDetailBinding> {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";

    private String url;

    @Override
    public ActivityMovieDetailBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityMovieDetailBinding.inflate(layoutInflater);
    }

    @Override
    protected Drawable getToolbarBackground() {
        return new ColorDrawable(0x00000000); //透明背景
    }

    @Override
    public void initData() {
        setTitle("电影");
        viewBinding.svMovieDetail.requestDisallowInterceptTouchEvent(true);

        url = Apis.MOVIE_DETAIL + getIntent().getStringExtra(EXTRA_MOVIE_ID);
        loadData();
    }

    private void initRecyclerView(final List<MovieItemDetailBean.PersonBean> performerBeanList) {
        MoviePhotoAdapter mAdapter = new MoviePhotoAdapter(performerBeanList);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoDetailActivity.showActivity(MovieDetailActivity.this, new ArrayList<PhotoModel>(performerBeanList), position);
            }
        });

        viewBinding.recyclerView.setNestedScrollingEnabled(false);
        viewBinding.recyclerView.setVisibility(View.VISIBLE);
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        viewBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void loadData() {
        HttpManager.get(url)
                .tag(this)
                .headers("referer", Apis.HOST_DOUBAN)
                .build()
                .enqueue(new StringCallback<String>() {

                    @Override
                    public void onSuccess(String response, Object... obj) {
                        Document parse = Jsoup.parse(response);
                        Elements script = parse.getElementsByTag("script");
                        for (Element child : script) {
                            if (child.outerHtml().contains("application/ld+json")) {
                                showMovieDetail(JsonUtil.toObject(child.data(), MovieItemDetailBean.class));
                                break;
                            }
                        }

                        // <header class="main-hd">
                        //  <a href="https://www.douban.com/people/bighead/" class="avator">
                        //      <img width="24" height="24" src="https://img2.doubanio.com/icon/u1000152-23.jpg"></a>
                        //  <a href="https://www.douban.com/people/bighead/" class="name">大头绿豆</a>
                        //  <span class="allstar50 main-title-rating" title="力荐"></span>
                        //  <span content="2005-05-12" class="main-meta">2005-05-12 20:44:13</span>
                        // </header>


                        //        <div data-cid="1000369">
                        //            <div class="main review-item" id="1000369">
                        //
                        //
                        //
                        //        <header class="main-hd">
                        //            <a href="https://www.douban.com/people/bighead/" class="avator">
                        //                <img width="24" height="24" src="https://img2.doubanio.com/icon/u1000152-23.jpg">
                        //            </a>
                        //
                        //            <a href="https://www.douban.com/people/bighead/" class="name">大头绿豆</a>
                        //
                        //                <span class="allstar50 main-title-rating" title="力荐"></span>
                        //
                        //            <span content="2005-05-12" class="main-meta">2005-05-12 20:44:13</span>
                        //
                        //
                        //        </header>
                        //
                        //
                        //                <div class="main-bd">
                        //
                        //                    <h2><a href="https://movie.douban.com/review/1000369/">十年·肖申克的救赎</a></h2>
                        //
                        //                    <div id="review_1000369_short" class="review-short" data-rid="1000369">
                        //                        <div class="short-content">
                        //                              <p class="spoiler-tip">这篇影评可能有剧透</p>
                        //
                        //                            距离斯蒂芬·金（Stephen King）和德拉邦特（Frank Darabont）们缔造这部伟大的作品已经有十年了。我知道美好的东西想必大家都能感受，但是很抱歉，我的聒噪仍将一如既往。 在我眼里，肖申克的救赎与信念、自由和友谊有关。 ［1］信 念 瑞德（Red）说，希望是危险的东西，是精...
                        //
                        //                            &nbsp;(<a href="javascript:;" id="toggle-1000369-copy" class="unfold" title="展开">展开</a>)
                        //                        </div>
                        //                    </div>
                        //
                        //                    <div id="review_1000369_full" class="hidden">
                        //                        <div id="review_1000369_full_content" class="full-content"></div>
                        //                    </div>
                        //
                        //                    <div class="action">
                        //                        <a href="javascript:;" class="action-btn up" data-rid="1000369" title="有用">
                        //                            <img src="https://img3.doubanio.com/f/zerkalo/536fd337139250b5fb3cf9e79cb65c6193f8b20b/pics/up.png" />
                        //                            <span id="r-useful_count-1000369">
                        //                                    16869
                        //                            </span>
                        //                        </a>
                        //                        <a href="javascript:;" class="action-btn down" data-rid="1000369" title="没用">
                        //                            <img src="https://img3.doubanio.com/f/zerkalo/68849027911140623cf338c9845893c4566db851/pics/down.png" />
                        //                            <span id="r-useless_count-1000369">
                        //                                    701
                        //                            </span>
                        //                        </a>
                        //                        <a href="https://movie.douban.com/review/1000369/#comments" class="reply ">973回应</a>
                        //
                        //                        <a href="javascript:;;" class="fold hidden">收起</a>
                        //                    </div>
                        //                </div>
                        //            </div>
                        //        </div>


                        Elements elementsByAttribute = parse.getElementsByClass("main review-item");

                        ArrayList<CommentBean> movieComments = new ArrayList<>(elementsByAttribute.size());


                        for (Element element : elementsByAttribute) {
                            CommentBean movieComment = new CommentBean();

                            Elements hdElements = element.getElementsByClass("main-hd");
                            if (hdElements.size() > 0) {
                                String hd = hdElements.get(0).toString();

                                Matcher image = RegexHelper.matcher(hd, "src=\"", "\"");
                                if (image.find()) {
                                    movieComment.setUserImageUrl(image.group());
                                }

                                Matcher name = RegexHelper.matcher(hd, "class=\"name\">", "<");
                                if (name.find()) {
                                    movieComment.setUserName(name.group());
                                }

                                Matcher time = RegexHelper.matcher(hd, "main-meta\">", "<");
                                if (time.find()) {
                                    movieComment.setCommentTime(time.group());
                                }

                            }


                            Elements bdElements = element.getElementsByClass("main-bd");
                            if (bdElements.size() > 0) {
                                Element bdElement = bdElements.get(0);
                                String bd = bdElement.toString();

                                Matcher title = RegexHelper.matcher(bd, "/\">", "<");
                                if (title.find()) {
                                    movieComment.setCommentTitle(title.group());
                                }

                                Matcher rid = RegexHelper.matcher(bd, "data-rid=\"", "\"");
                                if (rid.find()) {
                                    movieComment.setRid(rid.group());
                                }

                                Matcher up = RegexHelper.matcher(bd, StringUtils.format("r-useful_count-%s\">", movieComment.getRid()), "<");
                                if (up.find()) {
                                    movieComment.setActionUp(up.group());
                                }

                                Matcher down = RegexHelper.matcher(bd, StringUtils.format("r-useless_count-%s\">", movieComment.getRid()), "<");
                                if (down.find()) {
                                    movieComment.setActionDown(down.group());
                                }

                                Matcher reply = RegexHelper.matcher(bd, "class=\"reply \">", "<");
                                if (reply.find()) {
                                    movieComment.setReply(reply.group());
                                }

                                Matcher content = RegexHelper.matcher(bd, "class=\"short-content\">", "&");
                                if (content.find()) {
                                    movieComment.setCommentShortContent(content.group());
                                }
                            }
                            movieComments.add(movieComment);
                        }
                        Logger.json(JsonUtil.toJson(movieComments));
                        initComment(movieComments);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        super.onAfter(success);
                        hideLoading(success);
                    }
                });
    }

    private void initComment(final List<CommentBean> performerBeanList) {
        CommentAdapter mAdapter = new CommentAdapter(performerBeanList);
        viewBinding.recyclerViewComment.setVisibility(View.VISIBLE);
        viewBinding.recyclerViewComment.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.recyclerViewComment.setAdapter(mAdapter);
        //解决RecyclerView在ScrollView中滑动卡顿问题
        viewBinding.recyclerViewComment.setNestedScrollingEnabled(false);
    }

    private String format(String str) {
        return str.replace("[", "").replace("]", " ").replace(",", " ");
    }

    public void showMovieDetail(MovieItemDetailBean response) {
        ImageManager.getBitmap(viewBinding.ivMovieDetail, response.getImage(), viewBinding.ivMovieDetailBg);
        setMovieTitle(response.getName());
        viewBinding.tvMovieDetailRating.setText(String.valueOf(response.getAggregateRating().getRatingValue()));
        viewBinding.tvMovieDetailRatingCount.setText("（" + response.getAggregateRating().getRatingCount() + "人评）");
        viewBinding.tvMovieDetailGenres.setText(format(response.getGenre().toString()));
        String duration = response.getDuration().replace("PT", "").replace("H", "小时").replace("M", "分钟");
        viewBinding.tvMovieDetailCountries.setText(format(response.getDatePublished()) + " / " + duration);
        viewBinding.tvMovieDetailSummary.setText(response.getDescription());

        List<MovieItemDetailBean.PersonBean> directors = response.getDirector();
        directors.addAll(response.getActor());
        getCelebrities(directors);
    }

    private void setMovieTitle(String titleName) {
        List<String> nameLis = new ArrayList<>(Arrays.asList(titleName.split(" ")));
        String title = nameLis.get(0);
        String originalTitle = title;
        if (nameLis.size() >= 2) {
            nameLis.remove(0);
            originalTitle = format(nameLis.toString());
        }
        viewBinding.tvMovieDetailTitle.setText(title);
        viewBinding.tvMovieDetailOriginalTitle.setText(originalTitle);
    }

    private Disposable disposable;

    /**
     * 获取导演 演员数据
     *
     * @param directors 导演 演员数据
     */
    private void getCelebrities(final List<MovieItemDetailBean.PersonBean> directors) {
        disposable = RxManager.createIO(new RxTask<List<MovieItemDetailBean.PersonBean>>() {

            @Override
            public List<MovieItemDetailBean.PersonBean> run() {
                try {
                    Document parse = Jsoup.connect(url + "/celebrities")
                            .header("referer", Apis.HOST_DOUBAN)
                            .execute()
                            .parse();
                    Elements celebrities = parse.getElementsByClass("celebrity");

                    //<li class="celebrity">
                    //
                    //
                    //  <a href="https://movie.douban.com/celebrity/1047973/" title="弗兰克·德拉邦特 Frank Darabont" class="">
                    //      <div class="avatar" style="background-image: url(https://img3.doubanio.com/view/celebrity/raw/public/p230.jpg)">
                    //    </div>
                    //  </a>
                    //
                    //    <div class="info">
                    //      <span class="name"><a href="https://movie.douban.com/celebrity/1047973/" title="弗兰克·德拉邦特 Frank Darabont" class="name">弗兰克·德拉邦特</a></span>
                    //
                    //      <span class="role" title="导演">导演</span>
                    //
                    //    </div>
                    //  </li>

                    //
                    //<li class="celebrity">
                    //  <a href="https://movie.douban.com/celebrity/1054521/" title="蒂姆·罗宾斯 Tim Robbins" class="">
                    //      <div class="avatar" style="background-image: url(https://img9.doubanio.com/view/celebrity/raw/public/p17525.jpg)">
                    //      </div>
                    //  </a>
                    //     <div class="info">
                    //      <span class="name"><a href="https://movie.douban.com/celebrity/1054521/" title="蒂姆·罗宾斯 Tim Robbins" class="name">蒂姆·罗宾斯 Tim Robbins</a></span>
                    //
                    //      <span class="role" title="演员 Actor (饰 安迪·杜佛兰 Andy Dufresne)">演员 Actor (饰 安迪·杜佛兰 Andy Dufresne)</span>

                    //      <span class="works"> 代表作： <a href="https://movie.douban.com/subject/1292052/" target="_blank" title="肖申克的救赎">肖申克的救赎</a> <a href="https://movie.douban.com/subject/6011805/" target="_blank" title="一九四二">一九四二</a> <a href="https://movie.douban.com/subject/30331959/" target="_blank" title="黑水">黑水</a> </span>
                    //     </div>
                    //  </li>


                    for (MovieItemDetailBean.PersonBean director : directors) {
                        for (Element element : celebrities) {
                            if (element.toString().contains(director.getUrl())) {
//                                Logger.e(element.toString());
                                Elements avatar = element.getElementsByClass("avatar");
                                Matcher matcher = RegexHelper.matcherBracket(avatar.toString());
                                if (matcher.find()) {
                                    director.setAvatarUrl(matcher.group());
                                }

                                Elements role = element.getElementsByClass("info");
                                Matcher matcherRole = RegexHelper.matcherBracket(role.toString());
                                if (matcherRole.find()) {
                                    director.setRole(matcherRole.group());
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return directors;
            }
        }).subscribe(new Consumer<List<MovieItemDetailBean.PersonBean>>() {
            @Override
            public void accept(List<MovieItemDetailBean.PersonBean> directors) throws Exception {
                initRecyclerView(directors);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxManager.dispose(disposable);
    }
}
