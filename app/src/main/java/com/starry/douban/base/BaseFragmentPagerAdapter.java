package com.starry.douban.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2018/8/8.
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> pages;
    private List<String> pageTitles;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> pages) {
        super(fm);
        this.pages = pages;
    }

    public void setPageTitles(List<String> pageTitles) {
        this.pageTitles = pageTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (pageTitles != null && position < pageTitles.size()) {
            return pageTitles.get(position);
        }
        return super.getPageTitle(position);
    }
}
