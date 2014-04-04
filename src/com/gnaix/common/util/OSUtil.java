package com.gnaix.common.util;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

public class OSUtil {
    private static final String TAG = "OSUtil";

    private static List<String> INVALID_IMEIs = new ArrayList<String>();
    static {
        INVALID_IMEIs.add("358673013795895");
        INVALID_IMEIs.add("004999010640000");
        INVALID_IMEIs.add("00000000000000");
        INVALID_IMEIs.add("000000000000000");
    }

    public static boolean isValidImei(String imei) {
        if (TextUtils.isEmpty(imei))
            return false;
        if (imei.length() < 10)
            return false;
        if (INVALID_IMEIs.contains(imei))
            return false;
        return true;
    }
    

    
    /**
     * @see http://code.google.com/p/android/issues/detail?id=10603
     */
    private static final String INVALID_ANDROIDID = "9774d56d682e549c";

    /**
     * 取得设备的唯一标识
     * 
     * imei -> androidId -> mac address -> uuid
     * 
     * 
     * @param context
     * @return
     */
    public static String getUdid(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String udid = telephonyManager.getDeviceId();

        if (isValidImei(udid)) {
            return udid;
        }

        udid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(udid)
                && !INVALID_ANDROIDID.equals(udid.toLowerCase(Locale.getDefault()))) {
            return udid;
        }

        String macAddress = getWifiMacAddress(context);
        if (!TextUtils.isEmpty(macAddress)) {
            udid = toMD5(macAddress + Build.MODEL + Build.MANUFACTURER + Build.ID + Build.DEVICE);
            return udid;
        }
        String name = String.valueOf(System.currentTimeMillis());
        udid = UUID.nameUUIDFromBytes(name.getBytes()).toString();
        return udid;
    }

    public static String toMD5(String encTarget) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        }
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        return md5;
    }

    public static String getWifiMacAddress(final Context context) {
        try {
            WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifimanager.getConnectionInfo().getMacAddress();
            if (TextUtils.isEmpty(mac))
                return null;
            return mac;
        } catch (Exception e) {
            Log.d(TAG, "Get wifi mac address error", e);
        }
        return null;
    }

    // 打印所有的 intent extra 数据
    public static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
        }
        return sb.toString();
    }

    public static String getNetworkTypeName(TelephonyManager telephonyManager) {
        int type = telephonyManager.getNetworkType();
        String name = "Unknown";
        switch (type) {
        case TelephonyManager.NETWORK_TYPE_GPRS:
            name = "GPRS";
            break;
        case TelephonyManager.NETWORK_TYPE_EDGE:
            name = "EDGE";
            break;
        case TelephonyManager.NETWORK_TYPE_CDMA:
            name = "CDMA";
            break;
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            name = "EVDO_0";
            break;
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            name = "EVDO_A";
            break;
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            name = "HSDPA";
            break;
        case TelephonyManager.NETWORK_TYPE_HSPA:
            name = "HSPA";
            break;
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            name = "HSUPA";
            break;
        case TelephonyManager.NETWORK_TYPE_UMTS:
            name = "UMTS";
            break;
        default:
        }
        return name;
    }

    public static boolean is2gNetwork(TelephonyManager telephonyManager) {
        int type = telephonyManager.getNetworkType();
        if (type == TelephonyManager.NETWORK_TYPE_GPRS
                || type == TelephonyManager.NETWORK_TYPE_EDGE) {
            return true;
        }
        return false;
    }

    public static int getCurrentSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * Last gps location -> last network location -> null
     * 
     * @param context
     * @return
     */
    public static Location getLocation(Context context) {
        LocationManager locMan = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    public static void sendEmail(Context context, String chooserTitle, String mailAddress,
            String subject, String preContent) {
        Log.v(TAG, "action: sendEmail");
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mailAddress });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        String content = "\n\n=====================\n";
        content += "Device Environment: \n----\n" + preContent;
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(emailIntent, chooserTitle));
    }

    // some apps only show content, some apps show both subject and content
    public static Intent getAndroidShareIntent(CharSequence chooseTitle, CharSequence subject,
            CharSequence content) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        return Intent.createChooser(shareIntent, chooseTitle);
    }

    public static Intent getAndroidImageShareIntent(CharSequence chooseTitle, String pathfile) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathfile));
        return Intent.createChooser(share, chooseTitle);
    }

    /**
     * 
     * @see android.telephony.TelephonyManager#getDeviceId()
     * @param context
     * @return
     */
    public static String getImei(TelephonyManager telephonyManager) {
        return telephonyManager.getDeviceId();
    }

    /**
     * @see android.telephony.TelephonyManager#getSubscriberId()
     * @param context
     * @return
     */
    public static String getImsi(TelephonyManager telephonyManager) {
        return telephonyManager.getSubscriberId();
    }

    public static String getSimSerialNumber(TelephonyManager telephonyManager) {
        return telephonyManager.getSimSerialNumber();
    }

    public static int getMcc(TelephonyManager telephonyManager) {
        String imsi = getImsi(telephonyManager);
        if (!TextUtils.isEmpty(imsi))
            return Integer.valueOf(imsi.substring(0, 3));
        return -1;
    }
    
    public static int getMnc(TelephonyManager telephonyManager) {
        String imsi = getImsi(telephonyManager);
        if (!TextUtils.isEmpty(imsi))
            return Integer.valueOf(imsi.substring(3, 5));
        return -1;
    }
    
    /**
     * dip转换成px
     * @param context
     * @param dip
     * @return
     */
    public static float dip2px(Context context,int dip){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getApplicationContext().getResources().getDisplayMetrics());
    }
}
