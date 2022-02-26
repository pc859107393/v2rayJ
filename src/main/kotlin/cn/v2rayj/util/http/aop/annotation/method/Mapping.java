package cn.v2rayj.util.http.aop.annotation.method;

import cn.v2rayj.util.http.constants.HttpContentType;
import cn.v2rayj.util.http.model.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Mapping {

    /**
     * 请求地址 or 映射
     */
    String value() default "";

    /**
     * 请求方式
     */
    HttpMethod method();

    /**
     * 请求体类型
     */
    String contentType() default HttpContentType.NO;
}
