package com.savor.resturant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.DateUtil;
import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.bean.VodBean;
import com.savor.resturant.utils.ImageLoaderManager;


import java.util.ArrayList;
import java.util.List;

/**
 * 带有侧滑条目的页面适配器
 * Created by wmm on 2016/11/1.
 */
public class CollectListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<VodBean> mVodContentsList = new ArrayList<VodBean>();
    private OnItemStoreClickListener callback;
    private CollectListAdapter.OnItemLongClickListener mOnItemLongClickListener;
    public static final float SCAL = 1.829f;

    public CollectListAdapter(Context context,OnItemStoreClickListener callback) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    public void setData(List<VodBean> list, boolean refresh) {
        if (refresh) {
            mVodContentsList.clear();
        }
        mVodContentsList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mVodContentsList.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mVodContentsList.size();
    }

    @Override
    public VodBean getItem(int position) {
        return mVodContentsList.get(position);
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
            convertView = mInflater.inflate(R.layout.item_video, null);

            holder.cover = (ImageView) convertView.findViewById(R.id.iv_cover);
            holder.name = (TextView) convertView.findViewById(R.id.video_name);
            holder.length = (TextView) convertView.findViewById(R.id.video_length);
            holder.canPlay = (ImageView) convertView.findViewById(R.id.can_play);
            holder.video_type = (TextView) convertView.findViewById(R.id.video_type);
            ViewGroup.LayoutParams layoutParams = holder.cover.getLayoutParams();
            float widthInPx = DensityUtil.getWidthInPx(mContext);
            float height = widthInPx*SCAL;
            layoutParams.height = (int) height;
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        final VodBean itemVo = getItem(position);
        if (itemVo.getCanPlay() == 1) {
            holder.canPlay.setVisibility(View.GONE);
        }else {
            holder.canPlay.setVisibility(View.GONE);
        }
        String imageURL = itemVo.getImageURL();
        String title = itemVo.getTitle();
        int duration = itemVo.getDuration();
        ImageLoaderManager.getImageLoader().loadImage(mContext,imageURL,holder.cover,R.drawable.ic_loadding2,R.drawable.ic_loadding2);
        holder.name.setText(title);
        int type = itemVo.getType();
        if (type == 1 ){
            holder.length.setVisibility(View.INVISIBLE);
        }else {
            holder.length.setVisibility(View.VISIBLE);
            holder.length.setText(" "+DateUtil.formatSecondsTime(Integer.toString(duration)));
        }
        holder.video_type.setText("#"+itemVo.getCategory());
  //      holder.store.setText("取消收藏");
//        if (VodStroeAction.contains(mContext, itemVo)) {
//            holder.store.setText("取消收藏");
//        } else {
//            holder.store.setText("收藏");
//        }
 //       holder.store.setOnClickListener(new mStoreListener( mVodContentsList.get(position)));
        convertView.setOnClickListener(new mStoreListener( mVodContentsList.get(position)));
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemLongClickListener!=null) {
                    mOnItemLongClickListener.onItemLongClick(itemVo);
                    return true;
                }
                return false;
            }
        });
        return convertView;
    }


    /**
     * 单击收藏事件监听器
     */
    public class mStoreListener implements OnClickListener{
        private VodBean itemVo;

        public mStoreListener(VodBean vodAndTopicItemVo) {
            this.itemVo = vodAndTopicItemVo;
        }

        @Override
        public void onClick(View view) {
            callback.onStoreClick(itemVo);
        }
    }


    public interface OnItemStoreClickListener {
        void onStoreClick(VodBean itemVo);
    }



    /**
     * 单击分享事件监听器
     */
    private OnItemShareClickListener mShareListener = null;

    public interface OnItemShareClickListener {
        void onShareClick(VodBean itemVo);
    }

    public void setOnItemShareClickListener(OnItemShareClickListener listener) {
        mShareListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(VodBean itemVo);
    }

    public void setOnItemLongClickListener(CollectListAdapter.OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }


    private class ViewHolder {

        public TextView length;
        public TextView name;
        public TextView video_type;
        public ImageView cover;
        public ImageView canPlay;
    }
}

