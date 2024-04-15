package cn.yejj.yejjrpc.demo.provider;

import cn.yejj.yejjrpc.core.config.ProviderConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableApolloConfig
@Import(ProviderConfig.class)
public class YejjrpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(YejjrpcDemoProviderApplication.class, args);
    }
}
