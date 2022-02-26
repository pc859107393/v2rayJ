package cn.v2rayj.util.http.aop.handler;


import cn.v2rayj.util.StringUtils;
import cn.v2rayj.util.http.OkBuilder;
import cn.v2rayj.util.http.aop.annotation.Headers;
import cn.v2rayj.util.http.aop.model.HttpMapping;
import cn.v2rayj.util.http.exception.HttpClientException;
import cn.v2rayj.util.http.model.HttpHeaders;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class MappingHeadersPaddingHandler {

    /**
     * 注解形式的静态请求头填充(方法)
     * <p>
     * 检查方法上是否存在Headers注解
     * 如果存在headers注解，则解析headers当中的静态请求头内容，并填充到mapping缓存中
     * </p>
     */
    public static void headersOnMethod(HttpMapping mapping, Method method) {
        //检查参数形式的请求头参数类型合法性
        headsOnMethodParamsCheck(method);
        //加载静态请求头
        mapping.getHeaders().add(loadAnnotationHeaders(method.getAnnotation(Headers.class)));
    }

    /**
     * 参数形式的动态请求头填充
     * <p>
     * 检查方法的参数上是否存在Headers注解
     * 如果存在headers注解，则解析headers当中的静态请求头内容，并填充到mapping缓存中
     * </p>
     *
     * @param okBuilder 请求构造器
     * @param method    映射方法
     */
    @SuppressWarnings("unchecked")
    public static void headsOnMethodParams(OkBuilder okBuilder, Method method, Object[] args) {
        //检查是否存在参数请求头填充对象，如果存在，则填充参数请求头
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Headers.class)) {
                Object arg = args[i];
                if (arg instanceof Map) {
                    okBuilder.headers((Map<String, String>) arg);
                } else if (arg instanceof HttpHeaders) {
                    okBuilder.headers((HttpHeaders) arg);
                } else if (arg instanceof String) {
                    try {
                        //字符串参数处理错误的时候，不处理
                        String[] split = ((String) arg).split(":");
                        okBuilder.header(split[0], split[1]);
                    } catch (Exception ignored) {
                    }
                }
                //其他类型不处理
            }
        }
    }

    private static Class[] headerTypes = {HttpHeaders.class, Map.class, String.class};

    /**
     * 检查方法请求头参数的合法性
     * <p>
     * 被{@link Headers}标识的注解一定要是{@link HttpHeaders}类型
     * </p>
     *
     * @param method
     */
    private static void headsOnMethodParamsCheck(Method method) {
        for (Parameter arg : method.getParameters()) {
            if (arg.isAnnotationPresent(Headers.class) && Arrays.stream(headerTypes).noneMatch(it -> it == arg.getType())) {
                throw new HttpClientException(String.format(
                        "the annotation identified by Headers must be %s type!",
                        HttpHeaders.class),
                        method
                );
            }
        }
    }

    /**
     * 从请求头注解中加载请求头
     *
     * @param annotation 请求头注解
     * @return
     */
    public static HttpHeaders loadAnnotationHeaders(Headers annotation) {
        HttpHeaders httpHeaders = HttpHeaders.builder();
        Optional.ofNullable(annotation)
                .ifPresent(headers -> Arrays.stream(headers.value())
                        .filter(it -> !StringUtils.isEmpty(it))
                        .map(it -> it.split(":"))
                        .filter(it -> it.length == 2)
                        .forEach(it -> httpHeaders.add(it[0], it[1]))
                );
        return httpHeaders;
    }
}
