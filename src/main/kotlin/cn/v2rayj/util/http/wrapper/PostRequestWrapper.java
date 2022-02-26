package cn.v2rayj.util.http.wrapper;


import cn.v2rayj.util.StringUtils;
import cn.v2rayj.util.http.constants.HttpContentType;
import cn.v2rayj.util.http.exception.HttpClientException;
import cn.v2rayj.util.http.model.HttpRequest;
import cn.v2rayj.util.http.utils.FileRequestBody;
import cn.v2rayj.util.http.utils.InnerFileUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class PostRequestWrapper implements RequestWrapper {

    protected final String url;
    protected final HttpRequest params;

    public PostRequestWrapper(String url, HttpRequest params) {
        this.url = url;
        this.params = params;
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(params))
                .post(initBody())
                .build();
    }

    protected RequestBody initBody() {
        //如果非文件上传
        if (!params.isHasFile()) {
            if (params.isJson()) {
                return RequestBody.create(params.getJsonBody(), MediaType.parse(HttpContentType.JSON));
            }
            String content = params.getParams().toString();
            return RequestBody.create(
                    content,
                    MediaType.parse(StringUtils.isEmpty(params.getContentType()) ? HttpContentType.FORM : params.getContentType())
            );
        }
        //如果是单文件上传
        Set<Map.Entry<String, Object>> entries = params.getParamsEntries().entrySet();
        //有一个文件且参数集合为空，说明塞入了文件流
        if (entries.isEmpty() && null != params.getBytes()) {
            String contentType = StringUtils.isEmpty(params.getContentType()) ? HttpContentType.FILE : params.getContentType();
            return FileRequestBody.create(contentType, params.getBytes(), params.isChunked());
        }
        //如果是表单上传，上传多文件，或者携带除文件意外的其他参数请求的上传文件
        if (entries.size() > 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(Objects.requireNonNull(MediaType.parse(HttpContentType.MULTIPART_FILE)));
            //创建文件请求体
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() instanceof File) {
                    builder.addFormDataPart(entry.getKey(), entry.getKey(),
                            RequestBody.create((File) entry.getValue(), MultipartBody.FORM));
                    continue;
                }
                if (entry.getValue() instanceof byte[]) {
                    builder.addFormDataPart(entry.getKey(), entry.getKey(),
                            RequestBody.create((byte[]) entry.getValue(), MultipartBody.FORM));
                    continue;
                }
                if (entry.getValue() instanceof InputStream) {
                    builder.addFormDataPart(entry.getKey(), entry.getKey(),
                            RequestBody.create(InnerFileUtil.input2byte((InputStream) entry.getValue()), MultipartBody.FORM));
                    continue;
                }
                builder.addFormDataPart(entry.getKey(), (String) entry.getValue());
            }
            return builder.build();
        }
        throw new HttpClientException("error request body!");
    }

}
