package com.gnaix.common.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;

import android.content.Context;
import android.text.TextUtils;

import com.gnaix.common.BuildConfig;
import com.gnaix.common.util.NetworkUtil;

public class BaseApiClient {
    protected static final String TAG = BaseApiClient.class.getSimpleName();
    //private AndroidHttpClient mHttpClient;
    protected Context mContext;
    protected String mServer;
    protected String mIP;

    private static int eventId = 5000;

    public static DefaultHttpClient sLongHttpClient;
    public static String mConnType;
    public final static String SHORT_CONNECTION = "0";
    public final static String LONG_CONNECTION = "1";
    
    public static final int HTTP_TIMEOUT = 30000;
    public static final int SOCKET_TIMEOUT = 30000;

    public static HttpClient getHttpClient(Context context) {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        System.out.println("Create Short Connection");
        int waptype = NetworkUtil.getNetworkType(context);
        if (waptype == NetworkUtil.TYPE_CM_CU_WAP) {
            System.out.println("is cmwap or unwap");
            HttpHost httpHost = new HttpHost("10.0.0.172", 80);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
        } else if (waptype == NetworkUtil.TYPE_CT_WAP) {
            System.out.println("is ctwap");
            HttpHost httpHost = new HttpHost("10.0.0.200", 80);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
        }
        return httpClient;
    }

    public HttpResponse excute(Context context, String url) throws Exception {
        if (!BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(mIP)) {
                String ip = NetworkUtil.getIP(getServer());
                if (!TextUtils.isEmpty(ip)) {
                    mIP = ip;
                }
            }
        }
        HttpGet request = new HttpGet(url);
        request.addHeader("Connection", "Close");
        modifyRequestToAcceptGzipResponse(request);
        //        HttpClient client = getHttpClient(context);
        HttpClient client = getHttpClient(context);
        if (NetworkUtil.isWap(context)) {
            return client.execute(request);
        } else {
            return client.execute(new HttpHost(getServer(), 80), request);
        }
    }

    public BaseApiClient(Context context) {
        //mHttpClient = AndroidHttpClient.newInstance("Android");
        mContext = context;
        //        value = MobclickAgent.getConfigParams(mContext, "send_error_request_fail");
        //        mConnType = MobclickAgent.getConfigParams(mContext, "connection_type");
        mConnType = SHORT_CONNECTION;
    }

    protected synchronized int generateEventId() {
        if (eventId >= Integer.MAX_VALUE - 1) {
            eventId = 5000;
        }
        eventId++;
        return eventId;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        is.close();

        return sb.toString();
    }

    protected String getDN() {
        return "http://" + (TextUtils.isEmpty(mIP) || NetworkUtil.isWap(mContext) ? mServer : mIP);
    }

    public void processException(Result resp, Exception e, String content) {
        processException(resp, e);
    }

    public void processException(Result resp, Exception e) {
        if (e instanceof JSONException) {
            resp.statusCode = Result.ERROR_JSON_PARSE;
        } else if (e instanceof SocketTimeoutException) {
            resp.statusCode = Result.ERROR_NETWORK_SOCKET_TIMEOUT;
        } else if (e instanceof ConnectTimeoutException) {
            resp.statusCode = Result.ERROR_NETWORK_CONNECT_TIMEOUT;
        } else if (e instanceof HttpHostConnectException || e instanceof UnknownHostException
                || e instanceof ConnectException) {
            resp.statusCode = Result.ERROR_NETWORK_CONNECT_HOST;
        } else if (e instanceof HttpResponseException || e instanceof NoHttpResponseException) {
            resp.statusCode = Result.ERROR_NETWORK_RESPONSE;
        } else if (e instanceof RedirectException) {
            resp.statusCode = Result.ERROR_NETWORK_REDIRECT;
        } else if (e instanceof SocketException || e instanceof ProtocolException
                || e instanceof java.net.ProtocolException) {
            resp.statusCode = Result.ERROR_NETWORK;
        } else {
            resp.statusCode = Result.ERROR_UNKNOW;
        }
        e.printStackTrace();
    }

    public static InputStream getUngzippedContent(HttpEntity entity) throws IOException {
        InputStream responseStream = entity.getContent();
        if (responseStream == null)
            return responseStream;
        Header header = entity.getContentEncoding();
        if (header == null)
            return responseStream;
        String contentEncoding = header.getValue();
        if (contentEncoding == null)
            return responseStream;
        if (contentEncoding.contains("gzip"))
            responseStream = new GZIPInputStream(responseStream);
        return responseStream;
    }

    public static void modifyRequestToAcceptGzipResponse(HttpRequest request) {
        request.addHeader("Accept-Encoding", "gzip");
    }

    public String getServer() {
        return mServer;
    }

    public void setServer(String mServer) {
        this.mServer = mServer;
    }
}