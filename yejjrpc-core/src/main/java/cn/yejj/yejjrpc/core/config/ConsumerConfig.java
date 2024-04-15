package cn.yejj.yejjrpc.core.config;

import cn.yejj.yejjrpc.core.api.*;
import cn.yejj.yejjrpc.core.cluster.GrayRouter;
import cn.yejj.yejjrpc.core.cluster.RoundLoadbalancer;
import cn.yejj.yejjrpc.core.consumer.ConsumerBootstrap;
import cn.yejj.yejjrpc.core.filter.ParameterFilter;
import cn.yejj.yejjrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Slf4j
@Configuration
@Import({AppConfigProperties.class, ConsumerConfigProperties.class})
public class ConsumerConfig {

    @Autowired
    AppConfigProperties appConfigProperties;

    @Autowired
    ConsumerConfigProperties consumerConfigProperties;
    @Bean
    ConsumerBootstrap consumerBootstrap(){
        return new ConsumerBootstrap();
    }

    /**
     * 项目启动后，创建动态代理对象
     * @param consumerBootstrap
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)  //设置优先级最高
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap){
        return args -> {
            log.info("===============================consumerBootstrap_runner  start===============================");
            consumerBootstrap.start();
        };
    }

    @Bean
    public LoaderBalancer loaderBalacer(){
        //return LoaderBalacer.Defaut;
        return new RoundLoadbalancer();
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    public RegistryCenter registryCenter(){
        //return new RegistryCenter.StaticRegistryCenter(List.of(serivces.split(",")));
        return new ZkRegistryCenter();
    }

    @Bean
    public Router router(){
        return new GrayRouter(consumerConfigProperties.getGrayRatio());
    }

//    @Bean
//    public Filter cacheFilter(){
//        return new CacheFilter();
//    }
    @Bean
    public Filter paramFilter(){
        return new ParameterFilter();
    }
//    @Bean
//    public Filter mockFiler(){
//        return new MockFilter();
//    }

    @Bean
    @RefreshScope
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired LoaderBalancer loadBalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoaderBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appConfigProperties.getId());
        context.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        context.getParameters().put("app.env", appConfigProperties.getEnv());
        context.getParameters().put("consumer.retries", String.valueOf(consumerConfigProperties.getRetries()));
        context.getParameters().put("consumer.timeout", String.valueOf(consumerConfigProperties.getTimeout()));
        context.getParameters().put("consumer.faultLimit", String.valueOf(consumerConfigProperties.getFaultLimit()));
        context.getParameters().put("consumer.halfOpenInitialDelay", String.valueOf(consumerConfigProperties.getHalfOpenInitialDelay()));
        context.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerConfigProperties.getHalfOpenDelay()));
        return context;
    }
}
