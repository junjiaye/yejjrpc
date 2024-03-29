package cn.yejj.yejjrpc.core.api;

import lombok.Data;
import lombok.ToString;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Data
@ToString
public class RpcRequest {
    private String service;
   // private String method;
    private String methodSign;
    private Object[] args;
}
