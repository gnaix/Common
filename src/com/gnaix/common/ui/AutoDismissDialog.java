package com.gnaix.common.ui;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

public class AutoDismissDialog extends ProgressDialog {
    private boolean isAutoDismiss;
    private int duration;

    private AutoDismissListener mAutoDismissListener;

    private Handler mHandler;

    public interface AutoDismissListener {

        void onAutoDismiss(DialogInterface dialog);
    }

    public AutoDismissDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setIndeterminate(true);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        mHandler = new MyHandler(this);
    }

    public static class MyHandler extends Handler {

        private WeakReference<AutoDismissDialog> dialogRef;

        public MyHandler(AutoDismissDialog dialog) {
            this.dialogRef = new WeakReference<AutoDismissDialog>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            AutoDismissDialog dialog = this.dialogRef.get();
            if(dialog != null) {
                if (dialog.isShowing()) {
                    try{
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if (dialog.mAutoDismissListener != null) {
                        dialog.mAutoDismissListener.onAutoDismiss(dialog);
                    }
                }
            }
        }

    }
    
    public static AutoDismissDialog show(Context context, String title, String message,
           int duration, AutoDismissListener listener) {
        AutoDismissDialog dialog = new AutoDismissDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setAutoDismiss(true);
        dialog.setAutoDismissListener(listener);
        dialog.setDuration(duration);
        dialog.show();
        return dialog;
    }

    public void show() {
        if (isAutoDismiss()) {
            mHandler.sendEmptyMessageDelayed(0, getDuration());
        }
        super.show();
    }

    /**
     * @return the isAutoDismiss
     */
    public boolean isAutoDismiss() {
        return isAutoDismiss;
    }

    /**
     * @param isAutoDismiss
     *            the isAutoDismiss to set
     */
    public void setAutoDismiss(boolean isAutoDismiss) {
        this.isAutoDismiss = isAutoDismiss;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
        if(this.duration <= 0) {
            this.duration = 20000;
        }
    }

    /**
     * @param mCancelListener
     *            the mCancelListener to set
     */
    public void setAutoDismissListener(AutoDismissListener listener) {
        this.mAutoDismissListener = listener;
    }

}
