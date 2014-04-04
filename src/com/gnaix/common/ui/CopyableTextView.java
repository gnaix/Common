package com.gnaix.common.ui;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CopyableTextView extends TextView implements OnClickListener {
    private ClipboardManager clipboard;

    public CopyableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public CopyableTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        setClickable(true);
        setOnClickListener(this);
    }

    private OnCopyListener mListener;

    public void setOnCopyListener(OnCopyListener l) {
        mListener = l;
    }

    public interface OnCopyListener {
        void onCopy(CharSequence text);
    }

    @Override
    public void onClick(View v) {
        setText(getText(), BufferType.SPANNABLE);
        clipboard.setText(getText());
        if (mListener != null) {
            mListener.onCopy(getText());
        }
        if (showHint) {
            Toast.makeText(getContext().getApplicationContext(), hint, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean showHint;

    public void showHint(boolean show) {
        showHint = show;
    }

    public String hint = "";

    public String getOnCopyHint() {
        return hint;
    }

    public void setOnCopyHint(String hint) {
        this.hint = hint;
    }
}
