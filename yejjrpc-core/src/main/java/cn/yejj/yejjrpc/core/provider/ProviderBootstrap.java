package cn.yejj.yejjrpc.core.provider;

import cn.yejj.yejjrpc.core.annotation.YejjProvider;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    //字段set方法，相当于实现了接口的set....方法
    ApplicationContext applicationContext;

    private Map<String,Object> skeletonMap = new HashMap<>();

    @PostConstruct  //bean创建后。未初始化之前执行 相当于init-method 对应的销毁方法是@PreDestroy
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(YejjProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k));
        //   skeletonMap.putAll(providers);
        providers.values().forEach(x -> {
            genInterface(x);
        });
    }

    private void genInterface(Object x){
        Class<?> intf = x.getClass().getInterfaces()[0];
        skeletonMap.put(intf.getCanonicalName(), x);
    }

}
