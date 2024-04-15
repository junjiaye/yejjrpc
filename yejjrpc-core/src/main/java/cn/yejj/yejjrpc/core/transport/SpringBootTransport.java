package cn.yejj.yejjrpc.core.transport;

import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yejjrpc
 * @ClassName: SpringBootTransport
 * @description:
 * @author: yejj
 * @create: 2024-04-06 20:54
 */
@RestController
public class SpringBootTransport {
    @Autowired
    ProviderInvoker providerInvoker;


    //使用HTTP + JSON 实现序列化和通信
    @RequestMapping("/yejjrpc")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invokeRequest(request);
    }
}
