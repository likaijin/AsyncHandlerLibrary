package com.dreamsun.sdk.asyncquery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * @Project: VSee
 * @Description: 异步非阻塞，对AsyncTask进行扩展包装
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class NonblockingAsyncTask extends AsyncTask<Void, Void, Void> {
	
	private static final String LOG_TAG = NonblockingAsyncTask.class.getSimpleName();

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
	
    private DataHolder holder;
	
	private ProcessDataHolderCallback callback;
	
	public NonblockingAsyncTask(DataHolder holder, ProcessDataHolderCallback callback) {
		this.holder = holder;
		this.callback = callback;
	}

	public NonblockingAsyncTask executeInBackground(Executor executor) {
		if (null != executeOnExecutorMethod) {
			try {
				Object obj = executeOnExecutorMethod.invoke(this, executor, null);
				if (null != obj) {
					return this;
				}
			} catch (IllegalArgumentException e) {
				Log.e(LOG_TAG, "抛未知参数名异常: ", e);
			} catch (IllegalAccessException e) {
				Log.e(LOG_TAG, "抛未知方法名异常: ", e);
			} catch (InvocationTargetException e) {
				Log.e(LOG_TAG, "抛未知目标异:", e);
			}
		}
		this.execute();
		return this;
	}
	
	private Handler handler;
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.handler = new Handler();
	}

	@Override
	protected Void doInBackground(Void... params) {
		Object result = this.holder.invoke();
		this.callback(result);
		return null;
	}
	
    private void callback(final Object data) {
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

    public interface ProcessDataHolderCallback {
		
    	void onCompleted(final Object data);
	}

}
