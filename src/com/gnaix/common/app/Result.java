package com.gnaix.common.app;

import java.io.Serializable;

public class Result implements Serializable{
    
    public int statusCode;
    public String errorMessage;
    public Object mResult;
    public int apiCode;
    
    public static final int OK = 1;
    public static final int FAIL = -1;
    public static final int ERROR_NETWORK = -10;
    public static final int ERROR_NETWORK_CONNECT_TIMEOUT = -11;
    public static final int ERROR_NETWORK_CONNECT_HOST = -12;
    public static final int ERROR_NETWORK_RESPONSE = -13;
    public static final int ERROR_NETWORK_REDIRECT = -14;
    public static final int ERROR_NETWORK_SOCKET_TIMEOUT = -15;
    public static final int ERROR_JSON_PARSE = -20;
    public static final int ERROR_UNKNOW = -100;
    public static final int ERROR_TOKEN_EXPIRE = 10006;

    
    public Result(){
        statusCode = FAIL;
    }
    
}
