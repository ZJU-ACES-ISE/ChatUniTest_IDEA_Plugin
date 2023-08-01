package zju.cst.aces.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class ConnectUtil {
    /*测试与openapi的连接是否成功*/
    public static boolean TestOpenApiConnection(String apikey, String proxyHost, String proxyPort) {
            CloseableHttpClient httpClient;
            if (!(proxyHost.equals("") || proxyPort.equals(""))) {
                // 设置代理
                HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
                httpClient = HttpClients.custom()
                        .setProxy(proxy)
                        .build();
            } else {
                httpClient = HttpClients.custom()
                        .build();
            }
            try {
                // 构造 API 请求
                HttpGet httpGet = new HttpGet("https://api.openai.com/v1/engines");
                // 设置 API 密钥
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apikey);
                // 发送请求并获取响应
                HttpResponse response = httpClient.execute(httpGet);
                // 处理响应
                int statusCode = response.getStatusLine().getStatusCode();
                String responseContent = EntityUtils.toString(response.getEntity());
                if (statusCode == 200) {
                    return true;
                }
            } catch (IOException e) {

            } finally {
                try {
                    // 关闭 HttpClient
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
    }
}
