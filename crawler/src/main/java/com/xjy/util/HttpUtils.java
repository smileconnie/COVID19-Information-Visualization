package com.xjy.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author ：xjy
 * @Desc ：封装HttpClient工具，方便爬取网页内容
 */
public abstract class HttpUtils {
    private static PoolingHttpClientConnectionManager cm = null;//声明HttpClient管理器对象（HttpClient连接池）
    private static List<String> userAgentList = null;
    private static RequestConfig config = null;

    //静态代码块会在类被加载的时候执行
    static {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);//设置最大连接数
        cm.setDefaultMaxPerRoute(20);//设置每个主机的并发数
        config = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
        userAgentList = new ArrayList<String>();
        userAgentList.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        userAgentList.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:73.0) Gecko/20100101 Firefox/73.0");
        userAgentList.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.5 Safari/605.1.15");
        userAgentList.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 Edge/16.16299");
        userAgentList.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        userAgentList.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
    }

    public static String getHtml(String url){
        //1.从连接池中获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        //2.创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);
        //3.设置请求配置对象和请求头
        httpGet.setConfig(config);
        httpGet.setHeader("User-Agent",userAgentList.get(new Random().nextInt(userAgentList.size())));
        //4.发起请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            //5.获取响应内容
            if (response.getStatusLine().getStatusCode() == 200){
                String html = EntityUtils.toString(response.getEntity(),"UTF-8");
                return html;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args){
        String html = HttpUtils.getHtml("http://www.itcast.cn");
        System.out.println(html);
    }
}
