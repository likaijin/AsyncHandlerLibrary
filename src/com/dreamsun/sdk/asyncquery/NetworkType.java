package com.dreamsun.sdk.asyncquery;

/**
 * @Project: VSee
 * @Description: 网络类型枚举
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public enum NetworkType {
	
	_INVALID(-1, "--"), // 无网络
	_WIFI(0, "WIFI"), // WIFI网络
	_WAP(1, "WAP"), // WAP网络
	_2G(2, "2G"), // 2G网络
	_3G(3, "3G"), // 3G网络
	_MOBILE(4, "MOBILE"); // 高速网络
	
	private int index;
	
	private String name;

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	private NetworkType(int index, String name) {
		this.index = index;
		this.name = name;
	}
}


