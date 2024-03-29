package cn.yejj.yejjrpc.core.filter;

import cn.yejj.yejjrpc.core.api.Filter;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: yejjrpc
 * @ClassName: CacheFilter
 * @description:
 * @author: yejj
 * @create: 2024-03-25 14:34
 */

public class CacheFilter implements Filter {
    //替换成guava cahce，加容量、过期时间 TODO
    static Map<String,Object> cache = new ConcurrentHashMap<>();
    @Override
    public Object prefilter(RpcRequest rpcRequest) {
        return cache.get(rpcRequest.toString());
    }

    @Override
    public Object postfilter(RpcRequest rpcRequest, RpcResponse rpcResponse,Object result) {
        cache.putIfAbsent(rpcRequest.toString(),result);
        return result;
    }
}
