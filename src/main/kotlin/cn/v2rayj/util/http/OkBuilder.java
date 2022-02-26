package cn.v2rayj.util.http;


import cn.v2rayj.util.StringUtils;
import cn.v2rayj.util.http.interceptor.GzipRequestInterceptor;
import cn.v2rayj.util.http.interceptor.LogInterceptor;
import cn.v2rayj.util.http.model.HttpHeaders;
import cn.v2rayj.util.http.model.HttpMethod;
import cn.v2rayj.util.http.model.HttpRequest;
import cn.v2rayj.util.http.utils.InnerFileUtil;
import cn.v2rayj.util.http.utils.RequestFactory;
import cn.v2rayj.util.http.utils.SSLUtil;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class OkBuilder {

    /**
     * 请求地址
     */
    private final String url;

    /**
     * 请求参数
     */
    private final HttpRequest request = new HttpRequest();

    /**
     * 是否开启gzip
     */
    private boolean isGzip = false;

    /**
     * 请求方式
     */
    private HttpMethod method = HttpMethod.GET;

    private OkBuilder(String url) {
        this.url = url;
    }

    /**
     * 构建实例
     *
     * @param url
     * @return
     */
    public static OkBuilder url(String url) {
        return new OkBuilder(url);
    }

    /**
     * 填充请求方式
     *
     * @param method
     * @return
     */
    public OkBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * 是否开启gzip
     *
     * @param isGzip
     * @return
     */
    public OkBuilder gzip(boolean isGzip) {
        this.isGzip = isGzip;
        return this;
    }

    /**
     * 填充请求头
     *
     * @param key   key
     * @param value value
     * @return
     */
    public OkBuilder header(String key, String value) {
        request.putHeaders(key, value);
        return this;
    }

    /**
     * 添加请求头 headers
     *
     * @param headers
     * @return
     */
    public OkBuilder headers(HttpHeaders headers) {
        request.putHeaders(headers);
        return this;
    }

    public OkBuilder headers(Map<String, String> headers) {
        request.putHeaders(headers);
        return this;
    }

    /**
     * 定义内容传输类型
     *
     * @param contentType
     * @return
     */
    public OkBuilder contentType(String contentType) {
        request.setContentType(contentType);
        return this;
    }

    public OkBuilder params(String key, String value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, Collection<? extends Serializable> value) {
        request.put(key, value.toString());
        return this;
    }

    public OkBuilder params(String key, int value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, long value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, boolean value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, float value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, double value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, char value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, File value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, byte[] value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, InputStream value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder body(File file) {
        return body(InnerFileUtil.file2byte(file));
    }

    public OkBuilder body(InputStream stream) {
        return body(InnerFileUtil.input2byte(stream));
    }

    public OkBuilder body(byte[] file) {
        request.put(file);
        return this;
    }

    public OkBuilder transferEncoding() {
        if (HttpMethod.GET.equals(method)) {
            throw new RuntimeException("http method can not be get!");
        }
        request.setChunked();
        request.putHeaders("Transfer-Encoding", "chunked");
        return this;
    }

    /**
     * 使用map集合填充参数
     *
     * @param map
     * @return
     */
    public OkBuilder params(Map<String, Object> map) {
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                if (value instanceof File) {
                    params(key, (File) value);
                } else if (value instanceof InputStream) {
                    params(key, (InputStream) value);
                } else if (value instanceof byte[]) {
                    params(key, (byte[]) value);
                } else if (value instanceof String) {
                    params(key, (String) value);
                } else if (value instanceof Collection) {
                    params(key, (Collection<? extends Serializable>) value);
                } else if (value instanceof Integer) {
                    params(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    params(key, (Boolean) value);
                } else if (value instanceof Double) {
                    params(key, (Double) value);
                } else if (value instanceof Character) {
                    params(key, (Character) value);
                }
            }
        }
        return this;
    }

    /**
     * 定义请求为JSON请求
     *
     * @return
     */
    public OkBuilder isJson(Object obj) {
        request.setJson(Boolean.TRUE);
        request.setJsonBody(obj);
        return this;
    }

    /**
     * 开启jsonPost请求
     *
     * @return
     */
    public OkBuilder isJson() {
        return this.isJson(null);
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public String execute() {
        try (Response response = beforeExecute()) {
            if (null == response) {
                throw new IOException("请求失败，无响应结果！");
            }
            if (null != response.body()) {
                String result = Objects.requireNonNull(response.body()).string();
                response.close();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response response() {
        return beforeExecute();
    }


    /**
     * 构造ws的请求
     *
     * @return
     */
    public WebSocket initWs(WebSocketListener listener) {
        if (StringUtils.isEmpty(url) || !(url.contains("ws://") || url.contains("wss://"))) {
            throw new IllegalArgumentException("Url can not be supported !");
        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                //设置读取超时时间
                .readTimeout(3, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(3, TimeUnit.SECONDS)
                //设置连接超时时间
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        //构造请求
        return mOkHttpClient.newWebSocket(initRequest(), listener);
    }


    /**
     * 构造带有ping-pong的ws请求
     *
     * @return
     */
    public WebSocket initPingWs(WebSocketListener listener) {
        if (StringUtils.isEmpty(url) || !(url.contains("ws://") || url.contains("wss://"))) {
            throw new IllegalArgumentException("Url can not be supported !");
        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                //设置读取超时时间
                .readTimeout(3, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(3, TimeUnit.SECONDS)
                //设置连接超时时间
                .connectTimeout(30, TimeUnit.SECONDS)
                .pingInterval(30, TimeUnit.SECONDS)
                .build();

        //构造请求
        return mOkHttpClient.newWebSocket(initRequest(), listener);
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public InputStream executeStream() {
        try (Response response = beforeExecute()) {
            if (null == response) {
                throw new IOException("Request failed, Not responding!");
            }
            InputStream body = response.body().byteStream();
            response.close();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public byte[] executeBytes() {
        try (Response response = beforeExecute()) {
            if (null == response) {
                throw new IOException("请求失败，无响应结果！");
            }
            if (null != response.body()) {
                byte[] bytes = response.body().bytes();
                response.close();
                return bytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public Response executeResponse() {
        try (Response response = beforeExecute()) {
            if (null == response) {
                throw new IOException("Request failed, Not responding!");
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response beforeExecute() {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Url can not be null!");
        }
        //构造请求
        Request request = initRequest();
        Response response;
        try {
            if (isGzip) {
                response = new OkHttpClient.Builder()
                        .addInterceptor(new GzipRequestInterceptor())
                        .sslSocketFactory(SSLUtil.createSSLSocketFactory(), new SSLUtil.TrustAllManager())
                        .hostnameVerifier(new SSLUtil.TrustAllHostnameVerifier())
                        .addInterceptor(new LogInterceptor())
                        .build()
                        .newCall(request)
                        .execute();
            } else {
                response = new OkHttpClient.Builder()
                        .sslSocketFactory(SSLUtil.createSSLSocketFactory(), new SSLUtil.TrustAllManager())
                        .hostnameVerifier(new SSLUtil.TrustAllHostnameVerifier())
                        .addInterceptor(new LogInterceptor())
                        .build()
                        .newCall(request)
                        .execute();
            }
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造请求，根据请求类型不同，此处应该采用工厂模式
     *
     * @return
     */
    private Request initRequest() {
        return new RequestFactory(method, request, url).initRequest();
    }
}
