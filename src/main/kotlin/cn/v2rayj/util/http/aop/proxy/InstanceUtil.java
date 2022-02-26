package cn.v2rayj.util.http.aop.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class InstanceUtil {

    @SuppressWarnings("unchecked")
    public static <T> T jdkBuild(Class<T> classInterface, InvocationHandler proxy) {
        return (T) Proxy.newProxyInstance(classInterface.getClassLoader(), new Class[] {classInterface}, proxy);
    }
}
