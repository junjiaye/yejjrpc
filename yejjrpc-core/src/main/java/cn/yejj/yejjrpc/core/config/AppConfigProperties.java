package cn.yejj.yejjrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: yejjrpc
 * @ClassName: AppConfigProperties
 * @description:
 * @author: yejj
 * @create: 2024-04-06 21:22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "yejjrpc.app")
public class AppConfigProperties {
    //instance配置信息


    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";
}
