package cn.yejj.yejjrpc.core.filter;

import cn.yejj.yejjrpc.core.api.Filter;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.exception.RpcException;
import cn.yejj.yejjrpc.core.exception.RpcExceptionEnum;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @program: yejjrpc
 * @ClassName: CacheFilter
 * @description:
 * @author: yejj
 * @create: 2024-03-25 14:34
 */

public class CacheFilter implements Filter {
    //替换成guava cahce，加容量、过期时间 TODO
   // static Map<String,Object> cache = new ConcurrentHashMap<>();

    static LoadingCache<String, Object> cahceBuilder = CacheBuilder
            .newBuilder()
            .build(new CacheLoader<String, Object>() {
                @Override
                public String load(String key) throws Exception {
                    return   "no " + key + "!";
                }

            });
    @Override
    public Object prefilter(RpcRequest rpcRequest) {
        try {
            return cahceBuilder.get(rpcRequest.toString());
        } catch (ExecutionException e) {
            throw new RpcException(e, RpcExceptionEnum.UNKNOWN_EX);
        }
    }

    @Override
    public Object postfilter(RpcRequest rpcRequest, RpcResponse rpcResponse,Object result) {
       // cache.putIfAbsent(rpcRequest.toString(),result);
        cahceBuilder.put(rpcRequest.toString(),result);
        return result;
    }
}
