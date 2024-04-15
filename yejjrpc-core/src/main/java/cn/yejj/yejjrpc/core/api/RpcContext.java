package cn.yejj.yejjrpc.core.api;

import cn.yejj.yejjrpc.core.mate.InstanceMata;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: yejjrpc
 * @ClassName: RpcContext
 * @description:
 * @author: yejj
 * @create: 2024-03-20 15:24
 */
@Data
public class RpcContext {
    List<Filter> filters;
    Router<InstanceMata> router;
    LoaderBalancer<InstanceMata> loaderBalancer;
    private Map<String, String> parameters = new HashMap<>();
    public String param(String key) {
        return parameters.get(key);
    }

    public static ThreadLocal<Map<String,String>> ContextParameters = new ThreadLocal<>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        ContextParameters.get().remove(key);
    }

}
