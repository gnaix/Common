package com.gnaix.common.util;

import java.net.UnknownHostException;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class NetworkUtil {
    public static final String CTWAP = "ctwap";
    public static final String CMWAP = "cmwap";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    public static final int TYPE_NET_WORK_DISABLED = 0;
    public static final int TYPE_OTHER_NET = 1;
    public static final int TYPE_CM_CU_WAP = 2;
    public static final int TYPE_CT_WAP = 3;

    public static String getIP(String host) {
        java.net.InetAddress x;
        try {
            x = java.net.InetAddress.getByName(host);
            String ip_devdiv = x.getHostAddress();
            return ip_devdiv;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cnmger = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cnmger.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    public static boolean isNetworkTypeAvailable(Context context, int networkType) {
        ConnectivityManager cnmger = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cnmger.getNetworkInfo(networkType);
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    public static boolean isWap(Context context) {
        return getNetworkType(context) > 1;
    }

    public static int getNetworkType(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null) {
            String type = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(type)) {
                if (type.toLowerCase(Locale.getDefault()).contains(CMWAP)
                        || type.toLowerCase(Locale.getDefault()).contains(WAP_3G)
                        || type.toLowerCase(Locale.getDefault()).contains(UNIWAP))
                    return TYPE_CM_CU_WAP;
                else if (type.toLowerCase(Locale.getDefault()).contains(CTWAP))
                    return TYPE_CT_WAP;
                else
                    return TYPE_OTHER_NET;
            }
        }
        return TYPE_OTHER_NET;
    }

    public static boolean isWifi(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null
                && !networkInfo.getTypeName().toLowerCase(Locale.getDefault()).equals("wifi")) {
            return true;
        }
        return false;
    }

}