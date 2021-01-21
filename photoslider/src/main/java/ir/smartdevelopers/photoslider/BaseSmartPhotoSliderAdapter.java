package ir.smartdevelopers.photoslider;

import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;

import com.ortiz.touchview.TouchImageView;

public abstract class BaseSmartPhotoSliderAdapter extends PagerAdapter {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnZoomListener mOnZoomListener;
    SmartPhotoSliderViewPager parentPager;
    private Context mContext;
    private boolean zoomEnable=true;
    int progressColor;
    public BaseSmartPhotoSliderAdapter(Context context) {
        mContext = context;

    }

    public OnZoomListener getOnZoomListener() {
        return mOnZoomListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClicked(int position, TouchImageView imageView);
    }
    public interface OnItemLongClickListener{
        boolean onItemLongClicked(int position,TouchImageView imageView);
    }
    public interface OnZoomListener{
        void onZoom(boolean isZoom);
    }
    public void setParentPager(SmartPhotoSliderViewPager parentPager) {
        this.parentPager = parentPager;
    }
    protected Context getContext(){
        return mContext;
    }

    public boolean isZoomEnable() {
        return zoomEnable;
    }

    public void setZoomEnable(boolean zoomEnable) {
        this.zoomEnable = zoomEnable;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.mOnItemClickListener=clickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    void setOnZoomListener(OnZoomListener onZoomListener) {
        mOnZoomListener = onZoomListener;
    }
}
