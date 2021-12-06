package cn.edu.xp.http;

import org.apache.http.Header;

/**
 * @ClassName: HttpResponse
 * @Description: http请求返回封装类
 * @Author: xp_sh
 * @Date: 2020/6/26 15:38
 * @Version: 1.0
 **/
public class HttpResponse {

	/**
	 * 返回中的http状态信息
	 */
	private String httpState;

	/**
	 * 返回中的Header信息
	 */
	private Header[] responseHeaders;

	/**
	 * String类型的result
	 */
	private String stringResult;

	/**
	 * btye类型的result
	 */
	private byte[] byteResult;

	public byte[] getByteResult() {
		if (byteResult != null) {
			return byteResult;
		}
		return null;
	}

	public String getStringResult() {
		if (stringResult != null) {
			return stringResult;
		}
		return null;
	}

	public String getHttpState() {
		return httpState;
	}

	public void setHttpState(String httpState) {
		this.httpState = httpState;
	}

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}

	public void setByteResult(byte[] byteResult) {
		this.byteResult = byteResult;
	}
	
	

}
