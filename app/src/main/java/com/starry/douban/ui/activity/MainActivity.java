package com.starry.douban.ui.activity;

import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.starry.douban.util.viewpager.v4.FragmentPagerItem;
import com.starry.douban.util.viewpager.v4.FragmentPagerItemAdapter;
import com.starry.douban.util.viewpager.v4.FragmentPagerItems;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.ui.fragment.BlankFragment;
import com.starry.douban.ui.fragment.HomeFragment;
import com.starry.douban.ui.fragment.MovieFragment;
import com.starry.douban.ui.fragment.MovieParentFragment;

import butterknife.BindView;


/**
 * Created by Starry Jerry on 2016/12/1.
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.viewpager_main)
    ViewPager viewpagerMain;
    @BindView(R.id.rbtn_main_home)
    RadioButton rbtnMainPhoto;
    @BindView(R.id.rbtn_main_book)
    RadioButton rbtnMainBook;
    @BindView(R.id.rbtn_main_setting)
    RadioButton rbtnMainSetting;
    @BindView(R.id.radioGroup_main)
    RadioGroup radioGroupMain;

    private String[] titles = new String[]{"首页", "电影", "设置"};

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(titles[0]);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(HomeFragment.class.getSimpleName(), HomeFragment.class));
        pages.add(FragmentPagerItem.of(MovieFragment.class.getSimpleName(), MovieParentFragment.class));
        pages.add(FragmentPagerItem.of(BlankFragment.class.getSimpleName(), BlankFragment.class));

        viewpagerMain.setOffscreenPageLimit(3);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewpagerMain.setAdapter(adapter);
    }

    @Override
    public void setListener() {
        radioGroupMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_main_home:
                        setTitle(titles[0]);
                        viewpagerMain.setCurrentItem(0, false);
                        break;
                    case R.id.rbtn_main_book:
                        setTitle(titles[1]);
                        viewpagerMain.setCurrentItem(1, false);
                        break;
                    case R.id.rbtn_main_setting:
                        setTitle(titles[2]);
                        viewpagerMain.setCurrentItem(2, false);
                        break;

                }
            }
        });

        viewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(titles[position]);
                ((RadioButton) radioGroupMain.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void loadData() {

    }
}
