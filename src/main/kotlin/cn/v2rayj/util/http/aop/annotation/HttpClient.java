package cn.v2rayj.util.http.aop.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClient {

    /**
     * host
     */
    String host() default "";

    /**
     * bean名称
     * 默认取ClassName作为bean名称
     */
    String name() default "";
}
