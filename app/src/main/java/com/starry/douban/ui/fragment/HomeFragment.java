package com.starry.douban.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.starry.douban.adapter.BookTagAdapter;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.FragmentHomeBinding;
import com.starry.douban.model.BookTag;
import com.starry.douban.ui.activity.BookListActivity;
import com.starry.douban.util.RegexHelper;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.util.UiUtils;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Starry Jerry
 * @since 2016/12/4.
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    private BookTagAdapter mAdapter;

    @Override
    public FragmentHomeBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentHomeBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("首页");
        //Activity加载多Fragment时，toolbar不设置fitsSystemWindows，
        // 否则会导致自定义标题往距上边距有个状态栏高度，导致标题未垂直居中
        toolbar.setFitsSystemWindows(false);
        viewBinding.viewStatusBarFix.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.getStatusBarHeight(mActivity)));

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new BookTagAdapter();
        mAdapter.addOnScrollListener(viewBinding.XRecyclerViewHome);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                BookListActivity.showActivity(mActivity, mAdapter.getItem(position).getTag());
            }
        });

        viewBinding.XRecyclerViewHome.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        viewBinding.XRecyclerViewHome.setAdapter(mAdapter);
        loadData();
    }

    @Override
    public void loadData() {
        HttpManager.get(Apis.BOOK_TAG)
                .tag(this)
                .headers("referer", Apis.HOST_DOUBAN)
                .build()
                .enqueue(new StringCallback<String>() {

                    @Override
                    public void onSuccess(String response, Object... obj) {
                        //  <div class="">
                        //        <div class="">
                        //          <a name="文学" class="tag-title-wrapper">
                        //            <h2 style="padding-top:10px">文学 · · · · · · </h2>
                        //          </a>
                        //          <table class="tagCol">
                        //            <tbody>
                        //                  <tr>
                        //                <td><a href="/tag/小说">小说</a><b>(7086696)</b></td>
                        //                <td><a href="/tag/外国文学">外国文学</a><b>(2789064)</b></td>
                        //                <td><a href="/tag/文学">文学</a><b>(2756274)</b></td>
                        //                <td><a href="/tag/经典">经典</a><b>(1749171)</b></td>
                        //                  </tr>
                        //                  <tr>
                        //                <td><a href="/tag/中国文学">中国文学</a><b>(1646261)</b></td>
                        //                <td><a href="/tag/随笔">随笔</a><b>(1509633)</b></td>
                        //                <td><a href="/tag/日本文学">日本文学</a><b>(1249437)</b></td>
                        //                <td><a href="/tag/散文">散文</a><b>(902254)</b></td>
                        //                  </tr>
                        //                  <tr>
                        //                <td><a href="/tag/村上春树">村上春树</a><b>(523850)</b></td>
                        //                <td><a href="/tag/诗歌">诗歌</a><b>(468335)</b></td>
                        //                <td><a href="/tag/童话">童话</a><b>(402577)</b></td>
                        //                <td><a href="/tag/名著">名著</a><b>(394865)</b></td>
                        //                  </tr>
                        //                  <tr>
                        //                <td><a href="/tag/儿童文学">儿童文学</a><b>(378564)</b></td>
                        //                <td><a href="/tag/古典文学">古典文学</a><b>(352081)</b></td>
                        //                <td><a href="/tag/余华">余华</a><b>(326036)</b></td>
                        //                <td><a href="/tag/王小波">王小波</a><b>(294140)</b></td>
                        //                  </tr>
                        //                  <tr>
                        //                <td><a href="/tag/当代文学">当代文学</a><b>(268642)</b></td>
                        //                <td><a href="/tag/杂文">杂文</a><b>(268065)</b></td>
                        //                <td><a href="/tag/张爱玲">张爱玲</a><b>(230705)</b></td>
                        //                <td><a href="/tag/外国名著">外国名著</a><b>(167726)</b></td>
                        //                  </tr>
                        //                  <tr>
                        //                <td><a href="/tag/鲁迅">鲁迅</a><b>(150995)</b></td>
                        //                <td><a href="/tag/钱钟书">钱钟书</a><b>(148386)</b></td>
                        //                <td><a href="/tag/诗词">诗词</a><b>(112670)</b></td>
                        //                <td><a href="/tag/茨威格">茨威格</a><b>(84352)</b></td>
                        //                  </tr>
                        //                  <tr>
                        //                <td><a href="/tag/米兰·昆德拉">米兰·昆德拉</a><b>(66088)</b></td>
                        //                <td><a href="/tag/杜拉斯">杜拉斯</a><b>(48258)</b></td>
                        //                <td><a href="/tag/港台">港台</a><b>(10153)</b></td>
                        //            </tbody>
                        //          </table>
                        //        </div>
                        //  </div>

                        Document parse = Jsoup.parse(response);
                        Elements tagCol = parse.getElementsByClass("tagCol");

                        List<BookTag> books = new ArrayList<>();
                        for (Element tagElement : tagCol) {
                            Matcher matcher = RegexHelper.matcher(tagElement.toString(), "\"/tag/", "\"");
                            while (matcher.find()) {
                                BookTag bookTag = new BookTag();
                                bookTag.setTag(matcher.group());
                                books.add(bookTag);
                            }
                        }
                        mAdapter.setAll(books);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        hideLoading(success);
                    }
                });
    }

}
