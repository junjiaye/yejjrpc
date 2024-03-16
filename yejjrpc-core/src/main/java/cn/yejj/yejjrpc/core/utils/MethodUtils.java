package cn.yejj.yejjrpc.core.utils;

import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @program: yejjrpc
 * @ClassName: MethodUtils
 * @description:
 * @author: yejj
 * @create: 2024-03-12 15:55
 */
public class MethodUtils {

    public static boolean checkLocalMethod(Method method){
        return method.getDeclaringClass().equals(Object.class);
    }
    public static String methodSign(Method method) {
        if(method == null){
            return "";
        }
        String name = method.getName();
        StringBuilder methodSign = new StringBuilder();
        methodSign.append(name);
        int parameterCount = method.getParameterCount();
        methodSign.append(parameterCount);
        if (parameterCount == 0){
            return methodSign.toString();
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Arrays.stream(parameterTypes).forEach(clazz -> methodSign.append(clazz.getName()).append(","));
        return DigestUtils.md5DigestAsHex(methodSign.toString().getBytes());
    }
}
