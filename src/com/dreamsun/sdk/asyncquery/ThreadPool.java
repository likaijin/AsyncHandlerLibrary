package com.dreamsun.sdk.asyncquery;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @Project: VSee
 * @Description: Thread Pool池化工具
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class ThreadPool {
	
	private static final String LOG_TAG = ThreadPool.class.getSimpleName();
	
	private static final Lock myLock = new ReentrantLock();
	
	private static volatile Executor executor;
	
	private static final int CORE_POOL_SIZE = 5;
	 
	private static final int MAXIMUM_POOL_SIZE = 128;
	 
	private static final int KEEP_ALIVE = 1;

	private static final BlockingQueue<Runnable> threadWorkQueue = new LinkedBlockingQueue<Runnable>(10);
	 
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);
        public Thread newThread(Runnable run) {
            return new Thread(run, "ThreadPool #" + count.getAndIncrement());
        }
    };
     
    private static final Executor NEW_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, threadWorkQueue, threadFactory);
     
	public static Executor getExecutor() {
		myLock.lock();
		try {
			if (null == ThreadPool.executor) {
				 Executor defaultExecutor = getDefaultThreadPoolExecutor();
				if (null == defaultExecutor) {
					defaultExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, threadWorkQueue, threadFactory);
				}
				ThreadPool.executor = defaultExecutor;
			}
		} finally {
			myLock.unlock();
		}
		return ThreadPool.executor;
	}
	
	public static Executor getExecutor(ExecutorService exec) {
		if (null == exec) {
			return getExecutor();
		}
		return exec;
	}
	
	public static Executor getExecutor(boolean isCreated) {
		if (!isCreated) {
			return getExecutor();
		}
		return NEW_THREAD_POOL_EXECUTOR;
	}
	
	private static Executor getDefaultThreadPoolExecutor() {
		try {
			Field threadPoolExecutor = AsyncTask.class.getDeclaredField("THREAD_POOL_EXECUTOR");
			if (null != threadPoolExecutor) {
				threadPoolExecutor.setAccessible(true);
				Object executor = threadPoolExecutor.get(null);
				if (null != executor && executor instanceof Executor) {
					return (Executor) executor;
				}
			}
		} catch (NoSuchFieldException e) {
			Log.e(LOG_TAG, "抛未知字段名异常: ", e);
		} catch (IllegalArgumentException e) {
			Log.e(LOG_TAG, "抛未知参数名异常: ", e);
		} catch (IllegalAccessException e) {
			Log.e(LOG_TAG, "抛未知方法名异常: ", e);
		}
		return null;
	}
	
}
