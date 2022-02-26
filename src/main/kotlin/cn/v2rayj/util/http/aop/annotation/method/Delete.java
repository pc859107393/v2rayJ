package cn.v2rayj.util.http.aop.annotation.method;

import cn.v2rayj.util.http.constants.HttpContentType;
import cn.v2rayj.util.http.model.HttpMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping(method = HttpMethod.DELETE)
public @interface Delete {

    /**
     * 请求映射名
     */
    @AliasFor(annotation = Mapping.class)
    String value() default "";


    /**
     * 请求体类型
     */
    @AliasFor(annotation = Mapping.class)
    String contentType() default HttpContentType.NO;
}
