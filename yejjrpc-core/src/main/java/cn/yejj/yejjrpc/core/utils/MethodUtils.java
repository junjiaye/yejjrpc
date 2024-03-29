package cn.yejj.yejjrpc.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: yejjrpc
 * @ClassName: MethodUtils
 * @description:
 * @author: yejj
 * @create: 2024-03-12 15:55
 */
public class MethodUtils {

    public static boolean checkLocalMethod(Method method){
        //获取方法所属的类，并判断是否是属于Object
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
        return methodSign.toString();
    }

    /**
     * 扫描bean里边的字段是否包含YejjConsumer注解
     * @param clazz
     * @return
     */
    public static List<Field> findAnnotatedField(Class<?> clazz,Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (clazz != null){
            Field[] fields = clazz.getDeclaredFields();
            //TODO 改成Stream
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return result;
    }
}
