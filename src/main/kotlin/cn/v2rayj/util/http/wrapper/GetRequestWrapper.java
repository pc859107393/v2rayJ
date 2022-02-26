package cn.v2rayj.util.http.wrapper;

import cn.v2rayj.util.StringUtils;
import cn.v2rayj.util.http.model.HttpRequest;
import okhttp3.Request;

public class GetRequestWrapper implements RequestWrapper {
    private final String url;
    private final HttpRequest params;

    public GetRequestWrapper(String url, HttpRequest params) {
        StringBuilder urlParams = params.getParams();
        if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(urlParams.toString())) {
            if (url.contains("?")) {
                url = url + "&" + urlParams;
            } else {
                url = url + "?" + urlParams;
            }
        }

        this.url = url;
        this.params = params;
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(params))
                .get()
                .build();
    }
}