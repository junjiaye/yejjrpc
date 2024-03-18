package cn.yejj.yejjrpc.demo.consumer;

import cn.yejj.yejjrpc.core.annotation.YejjConsumer;
import cn.yejj.yejjrpc.core.consumer.ConsumerConfig;
import cn.yejj.yejjrpc.demo.api.User;
import cn.yejj.yejjrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class YejjrpcDemoConsumerApplication {
    @YejjConsumer
    UserService userService;
    public static void main(String[] args) {
        SpringApplication.run(YejjrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumer_runner(){
        return x->{
            User user = userService.findById(2);
            System.out.println(user);
            System.out.println(userService.findStrById());
            System.out.println(userService.findIntgerById(100));
            System.out.println(userService.findIntgerById(5000));
            System.out.println(userService.getId(1233L));
            System.out.println(userService.getId(user));
            System.out.println(userService.getId(user,10));

            System.out.println(userService.getName());
            System.out.println(userService.getName(100));
            Arrays.stream(userService.getId()).forEach(id-> System.out.println(id));
            System.out.println(userService.getId(new int[]{23,2,3}));
            System.out.println(userService.getId(new ArrayList<>(Arrays.asList(111,2,3))));

        };
    }

}
