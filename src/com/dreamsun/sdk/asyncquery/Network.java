package com.dreamsun.sdk.asyncquery;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @Project: VSee
 * @Description: 网络相关包装工具类
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class Network {
	
	private static final String LOG_TAG = Network.class.getSimpleName();

	public static boolean isAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == cm) {
			return false;
		} else {
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (null == ni || !ni.isAvailable() || !ni.isConnected()) {
				return false;
			} else {
				if (ni.getState() == NetworkInfo.State.CONNECTED) { 
//					if (tryConnect()) {
						return true;
//					}
				}
			}	
		}
		return false;
	}

	public static boolean is3G(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
	
	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	public static boolean is2G(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && (ni.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE 
				          || ni.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS 
				          || ni.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
			return true;
		}
		return false;
	}
	
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((cm.getActiveNetworkInfo() != null 
				&& cm.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) 
				|| tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}
	
	public static boolean tryConnect() {
		boolean isConnect = false;
		HttpURLConnection conn = null; 
		try {
			URL url = new URL("http://www.baidu.com/");
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(5000);
			conn.connect();
			isConnect = true;
		} catch (SocketTimeoutException se) {
			Log.e(LOG_TAG, "Socket连接超时发生异常: " + se);
		} catch (ConnectTimeoutException ce) {
			Log.e(LOG_TAG, "Connect连接超时发生异常:" + ce);
		} catch (IOException ioe) {
			Log.e(LOG_TAG, "读取服务器数据发生I/O异常:" + ioe);
		} catch (Exception e) {
			Log.e(LOG_TAG, "HTTP请求发生未知异常:" + e);
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}
		return isConnect;
	}
	
	public static boolean isFastMobileNetwork(Context context) {  
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
		switch (tm.getNetworkType()) {  
	       case TelephonyManager.NETWORK_TYPE_1xRTT:  
	           return false; // ~ 50-100 kbps  
	           
	       case TelephonyManager.NETWORK_TYPE_CDMA:  
	           return false; // ~ 14-64 kbps  
	           
	       case TelephonyManager.NETWORK_TYPE_EDGE:  
	           return false; // ~ 50-100 kbps 
	           
	       case TelephonyManager.NETWORK_TYPE_EVDO_0:  
	           return true; // ~ 400-1000 kbps 
	           
	       case TelephonyManager.NETWORK_TYPE_EVDO_A:  
	           return true; // ~ 600-1400 kbps  
	           
	       case TelephonyManager.NETWORK_TYPE_GPRS:  
	           return false; // ~ 100 kbps  
	           
	       case TelephonyManager.NETWORK_TYPE_HSDPA:  
	           return true; // ~ 2-14 Mbps  
	           
	       case TelephonyManager.NETWORK_TYPE_HSPA:  
	           return true; // ~ 700-1700 kbps  
	           
	       case TelephonyManager.NETWORK_TYPE_HSUPA:  
	           return true; // ~ 1-23 Mbps  
	           
	       case TelephonyManager.NETWORK_TYPE_UMTS:  
	           return true; // ~ 400-7000 kbps 
	           
	       case TelephonyManager.NETWORK_TYPE_EHRPD:  
	           return true; // ~ 1-2 Mbps  
	           
	       case TelephonyManager.NETWORK_TYPE_EVDO_B:  
	           return true; // ~ 5 Mbps  
	           
	       case TelephonyManager.NETWORK_TYPE_HSPAP:  
	           return true; // ~ 10-20 Mbps  
	           
	       case TelephonyManager.NETWORK_TYPE_IDEN:  
	           return false; // ~25 kbps  
	           
	       case TelephonyManager.NETWORK_TYPE_LTE:  
	           return true; // ~ 10+ Mbps 
	           
	       case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
	           return false; 
	           
	       default:  
	           return false;  
	    }
	}

}


