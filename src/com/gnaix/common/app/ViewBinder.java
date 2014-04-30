package com.gnaix.common.app;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;

public abstract class ViewBinder implements Observer{
    
    private WeakReference<Context> mContextRef;
    
    public ViewBinder(Context context) {
        mContextRef = new WeakReference<Context>(context);
    }
    

    public abstract void initView();
    


    @Override
    public void update(Observable observable, Object data) {
        // TODO Auto-generated method stub
    }
    
    
}
