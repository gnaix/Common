package com.gnaix.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PullRefreshListView extends LinearLayout implements OnScrollListener {
    private boolean mPullRefreshing = false;
    private boolean mLoadingMore = false;
    private boolean mReadyToPull = false;
    private int mPullStartY;
    private ListView delegate;

    private float mScrollFactor = 0.75f;
    private static final int DEFAULT_LIMIT = 5;

    private int minPullDownDistance = 300;

    private OnPullListener mPullListener;

    public PullRefreshListView(Context context) {
        this(context, null);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setScrollFactor(float distance) {
        mScrollFactor = distance;
    }

    public float getScrollFactor() {
        return mScrollFactor;
    }

    @SuppressLint("NewApi")
    public PullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOrientation(VERTICAL);
        delegate = new ListView(context, attrs, defStyle);
        delegate.setId(NO_ID);
        delegate.setOnScrollListener(this);
        addView(delegate, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setAdapter(ListAdapter adapter) {
        delegate.setAdapter(adapter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        delegate.setOnItemClickListener(listener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mPullStartY = (int) ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:

            break;
        case MotionEvent.ACTION_UP:
            int y = (int) ev.getY();
            int deltaY = y - mPullStartY;
            if (deltaY > 0 && deltaY > minPullDownDistance) {//下拉
                if (!mPullRefreshing && mReadyToPull && mPullListener != null) {
                    Log.i("PullRefreshListView", "pull refresh");
                    mPullListener.onPullRefresh();
                    mPullRefreshing = true;
                }
            }
            mPullStartY = 0;
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (totalItemCount > 0) {
            if ((firstVisibleItem + visibleItemCount) >= totalItemCount / 3 * 2) {
                if (!mPullRefreshing && !mLoadingMore && mPullListener != null) {
                    Log.i("PullRefreshListView", "load more");
                    mPullListener.onLoadMore();
                    mLoadingMore = true;
                }
            }
        }
        if (firstVisibleItem == 0) {
            mReadyToPull = true;
        } else {
            mReadyToPull = false;
        }
    }

    public final void completeLoadMore() {
        mLoadingMore = false;
        Log.i("PullRefreshListView", "complete load more");
    }

    public final void completeRefresh() {
        mPullRefreshing = false;
        Log.i("PullRefreshListView", "complete refresh");
    }

    public interface OnPullListener {

        void onPullRefresh();

        void onLoadMore();
    }

    public void setOnPullListener(OnPullListener pullListener) {
        this.mPullListener = pullListener;
    }

}
