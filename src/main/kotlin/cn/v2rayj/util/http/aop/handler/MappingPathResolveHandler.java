package cn.v2rayj.util.http.aop.handler;


import cn.v2rayj.util.StringUtils;
import cn.v2rayj.util.http.aop.annotation.paramer.PathParam;
import cn.v2rayj.util.http.aop.model.HttpMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MappingPathResolveHandler {

    /**
     * 动态地址检查
     *
     * @param entity mapping对象
     */
    public static void dynamicPathCheck(HttpMapping entity) {
        if (entity.getPath().contains("{") && entity.getPath().lastIndexOf("}") > -1) {
            entity.setDynamicPath(true);
        }
    }


    /**
     * 返回动态地址
     *
     * @param entity mapping对象
     * @param method 请求方法
     * @param args   请求方法参数
     */
    public static String dynamicPathResolve(HttpMapping entity, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Map<String, String> map = new HashMap<>(8);
        for (int i = 0; i < parameters.length; i++) {
            PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
            if (null != pathParam) {
                map.put(pathParam.value(), args[i].toString());
            }
        }
        //生成一个新的path
        StringBuilder sb = new StringBuilder();
        String[] paths = Arrays.stream(entity.getPath().split("/"))
                .filter(it -> !StringUtils.isEmpty(it)).toArray(String[]::new);
        for (String it : paths) {
            sb.append("/");
            if (it.contains("{") && it.lastIndexOf("}") > -1) {
                String paramsName = it.substring(1, it.length() - 1);
                sb.append(map.get(paramsName));
                continue;
            }
            sb.append(it);
        }
        return String.format("%s%s%s", entity.getProtocol(), entity.getHost(), sb);
    }

}
