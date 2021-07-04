package com.starry.douban.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.databinding.ActivityPhotoDetailBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.PhotoModel;
import com.starry.douban.util.StringUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.parallaxviewpager.Mode;
import com.starry.rx.RxManager;
import com.starry.rx.RxTask;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Starry Jerry
 * @since 2019/1/1.
 */

public class PhotoDetailActivity extends BaseActivity<ActivityPhotoDetailBinding> {

    private static final String EXTRA_PHOTO_LIST = "extra_photo_list";
    private static final String EXTRA_POSITION = "extra_position";

    private ArrayList<PhotoModel> photoList;

    private int selectPosition;

    public static void showActivity(Context context, ArrayList<? extends PhotoModel> list, int position) {
        Intent intent = new Intent(context, PhotoDetailActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_PHOTO_LIST, list);
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected Drawable getToolbarBackground() {
        return new ColorDrawable(0x00000000); //透明背景
    }

    @Override
    public ActivityPhotoDetailBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityPhotoDetailBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        photoList = intent.getParcelableArrayListExtra(EXTRA_PHOTO_LIST);
        selectPosition = intent.getIntExtra(EXTRA_POSITION, 0);

        setPageAndTitle();
        initViewPager();
        viewBinding.vpPhotoDetail.setCurrentItem(selectPosition, false);
    }

    private void initViewPager() {
        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, @NonNull Object obj) {
                container.removeView((View) obj);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = View.inflate(container.getContext(), R.layout.item_beauty_detail, null);
                ImageView imageView = view.findViewById(R.id.iv_item_beauty_detail);
                ImageManager.loadImage(imageView, photoList.get(position).getPhotoUrl());
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return photoList.size();
            }
        };
        viewBinding.vpPhotoDetail.setAdapter(adapter);
        viewBinding.vpPhotoDetail.setMode(Mode.LEFT_OVERLAY);
//        viewPager.setMode(Mode.RIGHT_OVERLAY);
//        viewPager.setMode(Mode.NONE);

        viewBinding.vpPhotoDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
                setPageAndTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPageAndTitle() {
        viewBinding.tvPhotoDetailPage.setText(StringUtils.format("%d/%d", selectPosition + 1, photoList.size()));
        setTitle(photoList.get(selectPosition).getPhotoTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    private Disposable subscribe;

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file_download:
                ToastUtil.showToast("图片下载中...");
                subscribe = RxManager.createIO(new RxTask<Boolean>() {
                    @Override
                    public Boolean run() {
                        return ImageManager.downloadImage(photoList.get(selectPosition).getPhotoUrl());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxManager.dispose(subscribe);
    }
}
