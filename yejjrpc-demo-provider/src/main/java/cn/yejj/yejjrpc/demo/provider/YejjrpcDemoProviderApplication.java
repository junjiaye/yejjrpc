package cn.yejj.yejjrpc.demo.provider;

import cn.yejj.yejjrpc.core.annotation.YejjProvider;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.provider.ProviderBootstrap;
import cn.yejj.yejjrpc.core.provider.ProviderConfig;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;



@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class YejjrpcDemoProviderApplication {
    @Autowired
    ProviderBootstrap providerBootstrap;
    public static void main(String[] args) {
        SpringApplication.run(YejjrpcDemoProviderApplication.class, args);
    }



    //使用HTTP + JSON 实现序列化和通信
    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invokeRequest(request);
    }


    //ApplicationRunner 用于在Spring Boot应用程序启动后执行一些特定的代码
    @Bean
    ApplicationRunner providerRunner() {
        return args -> {
            RpcRequest request = new RpcRequest();
            request.setService("cn.yejj.yejjrpc.demo.api.UserService");
            //request.setMethod("toString");
           // request.setArgs(new Object[]{100});
            //RpcResponse response = invoke(request);
            //System.out.println(response.getResult());

            request.setMethodSign("a719a143601633a83c88ccad0068a1eb");
            request.setArgs(new Object[]{100});
            RpcResponse response1 = invoke(request);
            System.out.println(response1.getResult());
        };
    }


}
