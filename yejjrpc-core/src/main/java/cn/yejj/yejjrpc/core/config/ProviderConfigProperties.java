package cn.yejj.yejjrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: yejjrpc
 * @ClassName: ProviderConfigProperties
 * @description:
 * @author: yejj
 * @create: 2024-04-06 21:20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "yejjrpc.provider")
public class ProviderConfigProperties {
    //将yejjrpc.provider配置自动放到map中
    Map<String,String> metas = new HashMap<>();
}
