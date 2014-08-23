package com.dreamsun.sdk.asyncquery;

/**
 * @Project: VSee
 * @Description: 通用包装类
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class CommonWrapper {
	
	private static CommonWrapper instance = null;

	private CommonWrapper() {
	}

	public static CommonWrapper getInstance() {
		if (instance == null) {
			instance = new CommonWrapper();
		}
		return instance;
	}

	private String homeBaseUrl;

	public String getHomeBaseUrl() {
		return homeBaseUrl;
	}

	public void setHomeBaseUrl(String url) {
		this.homeBaseUrl = url;
	}

}
