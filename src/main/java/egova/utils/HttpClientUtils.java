package egova.utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *  http服务请求工具
 *
 */
@SuppressWarnings("deprecation")
public class HttpClientUtils {

    public static final int connTimeout=10000;
    public static final int readTimeout=10000;
    public static final String charset="UTF-8";
    private static HttpClient client = null;
    
    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    	// client = HttpClients.createDefault();
    }
    
    public static String postParameters(String url, String parameterStr) throws Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }
    
    public static String postParameters(String url, String parameterStr,String charset, Integer connTimeout, Integer readTimeout) throws Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }
    
    public static String postParameters(String url, Map<String, String> params) throws Exception {
         return post(url, params, null, connTimeout, readTimeout);
     }
    
    public static String postParameters(String url, Map<String, String> params, Integer connTimeout,Integer readTimeout) throws
            Exception {
         return post(url, params, null, connTimeout, readTimeout);
     }
      
    public static String get(String url) throws Exception {  
        return get(url, charset, null, null);  
    }
    
    public static String get(String url, String charset) throws Exception {  
        return get(url, charset, connTimeout, readTimeout);  
    } 

    /** 
     * 发送一个 Post 请求, 使用指定的字符集编码. 
     *  
     * @param url 
     * @param body RequestBody 
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @param charset 编码 
     * @param connTimeout 建立链接超时时间,毫秒. 
     * @param readTimeout 响应超时时间,毫秒. 
     * @return ResponseBody, 使用指定的字符集编码. 
     * @throws ConnectTimeoutException 建立链接超时异常 
     * @throws SocketTimeoutException  响应超时 
     * @throws Exception 
     */  
    public static String post(String url, String body, String mimeType,String charset, Integer connTimeout, Integer readTimeout) 
            throws Exception {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotEmpty(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null&& client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }  
    
    
    
    /** 
     * 提交form表单 
     *  
     * @param url 
     * @param
     * @param connTimeout 
     * @param readTimeout 
     * @return 
     * @throws ConnectTimeoutException 
     * @throws SocketTimeoutException 
     * @throws Exception 
     */  
    public static String post(String url, HttpEntity entity, Map<String, String> headers, Integer connTimeout,Integer readTimeout) throws
            Exception {
  
        HttpClient client = null;  
        HttpPost post = new HttpPost(url);  
        try {  
            if (entity != null){  
                post.setEntity(entity);  
            }
            
            if (headers != null && !headers.isEmpty()) {  
                for (Entry<String, String> entry : headers.entrySet()) {  
                    post.addHeader(entry.getKey(), entry.getValue());  
                }  
            }  
            // 设置参数  
            Builder customReqConf = RequestConfig.custom();  
            if (connTimeout != null) {  
                customReqConf.setConnectTimeout(connTimeout);  
            }  
            if (readTimeout != null) {  
                customReqConf.setSocketTimeout(readTimeout);  
            }  
            post.setConfig(customReqConf.build());  
            HttpResponse res = null;  
            if (url.startsWith("https")) {  
                // 执行 Https 请求.  
                client = createSSLInsecureClient();  
              //  HttpHost host = new HttpHost(post.getURI().getHost(), 443, "https");
                res = client.execute(post);  
            } else {  
                // 执行 Http 请求.  
                client = HttpClientUtils.client;  
                res = client.execute(post);  
            }  
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {  
            post.releaseConnection();  
            if (url.startsWith("https") && client != null  
                    && client instanceof CloseableHttpClient) {  
                ((CloseableHttpClient) client).close();  
            }  
        }  
    } 
    
    /** 
     * 提交form表单 
     *  
     * @param url 
     * @param params 
     * @param connTimeout 
     * @param readTimeout 
     * @return 
     * @throws ConnectTimeoutException 
     * @throws SocketTimeoutException 
     * @throws Exception 
     */  
    public static String post(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout,Integer readTimeout) throws
            Exception {
  
    	 UrlEncodedFormEntity entity = null;
   
            if (params != null && !params.isEmpty()) {  
                List<NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();  
                Set<Entry<String, String>> entrySet = params.entrySet();  
                for (Entry<String, String> entry : entrySet) {  
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
                }  
                  entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);  
       
            }
            return post(url,entity,headers,connTimeout,readTimeout);
             
    } 
    
    
    
    
    /** 
     * 提交form表单 
     *  
     * @param url 
     * @param
     * @param connTimeout 
     * @param readTimeout 
     * @return 
     * @throws ConnectTimeoutException 
     * @throws SocketTimeoutException 
     * @throws Exception 
     */  
	public static String post(String url, String body, Map<String, String> headers, Integer connTimeout,
			Integer readTimeout) throws Exception {

		StringEntity entity = null;

		if (StringUtils.isNotBlank(body)) {
			entity = new StringEntity(body, Consts.UTF_8);
		}

		return post(url, entity, headers, connTimeout, readTimeout);
	} 
    
    
    
    /** 
     * 发送一个 GET 请求 
     *  
     * @param url 
     * @param charset 
     * @param connTimeout  建立链接超时时间,毫秒. 
     * @param readTimeout  响应超时时间,毫秒. 
     * @return 
     * @throws ConnectTimeoutException   建立链接超时 
     * @throws SocketTimeoutException   响应超时 
     * @throws Exception 
     */  
    public static String get(String url, String charset, Integer connTimeout,Integer readTimeout) 
            throws Exception {
        
        HttpClient client = null;  
        HttpGet get = new HttpGet(url);
        String result = "";  
        try {  
            // 设置参数  
            Builder customReqConf = RequestConfig.custom();  
            if (connTimeout != null) {  
                customReqConf.setConnectTimeout(connTimeout);  
            }  
            if (readTimeout != null) {  
                customReqConf.setSocketTimeout(readTimeout);  
            }  
            get.setConfig(customReqConf.build());  
  
            HttpResponse res = null;  
  
            if (url.startsWith("https")) {  
                // 执行 Https 请求.  
                client = createSSLInsecureClient();  
                res = client.execute(get);  
            } else {  
                // 执行 Http 请求.  
                client = HttpClientUtils.client;  
                res = client.execute(get);  
            }  
  
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {  
            get.releaseConnection();  
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {  
                ((CloseableHttpClient) client).close();  
            }  
        }  
        return result;  
    }

    /**
     *
     * @param url
     * @param jsonStr
     * @return
     */
    public static String post(String url, String jsonStr) {
        String res = "";
        HttpEntity entity = null;
        // 创建默认的httpClient实例.
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httpPost
        HttpPost httpPost = new HttpPost(url);
        try {
            //发送post请求，并设置编码格式
            StringEntity se = new StringEntity(jsonStr, "UTF-8");
            httpPost.setEntity(se);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            entity = response.getEntity();
            if (entity != null) {
                res = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    
    
    /** 
     * 从 response 里获取 charset 
     *  
     * @param ressponse 
     * @return 
     */  
    @SuppressWarnings("unused")  
    private static String getCharsetFromResponse(HttpResponse ressponse) {  
        // Content-Type:text/html; charset=GBK  
        if (ressponse.getEntity() != null  && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null) {  
            String contentType = ressponse.getEntity().getContentType().getValue();  
            if (contentType.contains("charset=")) {  
                return contentType.substring(contentType.indexOf("charset=") + 8);  
            }  
        }  
        return null;  
    }  
    
    
    
    /**
     * 创建 SSL连接
     * @return
     * @throws GeneralSecurityException
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
/*        try {*/

        	// region 测试代码
        	/*SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {
                            return true;
                        }
                    }).build();

            		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {

                        @Override
                        public boolean verify(String arg0, SSLSession arg1) {
                            return true;
                        }
                    });

            return HttpClients.custom().setSSLSocketFactory(sslsf).build();*/

        	 /* SSLContext sslcontext =  SSLContext.getInstance("SSL");
              sslcontext.init(null,
                      new TrustManager[] {  new TrustAnyTrustManager() },
                      new java.security.SecureRandom());
              SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext
            		  ,  new HostnameVerifier() {

                          @Override
                          public boolean verify(String arg0, SSLSession arg1) {
                              return true;
                          }
                      });
              return HttpClients.custom().setSSLSocketFactory(sslsf).build();*/

        	//HttpHost host = new HttpHost(ip, 443, "https");

        	// endregion

        	//{{ 测试代码2
		try {
			CloseableHttpClient httpclient = new DefaultHttpClient();
			// Secure Protocol implementation.
			SSLContext ctx = SSLContext.getInstance("SSL");
			// Implementation of a trust manager for X509 certificates
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {

				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
	
			ClientConnectionManager ccm = httpclient.getConnectionManager();
			// register https protocol in httpclient's scheme registry
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 40443, ssf));
			return httpclient;
		} catch (GeneralSecurityException e) {
			throw e;
		}
    
	    /*try{
	        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
	            //信任所有
	            public boolean isTrusted(X509Certificate[] chain,
	                            String authType) throws CertificateException {
	                return true;
	            }
	        }).build();
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
	  
 
	        
	        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	      
	        
	    }  catch (GeneralSecurityException e) {
	    	e.printStackTrace();
            throw e;
        } */
	 
    }
    
/*    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }*/


    public static String Base64Post(String serverUrl, Map<String, String> params) throws IOException {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            //需要验证
            out.append(JsonUtils.serialize(params));
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
                throw new IOException("请求失败！");
            }

            // 读取响应
            int length = (int) connection.getContentLength();
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8");
                return result;
            }

        }catch(IOException e){
            throw new IOException("请求失败！");
        }
        return null;
    }

    public static byte[] getByteArray(String url) {
        HttpClient client = new DefaultHttpClient();

        try {
            HttpGet httpGet = new HttpGet(url);
//            httpGet.setHeader("Content-Type", "image/png");
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                response.getEntity().writeTo(bos);
                return bos.toByteArray();
            } else {
                System.out.println("状态码" + code + response.getEntity().getContent().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static String getString(String url) {
        HttpClient client = new DefaultHttpClient();

        try {
            HttpGet httpGet = new HttpGet(new URI(url));
//            httpGet.setHeader("Content-Type", "application/json");
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                return IOUtils.toString(response.getEntity().getContent(), charset);
            } else {
                System.out.println("状态码" + code + response.getEntity().getContent().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getPostString(String url, Map<String, Object> param) {
        HttpClient client = new DefaultHttpClient();

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(JsonUtils.serialize(param)));
            HttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                return IOUtils.toString(response.getEntity().getContent(), charset);
            } else {
                System.out.println("状态码" + code + response.getEntity().getContent().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String streamToBase64(String url) {
        String resStr = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httpPost
        HttpPost httpPost = new HttpPost(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                entity.writeTo(bos);
                //同时把数据保存本地一份
                FileOutputStream fout = new FileOutputStream("d:\\test.jpg");
                fout.write(bos.toByteArray());
                fout.close();
                return new BASE64Encoder().encode(bos.toByteArray()).replace("\r","").replace("\n","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resStr;
    }
}