package cn.yejj.yejjrpc.core.utils;

import cn.yejj.yejjrpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @program: yejjrpc
 * @ClassName: TypeUtils
 * @description:
 * @author: yejj
 * @create: 2024-03-16 21:38
 */
@Slf4j
public class TypeUtils {

    public static Object cast(Object origin,Class<?> type){
        if (origin == null){
            return null;
        }

        if(type.isArray()){
            if (origin instanceof List list){
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(resultArray,i,Array.get(origin,i));
            }
            return resultArray;
        }

        Class<?> aclass = origin.getClass();
        //判断aclass是否是type的子类或相同类型
        if(type.isAssignableFrom(aclass)){
            return origin;
        }

        if (origin instanceof HashMap map){
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if(type.equals(Integer.class) || type.equals(Integer.TYPE)){
            return Integer.valueOf(origin.toString());
        }else if(type.equals(Long.class) || type.equals(Long.TYPE)){
            return Long.valueOf(origin.toString());
        } else if(type.equals(Byte.class) || type.equals(Byte.TYPE)){
            return Byte.valueOf(origin.toString());
        }else if(type.equals(Short.class) || type.equals(Short.TYPE)){
            return Short.valueOf(origin.toString());
        }else if(type.equals(Character.class) || type.equals(Character.TYPE)){
            return Character.valueOf(origin.toString().charAt(0));
        }else if(type.equals(Double.class) || type.equals(Double.TYPE)){
            return Double.valueOf(origin.toString());
        }else if(type.equals(Float.class) || type.equals(Float.TYPE)){
            return Float.valueOf(origin.toString());
        }
        return null;
    }

    @Nullable
    public static Object castResult(Method method, Object result, RpcResponse rpcResponse) {
        Class<?> returnType = method.getReturnType();
        if(result instanceof JSONObject jsonResult){
            if (Map.class.isAssignableFrom(returnType)) {
                Map resultMap = new HashMap();
                Type genericReturnType = method.getGenericReturnType();
                log.debug(genericReturnType.toString());
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Class<?> keyType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                    log.debug("keyType  : " + keyType);
                    log.debug("valueType: " + valueType);
                    jsonResult.entrySet().stream().forEach(
                            e -> {
                                Object key = cast(e.getKey(), keyType);
                                Object value = cast(e.getValue(), valueType);
                                resultMap.put(key, value);
                            }
                    );
                }
                return resultMap;
            }
            JSONObject jsonrResult = (JSONObject) rpcResponse.getResult();
            return jsonrResult.toJavaObject(returnType);
        } else if (result instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if (returnType.isArray()) {
                Class<?> componentType = returnType.getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resultArray, i, array[i]);
                    } else {
                        Object castObject = cast(array[i], componentType);
                        Array.set(resultArray, i, castObject);
                    }
                }
                return resultArray;
            } else if (List.class.isAssignableFrom(returnType)) {
                List<Object> resultList = new ArrayList<>(array.length);
                Type genericReturnType = method.getGenericReturnType();
                log.debug(genericReturnType.toString());
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    log.debug(actualType.toString());
                    for (Object o : array) {
                        resultList.add(cast(o, (Class<?>) actualType));
                    }
                } else {
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            } else {
                return null;
            }
        }else{
            return cast(result, returnType);
        }
    }
}
