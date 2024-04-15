package cn.yejj.yejjrpc.core.api;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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
    //方法入参
    private Object[] args;
    // 跨调用方需要传递的参数（额外的参数，非方法入参）
    private Map<String,String> params = new HashMap<>();
}
