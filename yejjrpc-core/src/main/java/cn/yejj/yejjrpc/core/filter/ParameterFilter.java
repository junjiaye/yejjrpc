package cn.yejj.yejjrpc.core.filter;

import cn.yejj.yejjrpc.core.api.Filter;
import cn.yejj.yejjrpc.core.api.RpcContext;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * @program: yejjrpc
 * @ClassName: ParameterFilter
 * @description:
 * @author: yejj
 * @create: 2024-04-06 15:35
 */
public class ParameterFilter implements Filter {
    @Override
    public Object prefilter(RpcRequest rpcRequest) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if(!params.isEmpty()) {
            rpcRequest.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postfilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
        return null;
    }
}
