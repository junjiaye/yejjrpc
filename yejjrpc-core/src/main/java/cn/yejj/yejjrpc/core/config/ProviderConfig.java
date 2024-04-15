package cn.yejj.yejjrpc.core.config;

import cn.yejj.yejjrpc.core.provider.ProviderBootstrap;
import cn.yejj.yejjrpc.core.provider.ProviderInvoker;
import cn.yejj.yejjrpc.core.registry.zk.ZkRegistryCenter;
import cn.yejj.yejjrpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 *
 */
@Configuration
@Slf4j
@Import({AppConfigProperties.class,ProviderConfigProperties.class,SpringBootTransport.class})
public class ProviderConfig {
    @Value("${server.port:8080}")
    private String port;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "apollo.bootstrap", value = "enabled")
    ApolloChangedListener provider_apolloChangedListener() {
        return new ApolloChangedListener();
    }
    @Autowired
    AppConfigProperties appConfigProperties;

    @Autowired
    ProviderConfigProperties providerConfigProperties;

    @Bean
    ProviderBootstrap providerBootstrap(){
        return new ProviderBootstrap(port, appConfigProperties, providerConfigProperties);
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
