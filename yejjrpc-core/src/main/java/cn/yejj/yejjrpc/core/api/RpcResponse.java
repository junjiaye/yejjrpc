package cn.yejj.yejjrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private boolean status;
    private Object result;

}
