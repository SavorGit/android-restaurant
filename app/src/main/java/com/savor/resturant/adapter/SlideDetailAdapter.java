package com.savor.resturant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.savor.resturant.R;
import com.savor.resturant.bean.PictureInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminita on 2016/12/15.
 */

public class SlideDetailAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<PictureInfo> mList = new ArrayList<PictureInfo>();
    private boolean mIsEdit;

    public SlideDetailAdapter (Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<PictureInfo> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null && mList.size() > 0)
            return mList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_slide_detail, null);
            viewHolder.picture = (ImageView) view.findViewById(R.id.iv_picture);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.cb_check);
            //绘制图片显示的大小
           view.setTag(R.id.tag_holder, viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag(R.id.tag_holder);
        }
        //当最后一张图片为添加按钮时，不显示勾选框
        if (mIsEdit && i != mList.size()) {
            viewHolder.checkBox.setChecked(mList.get(i).isChecked());
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }

        String assetpath = mList.get(i).getAssetpath();
        File file = new File(assetpath);
        if(file!=null&&file.exists()) {
            Glide.with(mContext).load(assetpath).
                    placeholder(R.drawable.empty_slide).dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(viewHolder.picture);
        }else {
            viewHolder.picture.setImageResource(R.drawable.ic_deleted_hint);
        }

        return view;
    }

    private class ViewHolder {
        public ImageView picture;
        public CheckBox checkBox;
    }

    /**
     * 设置是否可编辑
     * @param isEdit true 编辑状态打开
     */
    public void setEditState (boolean isEdit) {
        mIsEdit = isEdit;
    }
}
