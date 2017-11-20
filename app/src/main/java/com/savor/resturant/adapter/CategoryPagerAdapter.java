package com.savor.resturant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * Created by hezd on 2017/1/13.
 */

public class CategoryPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mPagerList;
    private List<String> mTitleList;

    public CategoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<Fragment> list,List<String> titleList) {
        this.mPagerList = list;
        this.mTitleList = titleList;
        notifyDataSetChanged();
    }

    public void addPager(Fragment fragment,String title) {
        mPagerList.add(fragment);
        mTitleList.add(title);
        notifyDataSetChanged();
    }

    public void addPager(Fragment fragment,String title,int index) {
        mPagerList.add(index,fragment);
        mTitleList.add(index,title);
        notifyDataSetChanged();
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    @Override
    public int getCount() {
        return mPagerList==null?0:mPagerList.size();
    }

    @Override
    public Fragment getItem(int position) {
//        Fragment fragment = mPagerList.get(0);
//
//        Fragment fragment = mPagerList.get(position);
//        if(fragment instanceof HotspotFragment) {
//            return HotspotFragment.newInstance();
//        }else if(fragment instanceof RedianerFragment) {
//            return new RedianerFragment();
//        }
//        CategoryFragment categoryFragment = (CategoryFragment) fragment;
//        Bundle arguments = categoryFragment.getArguments();
//        int id = arguments.getInt("id");
//        return CategoryFragment.getInstance(id);
        return mPagerList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
