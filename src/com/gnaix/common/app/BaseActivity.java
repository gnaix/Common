package com.gnaix.common.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityStateManager.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityStateManager.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        ActivityStateManager.onDestroy(this);
        super.onDestroy();
    }
    
    @Override
    protected void onStop() {
        ActivityStateManager.onStop(this);
        super.onStop();
    }
    
}
