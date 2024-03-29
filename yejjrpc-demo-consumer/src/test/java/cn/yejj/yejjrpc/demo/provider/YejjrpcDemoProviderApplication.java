package cn.yejj.yejjrpc.demo.provider;

import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.provider.ProviderConfig;
import cn.yejj.yejjrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class YejjrpcDemoProviderApplication {
    @Autowired
    ProviderInvoker providerInvoker;
    public static void main(String[] args) {
        SpringApplication.run(YejjrpcDemoProviderApplication.class, args);
    }



    //使用HTTP + JSON 实现序列化和通信
    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invokeRequest(request);
    }


    //ApplicationRunner 用于在Spring Boot应用程序启动后执行一些特定的代码
    @Bean
    ApplicationRunner providerRunner() {
        return args -> {

        };
    }


}
