package cn.v2rayj.util.http.wrapper;

import cn.v2rayj.util.http.model.HttpRequest;
import okhttp3.Request;

public class PatchRequestWrapper extends PostRequestWrapper {

    public PatchRequestWrapper(String url, HttpRequest params) {
        super(url, params);
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(super.params))
                .patch(super.initBody())
                .build();
    }
}
