package cn.edu.xp.http;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @ClassName: HttpClient
 * @Description: http客户端抽象接口
 * @Author: xp_sh
 * @Date: 2020/6/26 15:38
 * @Version: 1.0
 **/
public interface HttpHandler {

	HttpResponse post(HttpRequest request) throws Exception;

	HttpResponse get(HttpRequest request) throws Exception;

	HttpResponse stream(HttpRequest request) throws Exception;
	
	HttpResponse postJson(HttpRequest request) throws Exception;

	JSONObject postFile(HttpRequest request) throws Exception;

	String get(String url);

	String get(String url, Map<String,String> params);

	String post(String url, Map<String,String> params);
	
}
