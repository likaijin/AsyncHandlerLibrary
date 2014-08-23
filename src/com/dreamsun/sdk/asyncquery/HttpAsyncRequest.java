package com.dreamsun.sdk.asyncquery;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

/**
 * @Project: VSee
 * @Description: 异步非阻塞Request
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class HttpAsyncRequest {

	private static final String LOG_TAG = HttpAsyncRequest.class.getSimpleName();

	private Handler handler;

	private HttpUriRequest request;

	private String uri;

	private Map<String, Object> params;

	private HTTPMethod method;

	private ProcessReceivedDataCallback callback;
	
	private FileForm fileFormData;

	public HttpAsyncRequest(String uri, Map<String, Object> params, HTTPMethod method, ProcessReceivedDataCallback callback) {
		this.uri = uri;
		this.params = params;
		this.method = method;
		this.callback = callback;
	}
	
	public HTTPMethod getMethod() {
		return this.method;
	}

	public HttpUriRequest getRequest() {
		return this.request;
	}
	
	public FileForm getFileFormData() {
		return this.fileFormData;
	}

	public void serializeData() {
		if (this.method == HTTPMethod.GET) {
			this.serializeGetData();
		} else if (this.method == HTTPMethod.POST) {
			this.serializePostData();
		} else if (this.method == HTTPMethod.OCTET_STREAM) {
			this.serializePostDataOfBoundary();
		} else if (this.method == HTTPMethod.XML) {
			this.serializePostDataOfXML();
		} else {
			Log.e(LOG_TAG, "未知HTTP请求格式.");
			return;
		}
	}

	// 普通GET请求
	private void serializeGetData() {
		StringBuilder url = new StringBuilder(CommonWrapper.getInstance().getHomeBaseUrl());
		if (TextUtils.isEmpty(url)) {
			Log.e(LOG_TAG, "非法请求.");
			return;
		}
		if (!TextUtils.isEmpty(this.uri)) {
			url.append(this.uri);
		}
		if (null == this.params || this.params.isEmpty() || this.params.size() == 0) {
			Log.i(LOG_TAG, "HTTP GET Request: " + url.toString());
			this.request = new HttpGet(url.toString());
			return;
		}
		url.append("?");
		Iterator<String> it = this.params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object val = this.params.get(key);
			if (val instanceof String || val instanceof Integer
					|| val instanceof Boolean || val instanceof Double
					|| val instanceof Float || val instanceof Long) {
				try {
					url.append(key).append("=").append(java.net.URLEncoder.encode(String.valueOf(val), HTTP.UTF_8)).append("&");
				} catch (UnsupportedEncodingException e) {
					Log.e(LOG_TAG, "未知编码格式: ", e);
					return;
				}
			} else {
				Log.e(LOG_TAG, "未知参数格式.");
				return;
			}
		}
		if (url.lastIndexOf("&") > -1) {
			String fullURL = url.substring(0, url.lastIndexOf("&"));
			Log.i(LOG_TAG, "HTTP GET Request: " + fullURL);
			this.request = new HttpGet(fullURL);
		}
	}

	// 普通POST请求
	@SuppressWarnings("rawtypes")
	private void serializePostData() {
		StringBuilder url = new StringBuilder(CommonWrapper.getInstance().getHomeBaseUrl());
		if (TextUtils.isEmpty(url)) {
			Log.e(LOG_TAG, "非法请求.");
			return;
		}
		if (!TextUtils.isEmpty(this.uri)) {
			url.append(this.uri);
		}
		if (null == this.params || this.params.isEmpty() || this.params.size() == 0) {
			Log.i(LOG_TAG, "HTTP POST Request: " + url.toString());
			this.request = new HttpPost(url.toString());
			return;
		}
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		Iterator<String> it = this.params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object val = this.params.get(key);
			if (val instanceof String || val instanceof Integer
					|| val instanceof Boolean || val instanceof Double
					|| val instanceof Float || val instanceof Long) {
				nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(val)));
			} else if (val instanceof List) {
				List pList = (List) val;
				for (int i = 0; i < pList.size(); i++) {
					nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(pList.get(i))));
				}
			} else {
				Log.e(LOG_TAG, "未知参数格式.");
				return;
			}
		}
		if (null != nameValuePairs && nameValuePairs.size() > 0) {
			HttpPost httpPost = new HttpPost(url.toString());
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG, "未知编码格式: ", e);
				return;
			}
			Log.i(LOG_TAG, "HTTP POST Request: " + url.toString() + ", Params: " + httpPost.getEntity());
			this.request = httpPost;
		}
	}
	
	// 报文处理请求
	private void serializePostDataOfXML() {
		StringBuilder url = new StringBuilder(CommonWrapper.getInstance().getHomeBaseUrl());
		if (TextUtils.isEmpty(url)) {
			Log.e(LOG_TAG, "非法请求.");
			return;
		}
		if (!TextUtils.isEmpty(this.uri)) {
			url.append(this.uri);
		}
		if (null == this.params || this.params.isEmpty() || this.params.size() == 0) {
			Log.e(LOG_TAG, "参数不能为空.");
			return;
		}
		HttpPost httpPost = new HttpPost(url.toString());
		Iterator<String> it = this.params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object val = this.params.get(key);
			if (val instanceof String) {
				httpPost.setEntity(new ByteArrayEntity(((String) val).getBytes()));
				break;
			} else if (val instanceof byte[]) {
				httpPost.setEntity(new ByteArrayEntity((byte[]) val));
				break;
			} else {
				Log.e(LOG_TAG, "未知参数格式.");
				return;
			}
		}
		this.request = httpPost;
	}

	// 文件流处理请求
	private void serializePostDataOfBoundary() {
		StringBuilder url = new StringBuilder(CommonWrapper.getInstance().getHomeBaseUrl());
		if (TextUtils.isEmpty(url)) {
			Log.e(LOG_TAG, "非法请求.");
			return;
		}
		if (!TextUtils.isEmpty(this.uri)) {
			url.append(this.uri);
		}
		if (null == this.params || this.params.isEmpty() || this.params.size() == 0) {
			Log.e(LOG_TAG, "参数不能为空.");
			return;
		}
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		FileForm fileForm = new FileForm();
		Iterator<String> it = this.params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object val = this.params.get(key);
			if (val instanceof byte[]) {
				fileForm.setData((byte[]) val);  // 获取file文件字节流数据
			} else if (val instanceof String) {
				String regexStr = ".jpg|.jpeg|.gif|.png";
				Pattern pattern = Pattern.compile(regexStr);
				Matcher matcher = pattern.matcher(String.valueOf(val));
				if (matcher.find()) {
					fileForm.setFileName(String.valueOf(val));  // 获取filename文件名，带文件格式，如：.jpg /.jpeg/.gif/.png等
				} else {
					tmpMap.put(key, val);
				}
			} else if (val instanceof Integer 
					      || val instanceof Boolean || val instanceof Double
					      || val instanceof Float || val instanceof Long) {
				tmpMap.put(key, val);
			} else {
				Log.e(LOG_TAG, "未知参数格式.");
				return;
			}
		}
		if (!tmpMap.isEmpty() && tmpMap.size() > 0) {
			url.append("?");
			it = tmpMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object val = tmpMap.get(key);
				url.append(key).append("=").append(String.valueOf(val)).append("&");
			}
		}
		if (url.lastIndexOf("&") > -1) {
			String fullURL = url.substring(0, url.lastIndexOf("&"));
			Log.i(LOG_TAG, "HTTP POST Request: " + fullURL);
			fileForm.setReqUrl(fullURL);
		} else {
			Log.i(LOG_TAG, "HTTP POST Request: " + url);
			fileForm.setReqUrl(url.toString());
		}
		this.fileFormData = fileForm;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void callback(final String data) {
		Runnable getDataTask = new Runnable() {
			@Override
			public void run() {
				if (null != callback) {
					callback.onCompleted(data);
				}
			}
		};
		this.handler.post(getDataTask);
	}

	public interface ProcessReceivedDataCallback {

		void onCompleted(final String data);
	}

}
 