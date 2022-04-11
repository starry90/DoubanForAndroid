package com.starry.douban.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import com.starry.douban.R;
import com.starry.douban.adapter.CommentAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityBookDetailBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.BookDetailBean;
import com.starry.douban.model.CommentBean;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.RegexHelper;
import com.starry.douban.util.StringUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.log.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 书籍详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class BookDetailActivity extends BaseActivity<ActivityBookDetailBinding> {

    public static final String EXTRA_BOOK_URL = "extra_book_url";

    private String url;

    public static void showActivity(Activity context, String bookUrl) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(EXTRA_BOOK_URL, bookUrl);
        context.startActivity(intent);
    }

    @Override
    public ActivityBookDetailBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityBookDetailBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        url = getIntent().getStringExtra(EXTRA_BOOK_URL);
        loadData();
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
                        //<div id="content">
                        // <div class="grid-16-8 clearfix">
                        //  <div class="article">
                        //   <div class="indent">
                        //    <div class="subjectwrap clearfix">
                        //     <div class="subject clearfix">
                        //      <div id="mainpic" class="">
                        //          <a class="nbg" href="https://img9.doubanio.com/view/subject/l/public/s27279654.jpg" title="活着">
                        //              <img src="https://img9.doubanio.com/view/subject/s/public/s27279654.jpg" title="点击看大图" alt="活着" rel="v:photo" style="max-width: 135px;max-height: 200px;">
                        //          </a>
                        //      </div>
                        //      <div id="info" class="">
                        //      <span> <span class="pl"> 作者</span>: <a class="" href="/author/4503668">余华</a> </span>
                        //       <br> <span class="pl">出版社:</span> 作家出版社
                        //       <br> <span class="pl">出版年:</span> 2012-8-1
                        //       <br> <span class="pl">页数:</span> 191
                        //       <br> <span class="pl">定价:</span> 20.00元
                        //       <br> <span class="pl">装帧:</span> 平装
                        //       <br> <span class="pl">丛书:</span>&nbsp;<a href="https://book.douban.com/series/1634">余华作品（2012版）</a>
                        //       <br> <span class="pl">ISBN:</span> 9787506365437
                        //       <br>
                        //      </div>
                        //     </div>
                        //     <div id="interest_sectl" class="">
                        //      <div class="rating_wrap clearbox" rel="v:rating">
                        //       <div class="rating_logo">
                        //        豆瓣评分
                        //       </div>
                        //       <div class="rating_self clearfix" typeof="v:Rating"> <strong class="ll rating_num " property="v:average"> 9.4 </strong> <span property="v:best" content="10.0"></span>
                        //        <div class="rating_right ">
                        //         <div class="ll bigstar45"></div>
                        //         <div class="rating_sum">
                        //          <span class=""> <a href="comments" class="rating_people"><span property="v:votes">611612</span>人评价</a> </span>
                        //         </div>
                        //        </div>
                        // …………
                        //      <div class="intro">
                        //       <p>《活着(新版)》讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于赌光了家业，一贫如洗。他的父亲被他活活气死，母亲则在穷困中患了重病，福贵前去求药，却在途中被国民党抓去当壮丁。经过几番波折回到家里，才知道母亲早已去世，妻子家珍含辛茹苦地养大两个儿女。此后更加悲惨的命运一次又一次降临到福贵身上，他的妻子、儿女和孙子相继死去，最后只剩福贵和一头老牛相依为命，但老人依旧活着，仿佛比往日更加洒脱与坚强。</p>
                        //       <p>《活着(新版)》荣获意大利格林扎纳•卡佛文学奖最高奖项（1998年）、台湾《中国时报》10本好书奖（1994年）、香港“博益”15本好书奖（1994年）、第三届世界华文“冰心文学奖”（2002年），入选香港《亚洲周刊》评选的“20世纪中文小说百年百强”、中国百位批评家和文学编辑评选的“20世纪90年代最有影响的10部作品”。</p>
                        //      </div>
                        // …………
                        //      <div class="intro">
                        //       <p>余华，1960年出生，1983年开始写作。至今已经出版长篇小说4部，中短篇小说集6部，随笔集4部。主要作品有《兄弟》《活着》《许三观卖血记》《在细雨中呼喊》等。其作品已被翻译成20多种语言在美国、英国、法国、德国、意大利、西班牙、荷兰、瑞典、挪威、希腊、俄罗斯、保加利亚、匈牙利、捷克、塞尔维亚、斯洛伐克、波兰、巴西、以色列、日本、韩国、越南、泰国和印度等国出版。曾获意大利格林扎纳·卡佛文学奖（1998年）、法国文学和艺术骑士勋章（2004年）、中华图书特殊贡献奖（2005年）、法国国际信使外国小说奖（2008年）等。</p>
                        //      </div>
                        // …………
                        //      <div data-cid="5825853">
                        //       <div class="main review-item" id="5825853">
                        //
                        //        <header class="main-hd">
                        //          <a href="https://www.douban.com/people/63250571/" class="avator">
                        //              <img width="24" height="24" src="https://img2.doubanio.com/icon/u63250571-2.jpg">
                        //          </a>
                        //          <a href="https://www.douban.com/people/63250571/" class="name">陈莫尼</a>
                        //
                        //          <span class="allstar50 main-title-rating" title="力荐"></span>
                        //
                        //          <span content="2013-03-29" class="main-meta">2013-03-29 14:10:26</span>
                        //        </header>
                        //        <div class="main-bd">
                        //         <h2><a href="https://book.douban.com/review/5825853/">生命不能承受之轻，能接受之痛</a></h2>
                        //         <div id="review_5825853_short" class="review-short" data-rid="5825853">
                        //          <div class="short-content">
                        //            当我跳进那个世界之前，我依然暗示着自己要怀着理智来阐述自己想说的心情，客观地评价这部作品。直到我翻完最后一页，我在余华沉静的笔墨中渐渐失去了耐心，我甚至迫不及待地想要写点什么，不需要充满张力的语言却也能让别人感受到此时此刻我的满腔热情。 我记得有人说过；当...
                        //            &nbsp;(<a href="javascript:;" id="toggle-5825853-copy" class="unfold" title="展开">展开</a>)
                        //          </div>
                        //         </div>
                        //         <div id="review_5825853_full" class="hidden">
                        //          <div id="review_5825853_full_content" class="full-content"></div>
                        //         </div>
                        //         <div class="action">
                        //          <a href="javascript:;" class="action-btn up" data-rid="5825853" title="有用">
                        //              <img src="https://img3.doubanio.com/f/zerkalo/536fd337139250b5fb3cf9e79cb65c6193f8b20b/pics/up.png">
                        //              <span id="r-useful_count-5825853"> 5162 </span>
                        //          </a>
                        //          <a href="javascript:;" class="action-btn down" data-rid="5825853" title="没用">
                        //              <img src="https://img3.doubanio.com/f/zerkalo/68849027911140623cf338c9845893c4566db851/pics/down.png">
                        //              <span id="r-useless_count-5825853"> 230 </span>
                        //          </a>
                        //          <a href="https://book.douban.com/review/5825853/#comments" class="reply ">244回应</a>
                        //          <a href="javascript:;;" class="fold hidden">收起</a>
                        //         </div>
                        //        </div>
                        //       </div>
                        //      </div>

                        Document parse = Jsoup.parse(response);
                        Element contentElement = parse.select("div#content").first();

                        Element nbgElement = contentElement.select("div#mainpic > a.nbg").first();
                        String href = nbgElement.attr("href");
                        String title = nbgElement.attr("title");

                        BookDetailBean bookDetailBean = new BookDetailBean();
                        bookDetailBean.setImageUrl(href);
                        bookDetailBean.setTitle(title);

                        Element infoElement = contentElement.select("div#info").first();
                        String info = infoElement.toString();

                        Element a = infoElement.select("a").first();
                        bookDetailBean.setAuthor(a.text().trim());

                        Matcher publisher = RegexHelper.matcher(info, "出版社:</span>", "<");
                        if (publisher.find()) {
                            bookDetailBean.setPublisher(publisher.group().trim());
                        }

                        Matcher publishDate = RegexHelper.matcher(info, "出版年:</span>", "<");
                        if (publishDate.find()) {
                            bookDetailBean.setPublishDate(publishDate.group().trim());
                        }

                        Element ratingNumber = contentElement.select("strong.rating_num").first();
                        bookDetailBean.setRatingNumber(ratingNumber.text().trim());

                        Element ratingPeople = contentElement.select("a.rating_people > span").first();
                        bookDetailBean.setRatingPeopleCount(ratingPeople.text().trim());

                        Elements introElements = contentElement.select("div.intro");
                        bookDetailBean.setBookSummary(introElements.first().text());
                        if (introElements.size() >= 2) {
                            bookDetailBean.setAuthorSummary(introElements.get(1).text());
                        }

                        showBookDetail(bookDetailBean);


                        Elements elementsByAttribute = contentElement.getElementsByClass("main review-item");

                        ArrayList<CommentBean> comments = new ArrayList<>(elementsByAttribute.size());


                        for (Element element : elementsByAttribute) {
                            CommentBean comment = new CommentBean();

                            Elements hdElements = element.getElementsByClass("main-hd");
                            if (hdElements.size() > 0) {
                                String hd = hdElements.get(0).toString();

                                Matcher image = RegexHelper.matcher(hd, "src=\"", "\"");
                                if (image.find()) {
                                    comment.setUserImageUrl(image.group());
                                }

                                Matcher name = RegexHelper.matcher(hd, "class=\"name\">", "<");
                                if (name.find()) {
                                    comment.setUserName(name.group());
                                }

                                Matcher time = RegexHelper.matcher(hd, "main-meta\">", "<");
                                if (time.find()) {
                                    comment.setCommentTime(time.group());
                                }

                            }


                            Elements bdElements = element.getElementsByClass("main-bd");
                            if (bdElements.size() > 0) {
                                Element bdElement = bdElements.get(0);
                                String bd = bdElement.toString();

                                Matcher commentTitle = RegexHelper.matcher(bd, "/\">", "<");
                                if (commentTitle.find()) {
                                    comment.setCommentTitle(commentTitle.group());
                                }

                                Matcher rid = RegexHelper.matcher(bd, "data-rid=\"", "\"");
                                if (rid.find()) {
                                    comment.setRid(rid.group());
                                }

                                Matcher up = RegexHelper.matcher(bd, StringUtils.format("r-useful_count-%s\">", comment.getRid()), "<");
                                if (up.find()) {
                                    comment.setActionUp(up.group());
                                }

                                Matcher down = RegexHelper.matcher(bd, StringUtils.format("r-useless_count-%s\">", comment.getRid()), "<");
                                if (down.find()) {
                                    comment.setActionDown(down.group());
                                }

                                Matcher reply = RegexHelper.matcher(bd, "class=\"reply \">", "<");
                                if (reply.find()) {
                                    comment.setReply(reply.group());
                                }

                                Matcher content = RegexHelper.matcher(bd, "class=\"short-content\">", "&");
                                if (content.find()) {
                                    comment.setCommentShortContent(content.group());
                                }
                            }
                            comments.add(comment);
                        }
                        Logger.json(JsonUtil.toJson(comments));
                        initComment(comments);
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

    public void showBookDetail(BookDetailBean response) {
        ImageManager.getBitmap(viewBinding.ivBookDetail, response.getImageUrl(), viewBinding.ivBookDetailBg);
        viewBinding.collapsingToolbar.setTitle(response.getTitle());
        viewBinding.tvBookDetailTitle.setText(response.getTitle());
        viewBinding.tvBookDetailRating.setText(response.getRatingNumber());
        viewBinding.tvBookDetailRatingCount.setText(getString(R.string.book_number_raters, response.getRatingPeopleCount()));
        viewBinding.tvBookDetailOtherInfo.setText(getString(R.string.book_author_info, response.getAuthor(), response.getPublisher(), response.getPublishDate()));
        viewBinding.tvBookDetailSummary.setText(Html.fromHtml(response.getBookSummary()));
        if (response.getAuthorSummary() != null) {
            viewBinding.tvBookDetailAuthorSummary.setText(Html.fromHtml(response.getAuthorSummary()));
        }
    }

    private void initComment(final List<CommentBean> commentList) {
        CommentAdapter mAdapter = new CommentAdapter(commentList);
        viewBinding.recyclerViewComment.setVisibility(View.VISIBLE);
        viewBinding.recyclerViewComment.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.recyclerViewComment.setAdapter(mAdapter);
        //解决RecyclerView在ScrollView中滑动卡顿问题
        viewBinding.recyclerViewComment.setNestedScrollingEnabled(false);
    }

}
