package cn.yejj.yejjrpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap(){
        return new ProviderBootstrap();
    }
}
