package com.dreamsun.sdk.asyncquery;

import java.util.concurrent.ExecutorService;

import com.dreamsun.sdk.asyncquery.NonblockingAsyncTask.ProcessDataHolderCallback;

/**
 * @Project: VSee
 * @Description: 异步非阻塞调用类
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class AsyncNonblocking {
	
	private ExecutorService exec;
	
	private DataHolder holder;
	
	private ProcessDataHolderCallback callback;
	
	private boolean isCreated = false;
	
	public AsyncNonblocking(DataHolder holder, boolean isCreated, ProcessDataHolderCallback callback) {
		this.holder = holder;
		this.isCreated = isCreated;
		this.callback = callback;
	}
	
	public AsyncNonblocking(DataHolder holder, ExecutorService exec, ProcessDataHolderCallback callback) {
		this.holder = holder;
		this.exec = exec;
		this.callback = callback;
	}
	
	public void execute() {
		NonblockingAsyncTask nat = new NonblockingAsyncTask(this.holder, this.callback);
		if (null == this.exec) {
			nat.executeInBackground(ThreadPool.getExecutor(this.isCreated));
		} else {
			nat.executeInBackground(ThreadPool.getExecutor(this.exec));
		}
	}

}
