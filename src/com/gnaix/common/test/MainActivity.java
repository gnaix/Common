package com.gnaix.common.test;

import com.gnaix.common.R;
import com.gnaix.common.ui.ExpandableTextView;
import com.gnaix.common.ui.PullRefreshListView;
import com.gnaix.common.ui.PullRefreshListView.OnPullListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnPullListener {

    private PullRefreshListView mPullRefreshLayout;
    String[] mStrings = new String[] { "1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2", "3",
            "1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2",
            "3", "1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2", "3", "1",
            "2", "3" };

    private Handler hander = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullRefreshLayout = (PullRefreshListView) findViewById(R.id.list);
        mPullRefreshLayout.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                mStrings));
        
        mPullRefreshLayout.setOnPullListener(this);
    }

    @Override
    public void onPullRefresh() {
        Toast.makeText(this, "is refreshing", Toast.LENGTH_SHORT).show();
        hander.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                mPullRefreshLayout.completeRefresh();                
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(this, "is loading more", Toast.LENGTH_SHORT).show();
        hander.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                mPullRefreshLayout.completeLoadMore(); 
            }
        }, 3000);
    }
}
