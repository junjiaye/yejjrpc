package cn.yejj.yejjrpc.core.exception;

import lombok.Data;

/**
 * @program: yejjrpc
 * @ClassName: YeJJRpcException
 * @description:
 * @author: yejj
 * @create: 2024-03-27 22:39
 */
@Data
public class RpcException extends RuntimeException {
    private String errorCode;
    private String msg;

    public RpcException(Throwable cause, RpcExceptionEnum errorInfo) {
        super(cause);
        this.errorCode = errorInfo.code;
        this.msg = errorInfo.msg;
    }


}
