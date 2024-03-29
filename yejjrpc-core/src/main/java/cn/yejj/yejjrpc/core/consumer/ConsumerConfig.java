package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.api.Filter;
import cn.yejj.yejjrpc.core.api.LoaderBalacer;
import cn.yejj.yejjrpc.core.api.RegistryCenter;
import cn.yejj.yejjrpc.core.api.Router;
import cn.yejj.yejjrpc.core.cluster.RoundLoadbalancer;
import cn.yejj.yejjrpc.core.filter.CacheFilter;
import cn.yejj.yejjrpc.core.filter.MockFilter;
import cn.yejj.yejjrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Slf4j
@Configuration
public class ConsumerConfig {

    @Value("${yejjrpc.providers}")
    String serivces;
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
    public LoaderBalacer loaderBalacer(){
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
        return Router.Default;
    }

//    @Bean
//    public Filter cacheFilter(){
//        return new CacheFilter();
//    }
//    @Bean
//    public Filter mockFiler(){
//        return new MockFilter();
//    }
}
