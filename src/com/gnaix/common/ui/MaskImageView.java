package com.gnaix.common.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class MaskImageView extends ScaleImageView {

    private boolean isHover = false;
    private Paint mPaint;
    
    private int maskColor = 0x50FFFFFF;

    public MaskImageView(Context context) {
        this(context, null);
    }

    public MaskImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isHover) {
            canvas.drawColor(Color.TRANSPARENT);
        } else {
            canvas.drawColor(getMaskColor());
        }
    }

    public void setHover(boolean hover) {
        isHover = hover;
        this.invalidate();
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

}
