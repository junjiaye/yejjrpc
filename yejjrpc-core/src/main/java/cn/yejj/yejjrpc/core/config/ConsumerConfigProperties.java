package cn.yejj.yejjrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: yejjrpc
 * @ClassName: CustomerConfigProperties
 * @description:
 * @author: yejj
 * @create: 2024-04-06 21:23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "yejjrpc.consumer")
public class ConsumerConfigProperties {
    private int retries = 1;

    private int timeout = 1000;

    private int faultLimit = 10;

    private int halfOpenInitialDelay = 10_000;

    private int halfOpenDelay = 60_000;

    private int grayRatio = 0;
}
