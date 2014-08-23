package com.dreamsun.sdk.asyncquery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

/**
 * @Project: VSee
 * @Description: 异步非阻塞，对AsyncTask进行扩展包装
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class HttpConnAsyncTask extends AsyncTask<Void, Void, Void> {
	
	private static final String LOG_TAG = HttpConnAsyncTask.class.getSimpleName();
	
	private HttpAsyncRequest request;
	
	private HttpAsyncResponse response;
	
	private Context ctx;
	
	private static Method executeOnExecutorMethod;
	
	static {
		 for (Method method : AsyncTask.class.getDeclaredMethods()) {
			 if ("executeOnExecutor".equals(method.getName())) {
				 Class<?>[] params = method.getParameterTypes();
				 if (null != params && params.length == 2 && params[0] == Executor.class && params[1].isArray()) {
					 executeOnExecutorMethod = method;
					 break;
				 }
			 }
		 }
	}

	public HttpConnAsyncTask(Context ctx, HttpAsyncRequest request) {
		this.request = request;
		this.ctx = ctx;
	}
	
	public HttpConnAsyncTask executeInBackground() {
		if (null != executeOnExecutorMethod) {
			try {
				Object obj = executeOnExecutorMethod.invoke(this, ThreadPool.getExecutor(), null);
				if (null != obj) {
					return this;
				}
			} catch (IllegalArgumentException e) {
				Log.e(LOG_TAG, "抛未知参数名异常: ", e);
			} catch (IllegalAccessException e) {
				Log.e(LOG_TAG, "抛未知方法名异常: ", e);
			} catch (InvocationTargetException e) {
				Log.e(LOG_TAG, "抛未知目标异常: ", e);
			}
		}
		this.execute();
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.request.setHandler(new Handler());
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (!Network.isAvailable(this.ctx)) {
			this.request.callback(null);
			Log.e(LOG_TAG, "未知网络或网络不可用.");
			return null;
		}
		this.request.serializeData();
		this.response = new HttpAsyncResponse(this.request);
		String result = this.response.handle();
		if (!TextUtils.isEmpty(result)) {
			this.request.callback(result);
		} else {
			this.request.callback(null);
		}
		return null;
	}

}
