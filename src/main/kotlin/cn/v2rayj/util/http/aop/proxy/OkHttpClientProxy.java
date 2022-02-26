package cn.v2rayj.util.http.aop.proxy;

import cn.v2rayj.util.JsonUtil;
import cn.v2rayj.util.StringUtils;
import cn.v2rayj.util.http.OkBuilder;
import cn.v2rayj.util.http.aop.annotation.Headers;
import cn.v2rayj.util.http.aop.annotation.HttpClient;
import cn.v2rayj.util.http.aop.annotation.method.Mapping;
import cn.v2rayj.util.http.aop.handler.MappingHeadersPaddingHandler;
import cn.v2rayj.util.http.aop.handler.MappingParamsPaddingHandler;
import cn.v2rayj.util.http.aop.handler.MappingPathResolveHandler;
import cn.v2rayj.util.http.aop.model.HttpMapping;
import cn.v2rayj.util.http.exception.HttpClientException;
import cn.v2rayj.util.http.model.HttpHeaders;
import okhttp3.Response;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OkHttpClientProxy<T> implements InvocationHandler {

    /**
     * 代理类型实例
     */
    private final Class<T> proxyInterface;

    /**
     * 映射缓存
     */
    private final Map<String, HttpMapping> mappingMap = new HashMap<>(1 >> 4);

    private OkHttpClientProxy(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
        //注册Mapping
        registerMappings(
                proxyInterface.getAnnotation(HttpClient.class).host(),
                MappingHeadersPaddingHandler.loadAnnotationHeaders(proxyInterface.getAnnotation(Headers.class))
        );
    }

    /**
     * 初始化代理容器
     * 传入参数必须是声明了注解：@{@link HttpClient}
     * 的接口类
     *
     * @param proxyInterface 需要实现代理的接口.class
     */
    public static <T> OkHttpClientProxy<T> proxy(Class<T> proxyInterface) {
        //创建代理容器
        return new OkHttpClientProxy<T>(proxyInterface);
    }

    public T build() {
        return InstanceUtil.jdkBuild(proxyInterface, this);
    }

    /**
     * 代理类方法执行的实现
     *
     * @param proxy  代理
     * @param method 方法
     * @param args   参数
     * @return object 对应方法的返回参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        HttpMapping httpMapping = mappingMap.get(method.getClass().getCanonicalName() + method.toGenericString());
        //检查动态映射,构建请求器
        OkBuilder okBuilder = OkBuilder.url(
                        //是否存在动态映射地址
                        httpMapping.isDynamicPath()
                                ? MappingPathResolveHandler.dynamicPathResolve(httpMapping, method, args)
                                : httpMapping.url())
                //填充请求方式
                .method(httpMapping.getMethod())
                //填充请求内容类型
                .contentType(httpMapping.getContentType())
                //填充静态请求头
                .headers(httpMapping.getHeaders());
        //填充参数请求头
        MappingHeadersPaddingHandler.headsOnMethodParams(okBuilder, method, args);
        //填充请求参数
        MappingParamsPaddingHandler.handler(okBuilder, method, args);

        Class<?> returnType = method.getReturnType();
        if (returnType == String.class) {
            return okBuilder.execute();
        }
        if (returnType.isArray() && returnType.getComponentType().equals(byte.class)) {
            return okBuilder.executeBytes();
        }
        if (returnType == Response.class) {
            return okBuilder.executeResponse();
        }

        return JsonUtil.parseObject(okBuilder.execute(), new com.fasterxml.jackson.core.type.TypeReference<Object>() {
            @Override
            public Type getType() {
                return method.getReturnType();
            }
        });
    }

    /**
     * 注册client的所有方法
     */
    private void registerMappings(String host, HttpHeaders headers) {
        Arrays.stream(this.proxyInterface.getMethods()).forEach(method -> {
            Mapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, Mapping.class);
            //不存在任何映射注解，抛出异常
            if (null == mapping) {
                throw new HttpClientException("no mapping annotation exists!", method);
            }
            //如果没有定义地址，则抛出地址未定义的异常
            if (StringUtils.isEmpty(mapping.value())) {
                throw new HttpClientException("the request url is undefined!", method);
            }
            //缓存mapping
            mappingMap.put(method.getClass().getCanonicalName() + method.toGenericString(),
                    HttpMapping.builder(host, mapping.value(), headers, mapping, method));
        });
    }

}
