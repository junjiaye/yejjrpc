package cn.yejj.yejjrpc.core.provider;

import cn.yejj.yejjrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 *
 */
@Configuration
@Slf4j
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap(){
        return new ProviderBootstrap();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap){
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap){
        return args -> {
            log.info("===============================providerBootstra_runner  start===============================");
            providerBootstrap.start();
        };
    }
    @Bean//(initMethod = "start",destroyMethod = "stop")
    ZkRegistryCenter zkRegistryCenter(){
        return new ZkRegistryCenter();
    }



}
