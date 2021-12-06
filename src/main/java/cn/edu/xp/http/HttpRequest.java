package cn.edu.xp.http;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: HttpRequest
 * @Description: http请求参数封装类
 * @Author: xp_sh
 * @Date: 2020/6/26 15:38
 * @Version: 1.0
 **/
public class HttpRequest {

	/**
	 * 待请求的url
	 */
	private String url = null;

	private int timeout = 0;

	private int connectionTimeout = 0;

	/**
	 * Post方式请求时组装好的参数值对
	 */
	private Map<String, String> parameters = null;
	private Map<String,String>headers = null;
	private String parameter;
	private Map<String, File> fileParameter;

	/**
	 * 默认的请求编码方式
	 */
	private String charset = "UTF-8";

	/**
	 * 请求返回的方式
	 */
	public HttpRequest(String url) {
		this.url = url;
	}

	public HttpRequest(String url, int timeout) {
		this.url = url;
		this.connectionTimeout = timeout;
	}
	
	public HttpRequest(String url, int timeout, String charset) {
		this(url, timeout);
		this.charset = charset;
	}

	public HttpRequest(String url, Map<String, String> params) {
		this(url);
		setParameters(params);
	}

	public HttpRequest(String url, Map<String, String> params, Map<String, File> fileParameter) {
		this(url);
		setParameters(params);
		setFileParameter(fileParameter);
	}
	
	public HttpRequest(String url, String params) {
		this(url);
		setParameter(params);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return Returns the charset.
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset
	 *            The charset to set.
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public Map<String, File> getFileParameter() {
		return fileParameter;
	}

	public void setFileParameter(Map<String, File> fileParameter) {
		this.fileParameter = fileParameter;
	}
}
