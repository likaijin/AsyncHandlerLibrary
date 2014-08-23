package com.dreamsun.sdk.asyncquery;

import java.util.Map;

import com.dreamsun.sdk.asyncquery.HttpAsyncRequest.ProcessReceivedDataCallback;

import android.content.Context;

/**
 * @Project: VSee
 * @Description: 异步非阻塞Connection
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class HttpAsyncConnection {

	private HttpAsyncRequest request;

	public HttpAsyncConnection(String uri, Map<String, Object> params, HTTPMethod method, ProcessReceivedDataCallback callback) {
		this.request = new HttpAsyncRequest(uri, params, method, callback);
	}

	public void execute(Context ctx) {
		HttpConnAsyncTask conn = new HttpConnAsyncTask(ctx, this.request);
		conn.executeInBackground();
	}

}
