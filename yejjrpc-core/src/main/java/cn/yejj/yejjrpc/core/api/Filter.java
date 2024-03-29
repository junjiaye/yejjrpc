package cn.yejj.yejjrpc.core.api;

public interface Filter {
    Object  prefilter(RpcRequest rpcRequest);

    Object  postfilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result);

    //    Filter next;
    Filter DEFAULT = new Filter() {
        @Override
        public RpcResponse prefilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object postfilter(RpcRequest rpcRequest, RpcResponse rpcResponse,Object result) {
            return result;
        }
    };
}
