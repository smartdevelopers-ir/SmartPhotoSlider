package ir.smartdevelopers.photoslider;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.PagerAdapter;

import com.ortiz.touchview.TouchImageView;

public abstract class BaseSmartPhotoSliderAdapter extends PagerAdapter {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnZoomListener mOnZoomListener;
    SmartPhotoSliderViewPager parentPager;

    private boolean zoomEnable=true;
    int progressColor;
    private LifecycleOwner mLifecycleOwner;
    public BaseSmartPhotoSliderAdapter(Context context) {
        if (context instanceof LifecycleOwner){
            mLifecycleOwner= (LifecycleOwner) context;
        }
    }
    public BaseSmartPhotoSliderAdapter(LifecycleOwner owner) {
        mLifecycleOwner= owner;

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

    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
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
