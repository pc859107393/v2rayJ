package cn.v2rayj.util.http.aop.annotation.paramer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    /**
     * Alias for {@link #name}.
     */
    String value() default "";

    /**
     * The name of the path variable to bind to.
     */
    String name() default "";

    /**
     * 必须为true
     *
     * @return
     */
    boolean required() default true;

}
