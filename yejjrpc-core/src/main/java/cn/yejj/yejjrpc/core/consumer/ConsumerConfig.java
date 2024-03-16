package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.provider.ProviderBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Configuration
public class ConsumerConfig {
    @Bean
    ConsumerBootstrap consumerBootstrap(){
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)  //设置优先级最高
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap){
        return args -> {
            System.out.println("===============================consumerBootstrap_runner  start===============================");
            consumerBootstrap.start();
        };
    }
}
