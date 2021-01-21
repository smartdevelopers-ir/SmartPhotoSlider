package ir.smartdevelopers.photoslider;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

public class SmartIndicator extends RelativeLayout {
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout dotContainer;
    public SmartIndicator(Context context) {
        super(context);
        init(context,null);
    }

    public SmartIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public SmartIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        View view= LayoutInflater.from(context).inflate(R.layout.smart_indicator_layout,this);
        mHorizontalScrollView=view.findViewById(R.id.photo_slider_scroller);
        dotContainer=view.findViewById(R.id.photo_slider_dot_container);
    }
    public HorizontalScrollView getHorizontalScrollView() {
        return mHorizontalScrollView;
    }

    public LinearLayout getDotContainer() {
        return dotContainer;
    }
}
