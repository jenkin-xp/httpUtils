package cn.edu.xp.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @Description http连接管理类
 * @Author xp
 * @Date 2021/12/6 17:45
 * @Version V1.0
 **/
public class ApacheHttpHandler implements HttpHandler {


	private static String DEFAULT_CHARSET = "UTF-8";

	/** 连接超时时间，由bean factory设置，缺省为8秒钟 */
	private static int defaultConnectionTimeout = 30000;

	/** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
	private static int defaultSoTimeout = 30000;

	/** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
	private static int defaultIdleConnTimeout = 60000;

	private static int defaultMaxConnPerHost = 30;

	private static int defaultMaxTotalConn = 80;

	/** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒 */
	private static final long defaultHttpConnectionManagerTimeout = 3 * 1000;

	/**
	 * HTTP连接管理器，该连接管理器必须是线程安全的.
	 */
	private static PoolingHttpClientConnectionManager connectionManager;

	private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static SSLContextBuilder builder = null;

	static {
		try {
            builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, (TrustStrategy) (x509Certificates, s) -> true);
            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslsf)
                    .build();
            // 创建一个线程安全的HTTP连接池
            connectionManager = new PoolingHttpClientConnectionManager(registry);
    		connectionManager.setMaxTotal(defaultMaxTotalConn);
    		connectionManager.setDefaultMaxPerRoute(defaultMaxConnPerHost);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

	/**
	 * 私有的构造方法
	 */
	ApacheHttpHandler() {
	}

	public HttpResponse post(HttpRequest request) throws Exception {
		return execute(Method.POST, request);
	}

	public HttpResponse get(HttpRequest request) throws Exception {
		return execute(Method.GET, request);
	}
	public HttpResponse stream(HttpRequest request) throws Exception {
		return execute(Method.STREAM, request);
	}
	public HttpResponse postJson(HttpRequest request) throws Exception {
		return execute(Method.JSON, request);
	}

	@Override
	public String get(String url) {
		try {
			return get(new HttpRequest(url)).getStringResult();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String get(String url, Map<String, String> params) {
		try {
			return get(new HttpRequest(url, params)).getStringResult();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String post(String url, Map<String, String> params) {
		HttpRequest r = new HttpRequest(url,params);
		try {
			HttpResponse httpResponse = post(r);
			if(httpResponse == null){
				return "";
			}
			return httpResponse.getStringResult();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private enum Method {
		POST, GET, STREAM,JSON
	}

	private HttpResponse execute(Method methodType, HttpRequest request) throws Exception {
		CloseableHttpClient httpclient = buildHttpClient();
		// 设置连接超时
		RequestConfig requestConfig = getRequestConfig(request);
		// 设置等待ConnectionManager释放connection的时间
		// 设置字符编码
		String charset = request.getCharset() != null ? request.getCharset() : DEFAULT_CHARSET;

		HttpRequestBase method = null;
		HttpResponse response = new HttpResponse();

		try {
			if (methodType == Method.POST) {
				method = new HttpPost(request.getUrl());
				if (request.getParameters() != null) {

					if (DEFAULT_CHARSET.equalsIgnoreCase(charset)) {
						StringBuilder sb = new StringBuilder();
						for (String key : request.getParameters().keySet()) {
							sb.append(key).append("=").append(request.getParameters().get(key)).append("&");
						}
						StringEntity entity = new StringEntity(sb.substring(0, sb.length() - 1));
						((HttpPost) method).setEntity(entity);
					} else {
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						Map<String, String> parameters = request.getParameters();
						Set<Entry<String, String>> entrySet = parameters.entrySet();
						for (Entry<String, String> entry : entrySet) {
							nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
						}
						((HttpPost) method).setEntity(new UrlEncodedFormEntity(nvps,charset));
					}
				}
			} else if (methodType == Method.GET) {
				if (request.getParameters() != null && request.getParameters().size() > 0) {
					StringBuilder sb = new StringBuilder(request.getUrl()).append("?");
					for (String key : request.getParameters().keySet()) {
						sb.append(key).append("=").append(request.getParameters().get(key)).append("&");
					}
					method = new HttpGet(sb.substring(0, sb.length() - 1));
				} else {
					method = new HttpGet(request.getUrl());
				}
			} else if (methodType == Method.STREAM) {
				method = new HttpPost(request.getUrl());
				byte[] data = request.getParameter().getBytes();
				ByteArrayInputStream bais = new ByteArrayInputStream(data);

				((HttpPost) method).setEntity(new InputStreamEntity(bais, data.length));
			} else if (methodType == Method.JSON) {
				method = new HttpPost(request.getUrl());
				String parameter = request.getParameter();
				StringEntity entity = new StringEntity(parameter, DEFAULT_CHARSET);
				entity.setContentType("application/json");
				((HttpPost) method).setEntity(entity);
			}

			if (methodType == Method.JSON) {
				method.addHeader("Content-Type", "application/json;charset=" + charset);
			} else {
				method.addHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);
			}

			fillHeader(request, method);
			// 设置Http Header中的User-Agent属性
			method.addHeader("User-Agent", "Mozilla/5.0"); // 谷歌浏览器 Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36
			//设置请求响应超时时间
			method.setConfig(requestConfig);

			org.apache.http.HttpResponse apacheHttpResponse = httpclient.execute(method);

			HttpEntity entity = apacheHttpResponse.getEntity();

			InputStream inputStream = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset));
			StringBuilder stringBuffer = new StringBuilder();
			String str = "";
			while ((str = br.readLine()) != null) {
				stringBuffer.append(str);
			}
			response.setStringResult(stringBuffer.toString());
			response.setResponseHeaders(method.getAllHeaders());
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(method != null){
				method.releaseConnection();
			}
		}
		return response;
	}

	public JSONObject postFile(HttpRequest request) throws Exception {
		CloseableHttpClient httpclient = buildHttpClient();

		RequestConfig requestConfig = getRequestConfig(request);

		// 设置字符编码
		String charset = request.getCharset() != null ? request.getCharset() : DEFAULT_CHARSET;

		HttpPost httpPost = new HttpPost(request.getUrl());

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (String key: request.getFileParameter().keySet()) {
			File file = request.getFileParameter().get(key);
  			builder.addBinaryBody(key, file, ContentType.create("image/jpeg"), file.getName());
		}

		HttpEntity entity = builder.build();

		httpPost.setEntity(entity);
		// 设置请求头
		fillHeader(request, httpPost);

		//设置请求响应超时时间
		httpPost.setConfig(requestConfig);
		org.apache.http.HttpResponse apacheHttpResponse = httpclient.execute(httpPost);
		HttpEntity responseEntity = apacheHttpResponse.getEntity();
		JSONObject returnJson = new JSONObject();
		if (responseEntity != null) {
			// 将响应内容转换为字符串
			String result = EntityUtils.toString(responseEntity, Charset.forName(charset));
			returnJson = JSONObject.parseObject(result);
		}
		return returnJson;
	}

	/**
	 * 填充请求头
	 * @param request
	 * @param httpRequestBase
	 */
	private void fillHeader(HttpRequest request, HttpRequestBase httpRequestBase) {
		if(request.getHeaders()!=null && !request.getHeaders().isEmpty()){
			Map<String, String> headers = request.getHeaders();
			for (Entry<String, String> stringStringEntry : headers.entrySet()) {
				String key = stringStringEntry.getKey();
				String value = stringStringEntry.getValue();
				httpRequestBase.addHeader(key,value);
			}
		}
	}

	/**
	 * 获取请求配置
	 * @param request
	 * @return
	 */
	private RequestConfig getRequestConfig(HttpRequest request) {
		// 设置连接超时
		int connectionTimeout = request.getConnectionTimeout() > 0 ? request.getConnectionTimeout() : defaultConnectionTimeout;
//		httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);

		// 设置回应超时
		int soTimeout = request.getTimeout() > 0 ? request.getTimeout() : defaultSoTimeout;
//		httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);

		return RequestConfig.custom()
				.setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(connectionTimeout)//从连接池获取请求的时间
				.setSocketTimeout(soTimeout).build();
	}

	/**
	 * 构建httpClient
	 * @return
	 */
	private CloseableHttpClient buildHttpClient() {
		return HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.setConnectionManager(connectionManager)
				.setConnectionManagerShared(true)
				.build();
	}
}
