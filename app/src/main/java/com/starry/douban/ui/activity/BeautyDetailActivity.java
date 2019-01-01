package com.starry.douban.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.BeautyModel;
import com.starry.douban.util.ToastUtil;
import com.starry.parallaxviewpager.Mode;
import com.starry.parallaxviewpager.ParallaxViewPager;
import com.starry.rx.RxManager;
import com.starry.rx.RxTask;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * @author Starry Jerry
 * @since 2019/1/1.
 */

public class BeautyDetailActivity extends BaseActivity {

    private static final String EXTRA_BEAUTY_LIST = "extra_beauty_list";
    private static final String EXTRA_POSITION = "extra_position";

    @BindView(R.id.vp_beauty_detail)
    ParallaxViewPager viewPager;

    private ArrayList<BeautyModel> beautyList;

    private int selectPosition;

    public static void showActivity(Context context, ArrayList<BeautyModel> list, int position) {
        Intent intent = new Intent(context, BeautyDetailActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_BEAUTY_LIST, list);
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected Drawable getToolbarBackground() {
        return new ColorDrawable(0x00000000); //透明背景
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_beauty_detail;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        beautyList = intent.getParcelableArrayListExtra(EXTRA_BEAUTY_LIST);
        selectPosition = intent.getIntExtra(EXTRA_POSITION, 0);

        initViewPager();
        viewPager.setCurrentItem(selectPosition, false);
    }

    private void initViewPager() {
        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object obj) {
                container.removeView((View) obj);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = View.inflate(container.getContext(), R.layout.item_beauty_detail, null);
                ImageView imageView = view.findViewById(R.id.iv_item_beauty_detail);
                ImageManager.loadImage(imageView, beautyList.get(position).getUrl());
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return beautyList.size();
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setMode(Mode.LEFT_OVERLAY);
//        viewPager.setMode(Mode.RIGHT_OVERLAY);
//        viewPager.setMode(Mode.NONE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file_download:
                ToastUtil.showToast("图片下载中...");
                RxManager.createIO(new RxTask<Boolean>() {
                    @Override
                    public Boolean run() {
                        return ImageManager.downloadImage(beautyList.get(selectPosition).getUrl());
                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean boo) throws Exception {
                        String result = boo ? "成功" : "失败";
                        ToastUtil.showToast("保存到系统相册" + result);
                    }
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
