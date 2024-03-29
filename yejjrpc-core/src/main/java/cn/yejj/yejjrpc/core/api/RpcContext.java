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
    LoaderBalacer<InstanceMata> loaderBalacer;
    private Map<String, String> parameters = new HashMap<>();
}
