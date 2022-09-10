package ir.smartdevelopers.photoslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.AbsSavedState;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class SmartPhotoSliderView extends RelativeLayout {
    private LinearLayout dotContainer;
    private HorizontalScrollView mHorizontalScrollView;
    private SmartIndicator mSmartIndicator;
    private BaseSmartPhotoSliderAdapter mAdapter;
    private SmartPhotoSliderViewPager viewPager;
    private int lastPos=0;
    private int shiftPos=0;
    private int mCurrentPosition=0;
    private float scaleSmallDot=0.65f;
    private float scaleActive=1.15f;
    private float scaleNormal=1;
    private long animationDuration=50;
    private boolean zoomEnable =true;
    private int mProgressColor;
    private int mActiveDotColor,mInactivateDotColor;
    private Handler mChangeSlideHandler;
    private long mSliderInterval;
    private Runnable mSlidingRunnable;
    private boolean mSlidingStarted=false;
    private boolean mLastStartedState=false;
    private boolean stateSaved=false;
    public SmartPhotoSliderView(Context context) {
        super(context);
        init(context,null);
    }

    public SmartPhotoSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context,attrs);
    }

    public SmartPhotoSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartPhotoSliderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attributeSet){
        inflate(context,R.layout.smart_view_pager_layout,this);
        viewPager = findViewById(R.id.photo_slider_pager);
        mActiveDotColor=ContextCompat.getColor(context,R.color.activeDot);
        mInactivateDotColor=ContextCompat.getColor(context,R.color.deactivateDot);
        if (attributeSet!=null){
            TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.SmartPhotoSliderView);
            mProgressColor=typedArray.getColor(R.styleable.SmartPhotoSliderView_progressColor,
                    ContextCompat.getColor(context,R.color.SIS_colorLoading));
            mActiveDotColor =typedArray.getColor(R.styleable.SmartPhotoSliderView_activeDotColor,mActiveDotColor);
            mInactivateDotColor =typedArray.getColor(R.styleable.SmartPhotoSliderView_inactiveDotColor,mInactivateDotColor);
            typedArray.recycle();
        }
        mSliderInterval=0;
    }

    public void setIndicator(SmartIndicator smartIndicator) {
        mSmartIndicator = smartIndicator;
    }

    public void setAdapter(BaseSmartPhotoSliderAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setParentPager(viewPager);
        mAdapter.setZoomEnable(zoomEnable);
        mAdapter.progressColor=mProgressColor;
        lastPos=0;
        shiftPos=0;
        viewPager.setAdapter(mAdapter);

        mAdapter.setOnZoomListener(new SmartPhotoSliderPhotoAdapter.OnZoomListener() {
            @Override
            public void onZoom(boolean isZoom) {

                viewPager.setEnable(!isZoom);

            }
        });
        if (mSmartIndicator !=null){
            final int imageCount=mAdapter.getCount();
            dotContainer= mSmartIndicator.getDotContainer();
            mHorizontalScrollView= mSmartIndicator.getHorizontalScrollView();
            prepareDots(imageCount);

            int current = viewPager.getCurrentItem();

            ((ImageView) dotContainer.getChildAt(current)).setImageDrawable(generateActiveDot());
            viewPager.clearOnPageChangeListeners();
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    changeDotsPos(position,imageCount);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setOnUserChangeListener(new SmartPhotoSliderViewPager.OnUserChangeListener() {
                @Override
                public void onUserChanged() {
                    stopSliding();
                }
            });
        }

        viewPager.setCurrentItem(mCurrentPosition);


    }
    private void prepareDots(int count){
        dotContainer.removeAllViewsInLayout();
        for (int i = 0; i < count; i++) {
            ImageView imageView = LayoutInflater.from(getContext()).
                    inflate(R.layout.smart_dot_layout, dotContainer,false)
                    .findViewById(R.id.photo_slider_imgDot);
            if (i>=4){
                imageView.animate().scaleY(scaleSmallDot);
                imageView.animate().scaleX(scaleSmallDot);

            }

            imageView.setImageDrawable(generateInactivateDot());
            dotContainer.addView(imageView);
        }

    }

    private Drawable generateActiveDot() {
        GradientDrawable activeDot=new GradientDrawable();
        activeDot.setColor(mActiveDotColor);
        activeDot.setShape(GradientDrawable.OVAL);
        return activeDot;
    }
    private Drawable generateInactivateDot() {
        GradientDrawable inactiveDot=new GradientDrawable();
        inactiveDot.setColor(mInactivateDotColor);
        inactiveDot.setShape(GradientDrawable.OVAL);
        return inactiveDot;
    }

    private void changeDotsPos(int currentPos,int totalCount){
        int direction = 1;
        if (currentPos - lastPos < 0) {
            direction = -1;
        }
        ImageView firstDot;
        ImageView lastDot;

        if (totalCount > 5) {
            /* if currentPos is between firstDot and lastDot do not scroll
             * else scroll to opposite of direction
             */
            if (currentPos> shiftPos && currentPos<shiftPos+4){
                /* do not scroll */
            }else {
                /* scroll */
                int scrollX=getResources().getDimensionPixelSize(R.dimen.translateX)*direction;
                mHorizontalScrollView.smoothScrollBy(scrollX,0);
                /* if shift left increase shiftPos else decrease it*/
                if (currentPos!=totalCount-1 && currentPos!=0)
                    shiftPos = shiftPos + direction;
            }
            Log.v("TTT","shitPos="+shiftPos+" direction="+direction+" current="+currentPos+" last pos="+lastPos);

            /* by first shift calculate firstDot and lastDot
             * to scale them down
             */
            if (shiftPos > 0 && shiftPos < totalCount - 1-4) {
                firstDot=(ImageView) dotContainer.getChildAt(shiftPos);
                lastDot=(ImageView) dotContainer.getChildAt(shiftPos+4);
                scaleDownDot(lastDot);
                scaleDownDot(firstDot);
                if (shiftPos==1){
                    /* scale down 0 dot after scroll*/
                    ImageView dot0=(ImageView) dotContainer.getChildAt(0);
                    scaleDownDot(dot0);
                }
                /* scale down  dot end after scroll*/
                if (shiftPos==totalCount-2-4){
                    ImageView dotEnd=(ImageView) dotContainer.getChildAt(totalCount-1);
                    scaleDownDot(dotEnd);
                }

            }
            if (shiftPos==0){
                /* do not scale down first dot
                 * just scale down last
                 * */
                lastDot=(ImageView) dotContainer.getChildAt(shiftPos+4);
                scaleDownDot(lastDot);
            }
            if (shiftPos==totalCount-1-4){
                /* do not scale down last dot
                 * just scale down first
                 * */
                firstDot=(ImageView) dotContainer.getChildAt(shiftPos);
                scaleDownDot(firstDot);
            }


            /* active current and deactivate last pos*/
            if (currentPos==0){
                ImageView currentDot=(ImageView) dotContainer.getChildAt(0);
                activeDot(currentDot);

            }else if ( currentPos == totalCount - 1){
                ImageView currentDot=(ImageView) dotContainer.getChildAt(totalCount-1);
                activeDot(currentDot);

            }else {
                ImageView currentDot=(ImageView) dotContainer.getChildAt(currentPos);
                activeDot(currentDot);
            }
            ImageView beforeDot=(ImageView) dotContainer.getChildAt(lastPos);
            if (beforeDot!=null){
                deactivateDot(beforeDot);
            }



        } else {
            /* just active current dot*/
            ImageView currentDot=(ImageView) dotContainer.getChildAt(currentPos);
            activeDot(currentDot);
            ImageView beforeDot=(ImageView) dotContainer.getChildAt(lastPos);
            if (beforeDot!=null){
                deactivateDot(beforeDot);
            }
        }

        lastPos=currentPos;

    }
    private void activeDot(ImageView imageView){
        imageView.setImageDrawable(generateActiveDot());
        imageView.animate().setDuration(animationDuration).scaleX(scaleActive);
        imageView.animate().setDuration(animationDuration).scaleY(scaleActive);
    }
    private void deactivateDot(ImageView imageView){
        imageView.setImageDrawable(generateInactivateDot());
        imageView.animate().setDuration(animationDuration).scaleX(scaleNormal);
        imageView.animate().setDuration(animationDuration).scaleY(scaleNormal);
    }

    public void setActiveDotColor(int activeDotColor) {
        mActiveDotColor = activeDotColor;
    }

    public void setInactivateDotColor(int inactivateDotColor) {
        mInactivateDotColor = inactivateDotColor;
    }

    private void scaleDownDot(ImageView imageView){
        imageView.animate().setDuration(animationDuration).scaleX(scaleSmallDot);
        imageView.animate().setDuration(animationDuration).scaleY(scaleSmallDot);
    }

    public boolean isZoomEnable() {
        return zoomEnable;
    }

    public void setZoomEnable(boolean zoomEnable) {
        this.zoomEnable = zoomEnable;
        if (mAdapter!=null){
            mAdapter.setZoomEnable(zoomEnable);
        }
    }
   public int getCurrentPosition(){
        if (mAdapter==null){
            throw new RuntimeException("The SmartPhotoSlider's adapter is null");
        }
        return viewPager.getCurrentItem();

   }
    public void setCurrentPosition(int position){
        mCurrentPosition=position;
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onDetachedFromWindow() {
        stopSliding();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        if (mSliderInterval > 0 && !stateSaved){
            startSliding();
        }
        super.onAttachedToWindow();
    }


    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible){
            if (mLastStartedState){
                startSliding();
            }
        }else {
            mLastStartedState = mSlidingStarted;
            stopSliding();
        }


    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.isStarted=mSlidingStarted;

        if (getId() ==0){
            setId(View.generateViewId());
        }
        int childCount=getChildCount();
        for (int i=0;i<childCount;i++){
            if (getChildAt(i).getId()==0){
                getChildAt(i).setId(View.generateViewId());
            }
        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState){
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(state);
            boolean started = ss.isStarted;
            viewPager.setCurrentItem(ss.currentPos,false);
            stateSaved=true;
            if (started){
                startSliding();
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public static class SavedState extends BaseSavedState {
        boolean isStarted;
        int currentPos;
        protected SavedState(Parcel source) {
            super(source);
            isStarted = source.readInt() == 1;
            currentPos = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isStarted ? 1 :0);
            dest.writeInt(currentPos);
        }
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


    private void startSliding(){
        if (mSliderInterval<=0){
            return;
        }
        if (mSlidingStarted){
            return;
        }
        if (getVisibility() != VISIBLE){
            stopSliding();
            return;
        }
        if (mChangeSlideHandler==null){
            mChangeSlideHandler=new Handler();
        }
        if (mSlidingRunnable == null) {
            mSlidingRunnable=new Runnable() {
                @Override
                public void run() {
                    if (!mSlidingStarted){
                        return;
                    }
                    int count = mAdapter.getCount();
                    int current=viewPager.getCurrentItem();
                    int pos ;
                    if (current < count-1){
                        pos = current+1;
                    }else {
                        pos = 0;
                    }
                    viewPager.setCurrentItem(pos);
                    mChangeSlideHandler.postDelayed(this,mSliderInterval);
                }
            };
        }
        mChangeSlideHandler.postDelayed(mSlidingRunnable,mSliderInterval);
        mSlidingStarted=true;
    }
    private  void  stopSliding(){
        if (mSlidingRunnable != null && mChangeSlideHandler != null) {
            mChangeSlideHandler.removeCallbacks(mSlidingRunnable);

        }
        mSlidingStarted=false;
    }
    private void reScheduleSliding(){
        if (mSlidingRunnable != null && mChangeSlideHandler != null) {
            mChangeSlideHandler.removeCallbacks(mSlidingRunnable);
        }
        startSliding();
    }
    public long getSliderInterval() {
        return mSliderInterval;
    }

    public void setSliderInterval(long sliderInterval) {
        mSliderInterval = sliderInterval;
    }
}
