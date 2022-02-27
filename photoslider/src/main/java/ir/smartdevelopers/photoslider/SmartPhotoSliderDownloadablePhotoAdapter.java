package ir.smartdevelopers.photoslider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ortiz.touchview.TouchImageView;

import java.io.File;

import ir.smartdevelopers.smartprogressimageview.SmartProgressImageView;

public abstract class SmartPhotoSliderDownloadablePhotoAdapter extends BaseSmartPhotoSliderAdapter {

    private boolean mAutoDownloadEnabled =false;
    private boolean[] mIsDownloading;
    private int mImageCount;

    public SmartPhotoSliderDownloadablePhotoAdapter(Context context,int imageCount) {
        super(context);
        mImageCount=imageCount;
        mIsDownloading=new boolean[getCount()];
    }

    @Override
    public  int getCount(){
        return mImageCount;
    };

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.item_downloadable_image_layout,container,false);
        final TouchImageView touchImageView=view.findViewById(R.id.item_downloadable_image_imgImage);
        View hover=view.findViewById(R.id.item_downloadable_image_hover);
        final ImageView imgDownload=view.findViewById(R.id.item_downloadable_image_download);
        Group downloadIconGroup=view.findViewById(R.id.item_downloadable_image_group);
        final SmartProgressImageView progressImageView=view.findViewById(R.id.item_downloadable_image_loadingView);
        progressImageView.setProgressNormalColor(progressColor);
        final Uri downloadedImageUri=getDownloadedImage(container.getContext().getApplicationContext(),position);

        if (downloadedImageUri!=null){
            downloadIconGroup.setVisibility(View.GONE);

                    Glide.with(touchImageView).load(downloadedImageUri)
                            .into(touchImageView);


        }else {
            downloadIconGroup.setVisibility(View.VISIBLE);
            if (mAutoDownloadEnabled){
                imgDownload.setImageResource(R.drawable.ic_clear_24_white);
                progressImageView.setVisibility(View.VISIBLE);
                progressImageView.startLoading();
                startDownload(container.getContext().getApplicationContext(),touchImageView, position);
            }else {
                imgDownload.setImageResource(R.drawable.ic_download_white);
                progressImageView.stopAnimation();
                progressImageView.setVisibility(View.INVISIBLE);
            }
        }
        hover.setTag(position);
        hover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=(int) v.getTag();
                if (mIsDownloading[pos]){
                    // cancel download
                    imgDownload.setImageResource(R.drawable.ic_download_white);
                    progressImageView.stopAnimation();
                    progressImageView.setVisibility(View.INVISIBLE);
                    cancelDownload(v.getContext().getApplicationContext(),pos);
                }else {
                    //start download
                    imgDownload.setImageResource(R.drawable.ic_clear_24_white);
                    progressImageView.setVisibility(View.VISIBLE);
                    progressImageView.startLoading();
                    startDownload(v.getContext().getApplicationContext(),touchImageView,pos);
                }
            }
        });
//        final TouchImageView touchImageView=getImage(new TouchImageView(getContext()),position);
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

        touchImageView.setTag(position);
        touchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos= (int) view.getTag();
//                Log.v("TTT","clicked");
                if (getOnItemClickListener()!=null){
                    getOnItemClickListener().onItemClicked(pos,touchImageView);
                }
            }
        });
        touchImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos= (int) view.getTag();
                if (getOnItemLongClickListener()!=null){
                    return getOnItemLongClickListener().onItemLongClicked(pos,touchImageView);
                }
                return false;
            }
        });


        view.setTag(position);
        container.addView(view);
        return view;
    }

    public final void downloadCompleted(int position,Uri downloadedImageUri){
        if (getLifecycleOwner()!=null && getLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED){
            return;
        }
        View view=parentPager.findViewWithTag(position);
        if (view!=null){
             TouchImageView touchImageView=view.findViewById(R.id.item_downloadable_image_imgImage);
            Group downloadIconGroup=view.findViewById(R.id.item_downloadable_image_group);
            downloadIconGroup.setVisibility(View.GONE);
            Glide.with(view.getContext()).load(downloadedImageUri).into(touchImageView);
        }
    }
    public final void downloadCompleted(int position){
        View view=parentPager.findViewWithTag(position);
        if (view!=null){
            downloadCompleted(position,getDownloadedImage(view.getContext().getApplicationContext(),position));
        }

    }
    public final void downloadFailed(int position){
        View view=parentPager.findViewWithTag(position);
        if (view!=null){
            ImageView imgDownload=view.findViewById(R.id.item_downloadable_image_download);
            Group downloadIconGroup=view.findViewById(R.id.item_downloadable_image_group);
            SmartProgressImageView progressImageView=view.findViewById(R.id.item_downloadable_image_loadingView);
            imgDownload.setImageResource(R.drawable.ic_download_white);
            progressImageView.stopAnimation();
            progressImageView.setVisibility(View.INVISIBLE);
        }
    }
    @CallSuper
    protected  void startDownload(Context context, TouchImageView touchImageView, int pos){
        mIsDownloading[pos]=true;
    }
    @CallSuper
    protected  void cancelDownload(Context context, int pos){
        mIsDownloading[pos]=false;
    }
    @Nullable
    protected abstract Uri getDownloadedImage(Context context, int position);

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);

    }



    public boolean isAutoDownloadEnabled() {
        return mAutoDownloadEnabled;
    }

    public void setAutoDownloadEnabled(boolean autoDownloadEnabled) {
        this.mAutoDownloadEnabled = autoDownloadEnabled;
    }


}
