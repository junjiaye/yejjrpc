package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;

/**
 * @program: yejjrpc
 * @ClassName: HttpInvoker
 * @description:
 * @author: yejj
 * @create: 2024-03-22 10:47
 */
public interface HttpInvoker {
    RpcResponse post(RpcRequest rpcRequest, String url) ;

}
