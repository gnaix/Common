package com.gnaix.common.app;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public abstract class BaseService extends Service {

    protected static final int MAX_WORK_THREAD = 5;

    protected ExecutorService pool;
    protected Map<Integer, ServiceCallback> observers;
    protected static int messageId = 1000;
    public static final int REQUEST_CALLBACK = 0;
    protected CallbackHandler mHandler;
    
    public Handler getMessageHandler() {
        return mHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("service onCreate()");
        mHandler = new CallbackHandler(this);
        pool = Executors.newFixedThreadPool(MAX_WORK_THREAD);
        observers = Collections.synchronizedMap(new HashMap<Integer, ServiceCallback>());
    }

    @Override
    public void onDestroy() {
        try {
            if (pool != null && !pool.isShutdown()) {
                pool.shutdown();
            }
            observers.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void registerServiceCallBack(ServiceCallback callback) {
        observers.put(callback.hashCode(), callback);
    }

    public void unregisterServiceCallBack(ServiceCallback callback) {
        observers.remove(callback.hashCode());
    }

    public interface ServiceCallback {
        public void callback(int messageId, Result result);
    }

    public synchronized int generateID() {
        if (messageId >= Integer.MAX_VALUE - 1) {
            messageId = 1000;
        }
        messageId++;
        return messageId;
    }

    public void sendResult(Result result,int messageId, int hashcode){
        Message msg = getMessageHandler().obtainMessage(REQUEST_CALLBACK);
        msg.obj = result;
        msg.arg1 = hashcode;
        msg.arg2 = messageId;
        getMessageHandler().sendMessage(msg);
    }
    
    static class CallbackHandler extends Handler {
        private final WeakReference<BaseService> mService;

        CallbackHandler(BaseService service) {
            mService = new WeakReference<BaseService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseService service = mService.get();
            if (service != null && msg.what == REQUEST_CALLBACK) {
                for (ServiceCallback callback : service.observers.values()) {
                    if (msg.arg1 == callback.hashCode()) {
                        callback.callback(msg.arg2, (Result) msg.obj);
                        break;
                    }
                }

            }
        }
    }

}