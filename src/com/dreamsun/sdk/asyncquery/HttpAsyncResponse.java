package com.dreamsun.sdk.asyncquery;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * @Project: VSee
 * @Description: 异步非阻塞Response
 * @Copyright: Copyright (c) Dec 18, 2013 11:36:06 AM
 * @Company: MDC.Co.,Ltd.
 * @author LiKaijin(kjli@mdc.cn)
 * @version 1.0
 */
public class HttpAsyncResponse {
	
	private static final String LOG_TAG = HttpAsyncResponse.class.getSimpleName();
	
	private HttpAsyncRequest request;
	
	public HttpAsyncResponse(HttpAsyncRequest request) {
		this.request = request;
	}
	
    public String handle() {
		// 针对SocketTimeoutException、ConnectTimeoutException、UnknownHostException三种可处理异常做3次容灾，其他异常直接丢弃不做任何处理
		for (int i = 0; i < 3; i++) {
			try {
				return httpRequest();
			} catch (SocketTimeoutException se) {
				Log.e(LOG_TAG, "Socket连接超时发生异常: " + se);
			} catch (ConnectTimeoutException ce) {
				Log.e(LOG_TAG, "Connect连接超时发生异常:" + ce);
			} catch (UnknownHostException ue) {
				Log.e(LOG_TAG, "请求的主机地址无效发生异常：" + ue);
			}
			Log.e(LOG_TAG, "Retrying request: " + i);
		}
		return null;
    }

    private String httpRequest() throws SocketTimeoutException, ConnectTimeoutException, UnknownHostException { 
    	if (HTTPMethod.OCTET_STREAM == this.request.getMethod()) {  // 文件流处理
    		HttpURLConnection conn = null;
    		DataOutputStream dos = null;
    		String BOUNDARY = UUID.randomUUID().toString(); 
    		String PREFIX = "--";
    		String SUFFIX = "\r\n";
    		try {
	        	  conn = (HttpURLConnection) (new URL(this.request.getFileFormData().getReqUrl())).openConnection();
	    		  conn.setReadTimeout(30 * 1000);
	    		  conn.setConnectTimeout(30 * 1000);
	    		  conn.setDoInput(true);
	    		  conn.setDoOutput(true);
	    		  conn.setUseCaches(false);
	    		  conn.setRequestMethod("POST");
	    		  conn.setRequestProperty("Connection", "Keep-Alive");
	    		  conn.setRequestProperty("Charset", "UTF-8");
	    		  conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
	    		  dos = new DataOutputStream(conn.getOutputStream());
	    		  StringBuffer sb = new StringBuffer();
	    		  sb.append(PREFIX).append(BOUNDARY).append(SUFFIX)
	    		  .append("Content-Disposition: form-data; name=\"file\"; filename=\"" + this.request.getFileFormData().getFileName() + "\"" + SUFFIX)
	    		  .append("Content-Type: application/octet-stream; charset=UTF-8" + SUFFIX).append(SUFFIX);
	    		  dos.write(sb.toString().getBytes());
	    		  dos.write(this.request.getFileFormData().getData());
	    		  dos.write(SUFFIX.getBytes());
	    		  byte[] end = (PREFIX + BOUNDARY + PREFIX + SUFFIX).getBytes();
	    		  dos.write(end);
	    		  dos.flush();
	    		  InputStream is = conn.getInputStream();
	    		  return getContentByInputStream(is, "UTF-8");
    		} catch (Exception e) {
    			Log.e(LOG_TAG, "文件上传失败:", e);
    		} finally {
    			if (null != dos) {
    				try {
						dos.close();
					} catch (IOException e) {
						Log.e(LOG_TAG, "关闭流操作发生I/O异常:", e);
					}
    				dos = null;
    			}
    			if (null != conn) {
    				conn.disconnect();
    				conn = null;
    			}
    		}
    	} else {
        	HttpEntity result = null;
        	HttpResponse httpResponse = null;
        	try {
        		int count = 0;
        		httpResponse = HttpClientFactory.getHttpClient().execute(this.request.getRequest());
        		int statusCode = httpResponse.getStatusLine().getStatusCode();
    			 // 容灾
    			 while(true) {
    		    	 if (statusCode == HttpStatus.SC_OK || statusCode >= HttpStatus.SC_BAD_REQUEST) {
    		    		 break;
    		    	 } else {
    		    		 httpResponse = HttpClientFactory.getHttpClient().execute(this.request.getRequest());
    		    		 statusCode = httpResponse.getStatusLine().getStatusCode();
    		    		 count++;
    		    		 if (count > 2) {
    		    			 Log.e(LOG_TAG, "尝试修复3次均失败.");
    		    			 break;
    		    		 }
    		    	 }
    		     }
    			 Log.d(LOG_TAG, "Http-Status:" + statusCode);
    			 if (statusCode != HttpStatus.SC_OK) {
    				 Log.e(LOG_TAG, "访问远程链接失败.");
    		    	 return null;
    		     }
    			 result = httpResponse.getEntity();
    			 if (null == result) {
    				 Log.e(LOG_TAG, "未请求到目标数据.");
    				 return null;
    			 }
    			 long length = httpResponse.getEntity().getContentLength();
    			 Log.d(LOG_TAG, "Content-Length:" + length);
    			 if (length > -1) {
    				 if (length > 1000000) {
    					 Log.e(LOG_TAG, "爬取到的目标数据字节数超过1MB阀值:" + length);
    					 return null;
    				 }
    			 }
    			 return EntityUtils.toString(result, HTTP.UTF_8);
        	} catch (SocketTimeoutException se) {
    			throw se;
    		} catch (ConnectTimeoutException ce) {
    			throw ce;
    		} catch (UnknownHostException ue) { 
    			throw ue;
    		} catch (IOException ioe) {
    			if (ioe instanceof ConnectTimeoutException) {
    				throw new ConnectTimeoutException("读取服务器数据发生I/O异常: " + ioe);
    			}
    			Log.e(LOG_TAG, "读取服务器数据发生I/O异常:", ioe);
    		} catch (Exception e) {
    			Log.e(LOG_TAG, "HTTP请求发生未知异常:", e);
    		} finally {
    			if (null != result) {
    				if (result.isStreaming()) {
    					try {
    						InputStream is = result.getContent();
    						is.close();
    						is = null;
    						result = null;
    					} catch (IllegalStateException e) {
    						Log.e(LOG_TAG, "关闭Stream流操作出现无效状态异常:", e);
    					} catch (IOException e) {
    						Log.e(LOG_TAG, "关闭Stream流操作出现I/O异常:", e);
    					}
    				}
    			}
    		}
    	}
    	return null;
    }  
    
	private static String getContentByInputStream(InputStream is, String charset) {	
		BufferedInputStream buffer = null;
		InputStreamReader reader = null;
//		BufferedReader br = null;
		try {
    		 buffer = new BufferedInputStream(is);
    		 reader = new InputStreamReader(buffer, charset);  
//    		 br = new BufferedReader(reader, 5 * 1024);  // 设置读取文件内容缓存为5KB
    		 StringBuffer content = new StringBuffer();  
    		 char[] ch = new char[8192];  // 缓存8KB
    		 int totalCount = 0;
    		 int readCount = 0;  
    		 while ((readCount = reader.read(ch)) != -1) {  
    			 totalCount += readCount;
    			 if (totalCount > 1000000) {
    				 Log.e(LOG_TAG, "爬取到的目标数据字节数超过1MB阀值:" + totalCount);
    				 return null;
    			 }
    			 content.append(ch, 0, readCount); 
    	     }
    		 return content.toString();
        } catch (IOException e) {
			 e.printStackTrace();
        } catch (Exception e) {
			 e.printStackTrace();
        } finally {
        	if (null != is) {
        		try {
					is.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "关闭InputStream流操作出现I/O异常:", e);
				}
        		is = null;
        	}
        	if (null != buffer) {
        		try {
					buffer.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "关闭BufferedInputStream流操作出现I/O异常:", e);
				}
        		buffer = null;
        	}
        	if (null != reader) {
        		try {
					reader.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "关闭InputStreamReader流操作出现I/O异常:", e);
				}
        		reader = null;
        	}
        }
		return null;
	}

}
