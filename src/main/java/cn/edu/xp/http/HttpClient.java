package cn.edu.xp.http;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Description 功能概述
 * @Author xp
 * @Date 2021/12/6 16:26
 * @Version V1.0
 **/
public class HttpClient {
    private static final HttpHandler httpClient = new ApacheHttpHandler();

    public static HttpResponse post(HttpRequest request) throws Exception {
        return httpClient.post(request);
    }

    public static HttpResponse get(HttpRequest request) throws Exception {
        return httpClient.get(request);
    }

    public static HttpResponse stream(HttpRequest request) throws Exception {
        return httpClient.stream(request);
    }

    public static HttpResponse postJson(HttpRequest request) throws Exception {
        return httpClient.postJson(request);
    }

    public static JSONObject postFile(HttpRequest request) throws Exception {
        return httpClient.postFile(request);
    }

    public static String get(String url){
        return httpClient.get(url);
    }

    public static String get(String url, Map<String,String> params){
        return httpClient.get(url,params);
    }

    public static String post(String url, Map<String,String> params){
        return httpClient.post(url,params);
    }

}
