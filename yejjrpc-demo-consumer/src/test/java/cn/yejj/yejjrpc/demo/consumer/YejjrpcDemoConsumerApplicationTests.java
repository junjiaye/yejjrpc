package cn.yejj.yejjrpc.demo.consumer;

import cn.yejj.yejjrpc.core.test.TestZKServer;
import cn.yejj.yejjrpc.demo.provider.YejjrpcDemoProviderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
@SpringBootTest(classes = YejjrpcDemoConsumerApplication.class)
class YejjrpcDemoConsumerApplicationTests {

    static ApplicationContext context1;
    static ApplicationContext context2;

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8094    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context1 = SpringApplication.run(YejjrpcDemoProviderApplication.class,
                "--server.port=8094", "--yejjrpc.zkServer=localhost:2182",
                "--logging.level.cn.yejj.yejjrpc=info");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8095    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context2 = SpringApplication.run(YejjrpcDemoConsumerApplication.class,
                "--server.port=8095", "--yejjrpc.zkServer=localhost:2182",
                "--logging.level.cn.yejj.yejjrpc=info");
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> aaaa  .... ");
    }

    @AfterAll
    static void destory() {
        SpringApplication.exit(context1, () -> 1);
        SpringApplication.exit(context2, () -> 1);
        zkServer.stop();
    }

}
