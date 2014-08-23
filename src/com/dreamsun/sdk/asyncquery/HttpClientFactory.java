package com.dreamsun.sdk.asyncquery;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * @Project: VSee
 * @Description: HTTP Connection池化工厂类
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class HttpClientFactory {

	// 连接池
	private final ClientConnectionManager clientConnectionManager;
	// Http-Client
	private final HttpClient httpClient;

	// 读取超时
	private static int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
	// 连接超时
	private static int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
	// 连接池的最大连接数
	private static int DEFAULT_MAX_CONN = 10;
	// Socket缓冲池大小
	private static int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	// 默认字符集
	private static String DEFAULT_CHARSET = "UTF-8";
	// 默认User Agent设置
	private static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1707.0 Safari/537.36";
	// http请求参数包装
	private static HttpParams httpParams;
	// http请求端口
	private static int httpPort = 80;
	// https请求端口
	private static int httpsPort = 443;

	public static void setDEFAULT_SOCKET_TIMEOUT(int DEFAULT_SOCKET_TIMEOUT) {
		HttpClientFactory.DEFAULT_SOCKET_TIMEOUT = DEFAULT_SOCKET_TIMEOUT;
	}

	public static void setDEFAULT_CONNECTION_TIMEOUT(int DEFAULT_CONNECTION_TIMEOUT) {
		HttpClientFactory.DEFAULT_CONNECTION_TIMEOUT = DEFAULT_CONNECTION_TIMEOUT;
	}

	public static void setDEFAULT_MAX_CONN(int DEFAULT_MAX_CONN) {
		HttpClientFactory.DEFAULT_MAX_CONN = DEFAULT_MAX_CONN;
	}

	public static void setDEFAULT_SOCKET_BUFFER_SIZE(int DEFAULT_SOCKET_BUFFER_SIZE) {
		HttpClientFactory.DEFAULT_SOCKET_BUFFER_SIZE = DEFAULT_SOCKET_BUFFER_SIZE;
	}

	public static void setDEFAULT_CHARSET(String DEFAULT_CHARSET) {
		HttpClientFactory.DEFAULT_CHARSET = DEFAULT_CHARSET;
	}

	public static void setDEFAULT_USER_AGENT(String DEFAULT_USER_AGENT) {
		HttpClientFactory.DEFAULT_USER_AGENT = DEFAULT_USER_AGENT;
	}

	public static void setHttpParams(HttpParams httpParams) {
		HttpClientFactory.httpParams = httpParams;
	}

	public static void setHttpPort(int httpPort) {
		HttpClientFactory.httpPort = httpPort;
	}

	public static void setHttpsPort(int httpsPort) {
		HttpClientFactory.httpsPort = httpsPort;
	}

	private final static HttpClientFactory HTTP_CLIENT_FACTORY = new HttpClientFactory();

	private HttpClientFactory() {
		httpParams = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParams, DEFAULT_CHARSET);
		HttpProtocolParams.setHttpElementCharset(httpParams, DEFAULT_CHARSET);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, DEFAULT_USER_AGENT);

		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONN);
		ConnManagerParams.setTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_CONN));

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), httpsPort));

		// 连接池仅实例化一次
		clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

		// HttpClient对象仅实例化一次
		httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
	}

	public static HttpClient getHttpClient() {
		return HTTP_CLIENT_FACTORY.httpClient;
	}

}
