package com.savor.resturant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.savor.resturant.bean.MediaInfo;

import java.util.List;

/**
 * 图片，视频预览界面
 * Created by hezd on 2017/11/27.
 */

public class MediaPreviewAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mPagerList;


    public MediaPreviewAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<Fragment> list) {
        this.mPagerList = list;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mPagerList.get(position);
    }

    @Override
    public int getCount() {
        return mPagerList==null?0:mPagerList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        int i = mPagerList.indexOf(object);
        if(i == -1)
            return PagerAdapter.POSITION_NONE;
        return i;
    }
}
