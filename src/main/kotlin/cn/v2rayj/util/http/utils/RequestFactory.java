package cn.v2rayj.util.http.utils;

import cn.v2rayj.util.http.model.HttpMethod;
import cn.v2rayj.util.http.model.HttpRequest;
import cn.v2rayj.util.http.wrapper.DeleteRequestWrapper;
import cn.v2rayj.util.http.wrapper.GetRequestWrapper;
import cn.v2rayj.util.http.wrapper.PatchRequestWrapper;
import cn.v2rayj.util.http.wrapper.PostRequestWrapper;
import cn.v2rayj.util.http.wrapper.PutRequestWrapper;
import cn.v2rayj.util.http.wrapper.WsRequestWrapper;
import okhttp3.Request;

public class RequestFactory {

    private final HttpMethod method;
    private final HttpRequest params;
    private final String url;

    /**
     * 根据这个工厂生成对应okhttp的请求
     *
     * @param method http请求方法
     * @param params http请求参数
     * @param url    http请求url
     */
    public RequestFactory(HttpMethod method, HttpRequest params, String url) {
        this.method = method;
        this.params = params;
        this.url = url;
    }

    /**
     * 根据请求内容型实现请求体生成
     *
     * @return
     */
    public Request initRequest() {
        switch (method) {
            case PATCH:
                return new PatchRequestWrapper(url, params).create();
            case DELETE:
                return new DeleteRequestWrapper(url, params).create();
            case GET:
                return new GetRequestWrapper(url, params).create();
            case WS:
                return new WsRequestWrapper(url, params).create();
            case PUT:
                return new PutRequestWrapper(url, params).create();
            case POST:
                return new PostRequestWrapper(url, params).create();
            default: {
                throw new RuntimeException(String.format("不支持的请求方式: [%s]", method));
            }
        }
    }

}

