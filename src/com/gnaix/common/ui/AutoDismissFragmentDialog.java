package com.gnaix.common.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

public class AutoDismissFragmentDialog extends DialogFragment {
    private static final int DEFAULT_DURATION = 20000;
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_MESSAGE = "MESSAGE";
    private static final String KEY_DURATION = "DURATION";
    private static final String KEY_IS_AUTO_DISMISS = "IS_AUTO_DISMISS";

    private AutoDismissListener mAutoDismissListener;

    private Handler mHandler;
    private String mTag;

    private String getDialogTag() {
        return mTag;
    }

    public interface AutoDismissListener {

        void onAutoDismiss(AutoDismissFragmentDialog dialog);
    }

    public AutoDismissFragmentDialog() {
        super();
        mTag = String.valueOf(SystemClock.elapsedRealtime());
        mHandler = new MyHandler(this);
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_AUTO_DISMISS, true);
        args.putInt(KEY_DURATION, DEFAULT_DURATION);
        setArguments(new Bundle());
    }

    /**
     * @param manager
     * @param title
     * @param message
     * @param duration
     * @param listener
     * @return The tag for this fragment
     */
    public static AutoDismissFragmentDialog create(FragmentManager manager, String title,
            String message, int duration, AutoDismissListener listener) {
        AutoDismissFragmentDialog dialog = new AutoDismissFragmentDialog();
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_AUTO_DISMISS, true);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putInt(KEY_DURATION, duration);
        dialog.setArguments(args);
        dialog.setAutoDismissListener(listener);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            dialog.setMessage(message);
        }
        return dialog;
    }

    public static class MyHandler extends Handler {

        private WeakReference<AutoDismissFragmentDialog> dialogRef;

        public MyHandler(AutoDismissFragmentDialog dialog) {
            this.dialogRef = new WeakReference<AutoDismissFragmentDialog>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            AutoDismissFragmentDialog dialog = this.dialogRef.get();
            if (dialog != null) {
                dialog.dismiss();
                if (dialog.mAutoDismissListener != null) {
                    dialog.mAutoDismissListener.onAutoDismiss(dialog);
                }
            }
        }

    }

    public void show(FragmentManager fragmentManager) {
/*      try {
            Field mDismissedField = DialogFragment.class.getDeclaredField("mDismissed");
            mDismissedField.setAccessible(true);
            mDismissedField.setBoolean(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            Field mShownByMeField = DialogFragment.class.getDeclaredField("mShownByMe");
            mShownByMeField.setAccessible(true);
            mShownByMeField.setBoolean(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, mTag);
        ft.commit();
        if (isAutoDismiss()) {
            mHandler.sendEmptyMessageDelayed(0, getDuration());
        }*/
        try {
            super.show(fragmentManager, mTag);
            if (isAutoDismiss()) {
                mHandler.sendEmptyMessageDelayed(0, getDuration());
            }
        } catch (IllegalStateException e) {
            Log.e(AutoDismissDialog.class.getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (IllegalStateException e) {
            Log.e(AutoDismissDialog.class.getSimpleName(), e.getMessage());
        }
    }

    /**
     * @return the isAutoDismiss
     */
    public boolean isAutoDismiss() {
        return getArguments().getBoolean(KEY_IS_AUTO_DISMISS);
    }

    /**
     * @param isAutoDismiss
     *            the isAutoDismiss to set
     */
    public void setAutoDismiss(boolean isAutoDismiss) {
        getArguments().putBoolean(KEY_IS_AUTO_DISMISS, isAutoDismiss);
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        int duration = getArguments().getInt(KEY_DURATION);
        if (duration <= 0) {
            getArguments().putInt(KEY_DURATION, DEFAULT_DURATION);
        }
        return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(int duration) {
        getArguments().putInt(KEY_DURATION, duration);
        if (duration <= 0) {
            getArguments().putInt(KEY_DURATION, 20000);
        }
    }

    /**
     * @param mCancelListener
     *            the mCancelListener to set
     */
    public void setAutoDismissListener(AutoDismissListener listener) {
        this.mAutoDismissListener = listener;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        String message = getArguments().getString(KEY_MESSAGE);
        return TextUtils.isEmpty(message) ? "" : message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        getArguments().putString(KEY_MESSAGE, message);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        String title = getArguments().getString(KEY_TITLE);
        return TextUtils.isEmpty(title) ? "" : title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        getArguments().putString(KEY_TITLE, title);
    }
}
