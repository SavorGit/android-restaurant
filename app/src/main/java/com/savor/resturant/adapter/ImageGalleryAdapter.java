package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.savor.resturant.R;
import com.savor.resturant.bean.ModelPic;
import com.savor.resturant.utils.blur.RotateTransformation;

import java.util.List;

/**
 * Created by etiennelawlor on 8/20/15.
 */
public class ImageGalleryAdapter extends PagerAdapter {

    // region Member Variables
    private final List<ModelPic> images;
    private FullScreenImageLoader fullScreenImageLoader;
    private String mFilePath;
    private int mCompoundPositin = -1;

    public void setCompoundPath(String filePath,int position) {
        this.mFilePath = filePath;
        this.mCompoundPositin = position;
    }

    public int getmCompoundPositin() {
        return mCompoundPositin;
    }

    public String getmFilePath() {
        return mFilePath;
    }



    // endregion

    // region Interfaces
    public interface FullScreenImageLoader {
        void loadFullScreenImage(ImageView iv, String imageUrl, int width, LinearLayout bglinearLayout);
    }
    // endregion

    // region Constructors
    public ImageGalleryAdapter(Context context, List<ModelPic> images) {
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fullscreen_image, null);

        final ImageView imageView = (ImageView) view.findViewById(R.id.iv);

        final ModelPic image = images.get(position);
//        image.setPrimaryText(null);
//        image.setDateText(null);
//        image.setDesText(null);
        Context context = imageView.getContext();
        String path = image.getAssetpath();
        String compoundPath = image.getCompoundPath();
        if(!TextUtils.isEmpty(compoundPath)) {
            path = compoundPath;
        }
        int rotate ;
        if(!TextUtils.isEmpty(compoundPath)) {
            rotate = image.getComRotateValue()%360;
        }
        else {
            rotate = image.getRotatevalue()%360;
        }
        view.setTag(image);
        Glide.with(context).
                load(path).
                centerCrop().
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                transform(new RotateTransformation(context,rotate)).
                into(imageView);
        container.addView(view, 0);

        return view;
    }

    @Override
    public int getCount() {
        return images==null?0:images.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        View view = ((View)object);
//        Object tag = view.getTag();
//        if(tag instanceof ModelPic) {
//            ((ModelPic) tag).setRotate(0);
//            ((ModelPic) tag).setComRotateValue(0);
//            ((ModelPic) tag).setRotatevalue(0);
//        }
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // region Helper Methods
    public void setFullScreenImageLoader(FullScreenImageLoader loader) {
        this.fullScreenImageLoader = loader;
    }
    // endregion

    private int mChildCount = 0;

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
