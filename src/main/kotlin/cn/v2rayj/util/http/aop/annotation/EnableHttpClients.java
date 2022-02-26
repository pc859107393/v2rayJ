package cn.v2rayj.util.http.aop.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableHttpClients {

    /**
     * 包地址路径集合
     */
    String[] value() default {};

    /**
     * 包地址路径集合
     */
    String[] basePackages() default {};

}
