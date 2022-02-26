package cn.v2rayj.util.http.exception;

import java.lang.reflect.Method;

public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String format, Method method) {
        throw new HttpClientException(String.format("%s \n -> Error by: %s#%s", format, method.getDeclaringClass().getName(), method.getName()));
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
