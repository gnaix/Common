package com.gnaix.common.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gnaix.common.R;

public class ExpandableTextView extends LinearLayout implements OnClickListener{

    private static final int DEFAULT_SHRINK_LINES = 2;
    private boolean isExpanded = false;
    private int shrinkLines = DEFAULT_SHRINK_LINES;
    private TextView mTextView;
    private ImageView mIndicator;
    private Drawable indicatorUp,indicatorDown;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        this.mTextView = new TextView(context);
        this.mIndicator = new ImageView(context);
        this.addView(mTextView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        LayoutParams flagParam = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        flagParam.gravity = android.view.Gravity.RIGHT;
        this.addView(mIndicator, flagParam);
        
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ExpandableTextView);
        int lines = a.getInt(R.styleable.ExpandableTextView_shrinkLines, DEFAULT_SHRINK_LINES);
        this.setShrinkLines(lines);
        String text = a.getString(R.styleable.ExpandableTextView_text);
        this.mTextView.setText(text);
        
        ColorStateList textColor = a.getColorStateList(R.styleable.ExpandableTextView_textColor);
        this.mTextView.setTextColor(textColor != null ? textColor : ColorStateList.valueOf(0xFF000000));
        
        indicatorUp = a.getDrawable(R.styleable.ExpandableTextView_indicatorUp);
        if(indicatorUp == null) {
            indicatorUp = getResources().getDrawable(R.drawable.arrow_up);
        }
        indicatorDown = a.getDrawable(R.styleable.ExpandableTextView_indicatorDown);
        if(indicatorDown == null) {
            indicatorDown = getResources().getDrawable(R.drawable.arrow_down);
        }
        
        
        mIndicator.setImageDrawable(indicatorDown);
        this.setExpanded(false);
        a.recycle();
        
        this.setOnClickListener(this);
    }

    /**
     * @return the isExpanded
     */
    private boolean isExpanded() {
        return isExpanded;
    }

    /**
     * @param isExpanded
     *            the isExpanded to set
     */
    private void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
        if (isExpanded) {
            mIndicator.setImageDrawable(indicatorUp);;
            mTextView.setMaxLines(mTextView.getLineCount());
        } else {
            mIndicator.setImageDrawable(indicatorDown);
            mTextView.setMaxLines(shrinkLines);
        }
    }

    /**
     * 返回设置的收缩行数
     * @return the shrinkLines
     */
    public int getShrinkLines() {
        return shrinkLines;
    }

    /**
     * 设置收缩行数
     * @param shrinkLines
     *            the shrinkLines to set
     */
    public void setShrinkLines(int shrinkLines) {
        this.shrinkLines = shrinkLines;
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
        if (mTextView.getLineCount() <= shrinkLines) {
            mIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        setExpanded(!isExpanded());
    }
    
    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }
    
    public void setExpandIndicatorUp(Drawable drawable) {
        indicatorUp = drawable;
    }
    
    public void setExpandIndicatorUp(int resId) {
        indicatorUp = getResources().getDrawable(resId);
    }
    
    public void setExpandIndicatorDown(Drawable drawable) {
        indicatorDown = drawable;
        mIndicator.setImageDrawable(indicatorDown);
    }
    
    public void setExpandIndicatorDown(int resId) {
        indicatorDown = getResources().getDrawable(resId);
    }
}
