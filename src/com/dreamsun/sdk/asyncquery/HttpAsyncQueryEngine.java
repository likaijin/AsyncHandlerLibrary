package com.dreamsun.sdk.asyncquery;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.dreamsun.sdk.asyncquery.HttpAsyncRequest.ProcessReceivedDataCallback;
import com.dreamsun.sdk.asyncquery.NonblockingAsyncTask.ProcessDataHolderCallback;

import android.content.Context;

/**
 * @Project: VSee
 * @Description: 统一异步任务执行API总入
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class HttpAsyncQueryEngine {

	private final static HttpAsyncQueryEngine instance = new HttpAsyncQueryEngine();

	private HttpAsyncQueryEngine() {

	}

	public static HttpAsyncQueryEngine createInstance() {
		return instance;
	}

	public void initEngineWithHomeBase(String homeBase) {
		CommonWrapper.getInstance().setHomeBaseUrl(homeBase);
	}

	public boolean isNetworkAvailable(Context context) {
		return Network.isAvailable(context);
	}

	public void executorService(Runnable task) {
		executorService(task, false);
	}

	public void executorService(Runnable task, boolean isCreated) {
		ThreadPool.getExecutor(isCreated).execute(task);
	}

	public void executorService(Runnable task, ExecutorService exec) {
		ThreadPool.getExecutor(exec).execute(task);
	}

	public void executorAsyncTask(DataHolder holder, ProcessDataHolderCallback callback) {
		executorAsyncTask(holder, callback, false);
	}

	public void executorAsyncTask(DataHolder holder, ProcessDataHolderCallback callback, boolean isCreated) {
		AsyncNonblocking an = new AsyncNonblocking(holder, isCreated, callback);
		an.execute();
	}

	public void executorAsyncTask(DataHolder holder, ProcessDataHolderCallback callback, ExecutorService exec) {
		AsyncNonblocking an = new AsyncNonblocking(holder, exec, callback);
		an.execute();
	}

	public void executeAsync(Context ctx, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, null, null, HTTPMethod.GET, callback);
	}

	public void executeAsync(Context ctx, String uri, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, uri, null, HTTPMethod.GET, callback);
	}

	public void executeAsync(Context ctx, Map<String, Object> params, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, null, params, HTTPMethod.GET, callback);
	}

	public void executeAsync(Context ctx, String uri, Map<String, Object> params, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, uri, params, HTTPMethod.GET, callback);
	}

	public void executeAsync(Context ctx, HTTPMethod method, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, null, null, method, callback);
	}

	public void executeAsync(Context ctx, String uri, HTTPMethod method, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, uri, null, method, callback);
	}

	public void executeAsync(Context ctx, Map<String, Object> params, HTTPMethod method, ProcessReceivedDataCallback callback) {
		executeAsync(ctx, null, params, method, callback);
	}

	public void executeAsync(Context ctx, String uri, Map<String, Object> params, HTTPMethod method, ProcessReceivedDataCallback callback) {
		sendRequest(ctx, uri, params, method, callback);
	}

	private void sendRequest(Context ctx, String uri, Map<String, Object> params, HTTPMethod method, ProcessReceivedDataCallback callback) {
		HttpAsyncConnection conn = new HttpAsyncConnection(uri, params, method, callback);
		conn.execute(ctx);
	}

}
