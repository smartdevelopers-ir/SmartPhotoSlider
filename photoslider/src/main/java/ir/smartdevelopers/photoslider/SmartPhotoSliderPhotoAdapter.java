package ir.smartdevelopers.photoslider;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ortiz.touchview.TouchImageView;

public abstract class SmartPhotoSliderPhotoAdapter extends BaseSmartPhotoSliderAdapter {




    public abstract TouchImageView getImage(TouchImageView imageView,int position);

    public SmartPhotoSliderPhotoAdapter(Context context) {
        super(context);
    }

    @Override
    public  abstract int getCount();

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final TouchImageView touchImageView=getImage(new TouchImageView(getContext()),position);
        touchImageView.setZoomEnabled(isZoomEnable());

            touchImageView.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
                @Override
                public void onMove() {
//                    Log.v("TTT","touched");
                    if (touchImageView.isZoomed()) {
//                        parentPager.setEnable(false);
                        if (getOnZoomListener()!=null){
                            getOnZoomListener().onZoom(true);
                        }
                    } else {
                        if (getOnZoomListener()!=null){
                            getOnZoomListener().onZoom(false);
                        }
//                        parentPager.setEnable(true);
                    }
                }
            });

        final int pos=position;
        touchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.v("TTT","clicked");
                if (getOnItemClickListener()!=null){
                    getOnItemClickListener().onItemClicked(pos,touchImageView);
                }
            }
        });
        touchImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getOnItemLongClickListener()!=null){
                    return getOnItemLongClickListener().onItemLongClicked(pos,touchImageView);
                }
                return false;
            }
        });

        container.addView(touchImageView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        return touchImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);

    }




}
