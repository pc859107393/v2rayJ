package cn.v2rayj.util.http.aop.model;

import cn.v2rayj.util.http.aop.annotation.method.Mapping;
import cn.v2rayj.util.http.aop.handler.MappingHeadersPaddingHandler;
import cn.v2rayj.util.http.aop.handler.MappingPathResolveHandler;
import cn.v2rayj.util.http.constants.HttpProtocol;
import cn.v2rayj.util.http.exception.HttpClientException;
import cn.v2rayj.util.http.model.HttpHeaders;
import cn.v2rayj.util.http.model.HttpMethod;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpMapping {

    /**
     * 请求前缀
     * http://
     * https://
     * ws://
     * wss://
     *
     * @see HttpProtocol
     */
    private String protocol;

    /**
     * host
     */
    private String host;

    /**
     * 请求资源路径
     */
    private String path;

    /**
     * 请求头
     */
    private HttpHeaders headers = HttpHeaders.builder();

    /**
     * 是否是动态资源路径
     */
    private boolean isDynamicPath;

    /**
     * 请求方式
     */
    private HttpMethod method;

    /**
     * contentType
     */
    private String contentType;

    /**
     * 请求地址
     *
     * @return
     */
    public String url() {
        if (!path.startsWith("/")) {
            return String.format("%s%s/%s", protocol, host, path);
        }
        return String.format("%s%s%s", protocol, host, path);
    }

    /**
     * 构建
     *
     * @param host    host
     * @param headers 是否存在类全局请求头
     * @param mapping 请求映射
     * @return
     */
    public static HttpMapping builder(String host, String uri, HttpHeaders headers, Mapping mapping, Method method) {
        HttpMapping httpMapping = new HttpMapping();
        httpMapping.setMethod(mapping.method());
        httpMapping.setContentType(mapping.contentType());
        httpMapping.headers.add(headers);

        URL url = null;
        try {
            //检查是不是合法的 protocol
            String httpPrefix = HttpProtocol.getProtocol(uri);
            if (null != httpPrefix) {
                if ((httpPrefix.equals(HttpProtocol.WS) || httpPrefix.equals(HttpProtocol.WSS))) {
                    //存在前缀，且有websocket协议
                    httpMapping.setProtocol(httpPrefix);
                    uri = uri.replaceFirst(httpPrefix, HttpProtocol.HTTP);
                } else {
                    httpMapping.setProtocol(httpPrefix);
                }
            }
            //先使用uri进行url检查
            url = new URL(uri);
            //能构造成功url，说明是完整URL
            uri = url.getPath();
            if (null != url.getQuery() && url.getQuery().length() > 0) {
                uri += String.format("?%s", url.getQuery());
            }
        } catch (MalformedURLException e) {
            //uri构造url失败，说明不包含 url 的头部信息
            try {
                //检查是不是合法的 protocol
                String httpPrefix = HttpProtocol.getProtocol(host);
                if (null != httpPrefix) {
                    if ((httpPrefix.equals(HttpProtocol.WS) || httpPrefix.equals(HttpProtocol.WSS))) {
                        //存在前缀，且有websocket协议
                        httpMapping.setProtocol(httpPrefix);
                        host = host.replaceFirst(httpPrefix, HttpProtocol.HTTP);
                    } else {
                        httpMapping.setProtocol(httpPrefix);
                    }
                } else {
                    httpMapping.setProtocol(HttpProtocol.HTTP);
                    host = HttpProtocol.HTTP + host;
                }
                url = new URL(host);
            } catch (MalformedURLException exception) {
                throw new HttpClientException("the url is error!", method);
            }
        }

        if (url.getPort() != 80 && url.getPort() != 443 && url.getPort() > 0) {
            host = url.getHost();
            host = String.format("%s:%s", host, url.getPort());
        } else {
            host = url.getHost();
        }
        httpMapping.setHost(host);
        httpMapping.setPath(uri);

        //检查是否存在动态映射
        MappingPathResolveHandler.dynamicPathCheck(httpMapping);
        //解析静态请求头
        MappingHeadersPaddingHandler.headersOnMethod(httpMapping, method);
        //解析参数
        return httpMapping;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public boolean isDynamicPath() {
        return isDynamicPath;
    }

    public void setDynamicPath(boolean dynamicPath) {
        isDynamicPath = dynamicPath;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
