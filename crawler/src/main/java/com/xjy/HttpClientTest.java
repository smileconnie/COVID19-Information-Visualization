package com.xjy;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ：xjy
 * @Desc ：TODO
 */

public class HttpClientTest {
    /**
     * 测试HttpClient发送Get请求
     */
    @Test
    public void testGet() throws Exception {
        //0.创建配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)//设置连接的超时时间
                .setConnectTimeout(10000)//设置创建连接的超时时间
                .setConnectionRequestTimeout(10000)//设置请求超时时间
                //.setProxy(new HttpHost("123.207.57.145",  1080, null))//添加代理
                .build();

        //1.创建HttpClient对象
        //HttpClient httpClient = new DefaultHttpClient();//不用
        //CloseableHttpClient httpClient = HttpClients.createDefault();//简单API
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();//常用

        //2.创建HttpGet请求
        String uri = "http://yun.itheima.com/search?so=java";
        HttpGet httpGet = new HttpGet(uri);
        //或者单独给httpGet设置
        //httpGet.setConfig(requestConfig);

        //3.设置请求头
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");

        CloseableHttpResponse response = null;
        try {
            //4.使用HttpClient发起请求
            response = httpClient.execute(httpGet);
            //5.判断响应状态码是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                //6.获取响应数据
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6.关闭资源
            response.close();
            httpClient.close();
        }
    }

    /**
     * 测试HttpClient发送Post请求
     */
    @Test
    public void testPost() throws Exception {
        //1.创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //2.创建HttpPost请求
        HttpPost httpPost = new HttpPost("http://yun.itheima.com/search");
        //3.准备参数
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("so", "java"));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
        //4.设置参数
        httpPost.setEntity(formEntity);
        //5.发起请求
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                //6.获取响应
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //7.关闭资源
            response.close();
            httpClient.close();
        }
    }

    /**
     * 测试HttpClient连接池
     */
    @Test
    public void testPool() throws Exception {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        cm.setMaxTotal(200);
        //设置每个主机的并发数
        cm.setDefaultMaxPerRoute(20);
        doGet(cm);
        doGet(cm);
    }

    private static void doGet(PoolingHttpClientConnectionManager cm) throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        //在下一行加断点
        HttpGet httpGet = new HttpGet("http://www.itcast.cn/");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放连接
            response.close();
            //不能关闭HttpClient
            //httpClient.close();
        }
    }

    /**
     * 测试HttpClient超时设置-添加代理
     */
    @Test
    public void testConfig() throws Exception{
        //0.创建请求配置对象
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)//设置连接超时时间
                .setConnectTimeout(10000)//设置创建连接超时时间
                .setConnectionRequestTimeout(10000)//设置请求超时时间
                .setProxy(new HttpHost("61.133.87.228",55443))//添加代理（免费代理服务器源自https://proxy.mimvp.com/freeopen）
                .build();
        //1.创建HttpClient对象
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        //2.创建HttpGet对象
        HttpGet httpGet = new HttpGet("http://www.itcast.cn/");
        //3.发起请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //4.获取响应数据
        if (response.getStatusLine().getStatusCode() == 200){
            String html = EntityUtils.toString(response.getEntity(),"UTF-8");
            System.out.println(html);
        }
        //5.关闭资源
        response.close();
        httpClient.close();
    }
}
