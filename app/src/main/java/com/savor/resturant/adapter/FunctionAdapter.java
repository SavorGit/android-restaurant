package com.savor.resturant.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.activity.SlideListActivity;
import com.savor.resturant.bean.FunctionItem;
import com.savor.resturant.utils.SlideManager;

import java.util.List;

/**
 * 首页功能列表
 * Created by hezd on 2017/11/30.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    private Context mContext;
    private List<FunctionItem> mData;

    public FunctionAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<FunctionItem> data) {
        this.mData = data;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_function_layout, parent, false);
        return new FunctionViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(FunctionViewHolder holder, int position) {
        final FunctionItem functionItem = mData.get(position);
        String content = functionItem.getContent();
        int resId = functionItem.getResId();
        final FunctionItem.FunctionType type = functionItem.getType();

        holder.imageView.setImageResource(resId);
        holder.textView.setText(content);

        holder.ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (type) {
                    case TYPE_RECOMMAND_GEENS:

                        break;
                    case TYPE_WELCOME_WORD:

                        break;
                    case TYPE_ADVERT:

                        break;
                    case TYPE_VIDEO:
                        intent = new Intent(mContext,SlideListActivity.class);
                        intent.putExtra("type", SlideManager.SlideType.VIDEO);
                        mContext.startActivity(intent);
                        break;
                    case TYPE_PIC:
                        intent = new Intent(mContext,SlideListActivity.class);
                        intent.putExtra("type", SlideManager.SlideType.IMAGE);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null?0:mData.size();
    }

    public class FunctionViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout ll_parent;
        public FunctionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_name);
            ll_parent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        }

    }

}
