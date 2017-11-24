package com.savor.resturant.adapter;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.utils.ImageLoaderManager;
import com.savor.resturant.widget.CustomCoverView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 幻灯片列表页适配器
 * Created by luminita on 2016/10/27.
 */
public class SlideListAdapter extends BaseAdapter {

    private static final String FILE_DIR = SavorApplication.getInstance().getFilesDir().toString();;
    private static final String FILE_NAME = "lantern_slide.json";
    protected Context mContext;
    protected LayoutInflater mInflater;
    private Point mPoint = new Point(0, 0);
    private List<SlideSetInfo> mList = new ArrayList<>();
    private OnItemSlideClickListener itemSlideClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public SlideListAdapter(Context context, OnItemSlideClickListener itemSlideClickListener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.itemSlideClickListener = itemSlideClickListener;
    }

    public void setData(List<SlideSetInfo> list, boolean refresh) {
        if (refresh)
            mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(SlideSetInfo slideSetInfo) {
        if(mList!=null) {
            mList.remove(slideSetInfo);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_slide_list, null);
            holder.slideItem = convertView.findViewById(R.id.ll_left);
           // holder.slideDel = convertView.findViewById(R.id.ll_right);
            holder.ivTopCover = (CustomCoverView) convertView.findViewById(R.id.cover_img);
            holder.tvNameCount = (TextView) convertView.findViewById(R.id.tv_name_count);
            holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
            holder.parentLayout = (RelativeLayout) convertView.findViewById(R.id.ll_parent);
            holder.ivTopCover.setOnMeasureListener(new CustomCoverView.OnMeasureListener() {
                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }

        final SlideSetInfo slideSetInfo = mList.get(position);
        String topImagePath = null;
        int imageCounts = 0;
        if (slideSetInfo.imageList != null && slideSetInfo.imageList.size() > 0) {
            topImagePath = slideSetInfo.imageList.get(0).getAssetpath();
            imageCounts = slideSetInfo.imageList.size();
        }
        File file = new File(topImagePath);
        if(file!=null&&file.exists()) {
            ImageLoaderManager.getImageLoader().loadImage(mContext, topImagePath, holder.ivTopCover);
        }else {
            holder.ivTopCover.setImageResource(R.drawable.ic_deleted_hint);
        }

        holder.tvNameCount.setText(slideSetInfo.groupName+"("+slideSetInfo.imageList.size()+")");

        String datetime = new SimpleDateFormat("yyyy/MM/dd").format(slideSetInfo.updateTime).toString();
        holder.tvUpdateTime.setText(mContext.getString(R.string.update_time, datetime));
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemLongClickListener!=null) {
                    mOnItemLongClickListener.onItemLongClick(slideSetInfo);
                    return true;
                }
                return false;
            }
        });
        convertView.setOnClickListener(new SlideListAdapter.mStoreListener(slideSetInfo));

        return convertView;
    }

    public static class ViewHolder {
        public View slideItem;
        //public View slideDel;
        public CustomCoverView ivTopCover;
        public TextView tvNameCount;
        public TextView tvUpdateTime;
        public RelativeLayout parentLayout;
    }


    public interface OnItemLongClickListener {
        void onItemLongClick(SlideSetInfo info);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnItemSlideClickListener {
        void onItemClick(SlideSetInfo info);
    }
    /**
     * 单击收藏事件监听器
     */
    public class mStoreListener implements View.OnClickListener {
        private SlideSetInfo slideSetInfo;

        public mStoreListener(SlideSetInfo slideSetInfo) {
            this.slideSetInfo = slideSetInfo;
        }

        @Override
        public void onClick(View view) {
            itemSlideClickListener.onItemClick(slideSetInfo);
        }
    }


}
