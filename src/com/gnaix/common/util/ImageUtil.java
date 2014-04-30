package com.gnaix.common.util;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.util.Log;

public class ImageUtil {
    private static final String TAG = "ImageUtil";

    /**
     * 获取图片的缩放比
     * 
     * @param srcWidth
     * @param srcHeight
     * @param maxWidth
     * @param maxHeight
     * @return scale
     */
    public static int getScale(int srcWidth, int srcHeight, int maxWidth, int maxHeight) {
        if (srcWidth > maxWidth || srcHeight > maxHeight)
            return Math.max(Math.round((float) srcHeight / (float) maxHeight),
                    Math.round((float) srcWidth / (float) maxWidth));
        else
            return 1;
    }

    /**
     * Get size of image
     * 
     * @param imagePath
     * @return size
     */
    public static Point getSize(final String imagePath) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            BitmapFactory.decodeFileDescriptor(file.getFD(), null, options);
            return new Point(options.outWidth, options.outHeight);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
        }
    }
}
