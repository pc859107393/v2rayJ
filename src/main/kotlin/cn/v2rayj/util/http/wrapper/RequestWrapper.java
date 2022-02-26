package cn.v2rayj.util.http.wrapper;

import cn.v2rayj.util.http.model.HttpHeaders;
import cn.v2rayj.util.http.model.HttpRequest;
import okhttp3.Headers;
import okhttp3.Request;


/**
 * @author cheng
 */
public interface RequestWrapper {

    /**
     * 默认的请求头构造器
     *
     * @param params
     * @return
     */
    default Headers initHeaders(HttpRequest params) {
        // 创建Headers
        HttpHeaders httpHeaders = params.getHeaders();
        Headers.Builder headerBuilder = new Headers.Builder();
        httpHeaders.foreach(headerBuilder::add);
        return headerBuilder.build();
    }

    /**
     * 请求构造
     *
     * @return
     */
    Request create();
}