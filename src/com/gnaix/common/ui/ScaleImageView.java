package com.gnaix.common.ui;

import com.gnaix.common.util.OSUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;


public class ScaleImageView extends ImageView implements OnGlobalLayoutListener {
    
    private ViewTreeObserver mObserver;
    private float scaleWidth = 1.0f;
    private float scaleHeight = 1.0f;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mObserver = this.getViewTreeObserver();
        mObserver.addOnGlobalLayoutListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
    @Override
    public void onGlobalLayout() {
        int width = getWidth();
        if(mObserver.isAlive()){
            if(OSUtil.getCurrentSdkVersion() >= Build.VERSION_CODES.JELLY_BEAN){
                mObserver.removeOnGlobalLayoutListener(this);
            }else{
                mObserver.removeGlobalOnLayoutListener(this);
            }
        }
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = (int) (width / scaleWidth * scaleHeight);
        this.setLayoutParams(params);
    }

    public float getScaleWidth() {
        return scaleWidth;
    }
    
    public float getScaleHeight() {
        return scaleHeight;
    }

    public void setScale(float scaleWidth,float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
    }

}

