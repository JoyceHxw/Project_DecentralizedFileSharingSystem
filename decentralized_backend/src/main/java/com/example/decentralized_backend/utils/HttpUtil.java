package com.example.decentralized_backend.utils;

import com.example.decentralized_backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HttpUtil {

    private static final int TIME_OUT = 120 * 1000;

    private static CloseableHttpClient httpClient = null;

    /**
     * HttpGet请求
     *
     * @param uriBuilder
     * @return
     * @throws URISyntaxException
     */
    public static String httpGet(URIBuilder uriBuilder, Map<String, String> header) throws URISyntaxException {
        HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.addHeader("Accept", "application/json");
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        config(httpGet);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = getHttpClient(uriBuilder.build().toString()).execute(httpGet, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        } catch (IOException e) {
            log.error("httpGet请求异常:[{ }]", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("httpGet请求response关闭异常:[{ }]", e);
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
            }
        }
        return result;
    }

    public static void httpGetFile(URIBuilder uriBuilder, Map<String, String> header, String hostname, String path) throws URISyntaxException {
        HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.addHeader("Accept", "application/json");
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        config(httpGet);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(uriBuilder.build().toString()).execute(httpGet, HttpClientContext.create());
            //检查响应状态码是否为重定向
            if(response.getStatusLine().getStatusCode()==308){
                // 获取重定向地址
                String redirectUrl = "http://"+hostname+":1633"+response.getFirstHeader("Location").getValue();
                // 获取文件名
                // 重新执行GET请求
                httpGet = new HttpGet(redirectUrl);
                response = httpClient.execute(httpGet);
                String contentDispositionHeader=null;
                for (Header responseHeader : response.getHeaders("Content-Disposition")) {
                    contentDispositionHeader=responseHeader.getValue();
                }
                // 使用正则表达式来提取filename参数的值
                Pattern pattern = Pattern.compile("filename=\"([^\"]+)\"");
                Matcher matcher = pattern.matcher(contentDispositionHeader);
                // 如果匹配成功，则提取出filename参数的值
                String filename=null;
                if (matcher.find()) {
                    filename = path+matcher.group(1);
                } else {
                    throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"没有文件名");
                }
                // 获取响应实体内容的输入流
                InputStream inputStream = response.getEntity().getContent();
                // 创建文件输出流，以获取的文件名保存文件
                FileOutputStream outputStream = new FileOutputStream(filename);
                // 将响应实体内容写入文件输出流
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                // 关闭输入输出流
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            log.error("httpGet请求异常:[{ }]", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("httpGet请求response关闭异常:[{ }]", e);
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
            }
        }
    }

    /**
     * HttpPost请求
     *
     * @param url 请求地址
     * @param he 请求参数
     * @return
     */
    public static String httpPost(String url, HttpEntity he, Map<String, String> header) {
        HttpPost httpPost = new HttpPost(url);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        config(httpPost);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            if(he!=null){
//                StringEntity se = new StringEntity(params,
//                        "utf-8");
//                se.setContentType("text/json");
//                se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                httpPost.setEntity(he);
            }
            response = getHttpClient(url).execute(httpPost,
                    HttpClientContext.create());
            log.info("response:{}", response);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity,
                    "utf-8");
            log.info("result:{}", result);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            log.error("httpPost请求异常：[{}]", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        } finally {
            try {
                if
                (response != null) {
                    response.close();
                }
            } catch
            (IOException e) {
                log.error("httpPost请求response关闭异常：[(}]", e);
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
            }
        }
        return result;
    }

    public static String httpPatch(String url, String params, Map<String, String> header) {
        HttpPatch httpPatch=new HttpPatch(url);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPatch.addHeader(entry.getKey(), entry.getValue());
            }
        }
        config(httpPatch);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpPatch.addHeader("Content-Type", "application/json");
            if(params!=null){
                StringEntity se = new StringEntity(params,
                        "utf-8");
                se.setContentType("text/json");
                se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                httpPatch.setEntity(se);
            }
            response = getHttpClient(url).execute(httpPatch,
                    HttpClientContext.create());
            log.info("response:{}", response);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity,
                    "utf-8");
            log.info("result:{}", result);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            log.error("httpPatch请求异常：[{}]", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        } finally {
            try {
                if
                (response != null) {
                    response.close();
                }
            } catch
            (IOException e) {
                log.error("httpPatch请求response关闭异常：[(}]", e);
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
            }
        }
        return result;
    }

    private static synchronized CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            httpClient = createHttpClient(hostname, port);
        }
        return httpClient;
    }

    private static CloseableHttpClient createHttpClient(String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        // 设置为了信任所有SSL证书
        SSLConnectionSocketFactory sslsf = createSSLContext();
        // LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        //将最大连接数增加
        cm.setMaxTotal(200);
        //将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(40);
        HttpHost httpHost = new HttpHost(hostname, port);
        //将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost),
                100);
        //请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                //如果已经重试了5次，就放弃
                if (executionCount >= 5) {
                    return false;
                }
                //如果服务器丢掉了连接，那么就重试
                if
                (exception instanceof NoHttpResponseException) {
                    return true;
                }
                //不要重试SSL握手异常
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                //超时
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                //目标服务器不可达
                if
                (exception instanceof UnknownHostException) {
                    return false;
                }
                //SSL握手异常
                if (exception instanceof SSLException) {
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                //如果请求是幂等的，就再次尝试
                return (request instanceof HttpEntityEnclosingRequest);
            }
        };

        return HttpClients.custom().setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();
    }

    private static SSLConnectionSocketFactory createSSLContext() {
        SSLContext sslContext = null;
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                // 忽略证书校验
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            log.error("不存在该算法： {}", e.getMessage());
        } catch (KeyManagementException e) {
            log.error("密钥异常： {}", e.getMessage());
        } catch (KeyStoreException e) {
            log.error("密钥存储异常： {}", e.getMessage());
        }
        return sslConnectionSocketFactory;
    }

    /**
     * 配置相关信息
     *
     * @param base
     */
    private static void config(HttpRequestBase base) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIME_OUT)
                .setConnectTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT)
                .build();
        base.setConfig(requestConfig);
    }

}
