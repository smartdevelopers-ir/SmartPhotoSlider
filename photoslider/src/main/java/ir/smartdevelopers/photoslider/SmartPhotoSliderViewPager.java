package ir.smartdevelopers.photoslider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

 class SmartPhotoSliderViewPager extends ViewPager {
    /* for disabling sliding for example when image is zoomed*/
     private boolean enable=true;

    public SmartPhotoSliderViewPager(@NonNull Context context) {
        super(context);
    }

    public SmartPhotoSliderViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (enable) {

                return super.onInterceptTouchEvent(ev);
            }else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enable) {
            return super.onTouchEvent(ev);
        }else {
            return false;
        }
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
