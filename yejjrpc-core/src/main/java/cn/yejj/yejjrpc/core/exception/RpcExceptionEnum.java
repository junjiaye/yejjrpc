package cn.yejj.yejjrpc.core.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @program: yejjrpc
 * @ClassName: YeJJExceptionEnum
 * @description:
 * @author: yejj
 * @create: 2024-03-28 21:28
 */
@AllArgsConstructor
@NoArgsConstructor
public enum RpcExceptionEnum {

    NOMETHOD_EX("Y001","method_not_exists"),
    SOUCKET_TIMEOUT_EX("Y001","http_invoke_timeout"),
    UNKNOWN_EX("Z001","unknown");

    public String code;
    public String msg;

}
