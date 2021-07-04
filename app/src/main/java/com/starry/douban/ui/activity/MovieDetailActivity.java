package com.starry.douban.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.starry.douban.adapter.MoviePhotoAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityMovieDetailBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.MovieItemDetailBean;
import com.starry.douban.model.PhotoModel;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.RegexHelper;
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

        viewBinding.recyclerView.setVisibility(View.VISIBLE);
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        viewBinding.recyclerView.setAdapter(mAdapter);
        viewBinding.recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewBinding.recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void loadData() {
        HttpManager.get(url)
                .tag(this)
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
                                Logger.e(element.toString());
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
